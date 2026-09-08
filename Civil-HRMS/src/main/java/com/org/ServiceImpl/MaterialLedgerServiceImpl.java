package com.org.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.org.DTO.SiteBalanceReport;
import com.org.Entity.MaterialSku;
import com.org.Entity.MaterialTransaction;
import com.org.Entity.ProjectSite;
import com.org.Enum.TransactionType;
import com.org.Repository.MaterialSkuRepository;
import com.org.Repository.MaterialTransactionRepository;
import com.org.Repository.ProjectSiteRepository;
import com.org.Service.MaterialLedgerService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialLedgerServiceImpl implements MaterialLedgerService{

	private final MaterialTransactionRepository txRepository;
    private final ProjectSiteRepository siteRepository;
    private final MaterialSkuRepository skuRepository;

    @Autowired
    public MaterialLedgerServiceImpl(MaterialTransactionRepository txRepository,
                                    ProjectSiteRepository siteRepository,
                                    MaterialSkuRepository skuRepository) {
        this.txRepository = txRepository;
        this.siteRepository = siteRepository;
        this.skuRepository = skuRepository;
    }

    @Override
    @Transactional
    public void logTransaction(MaterialTransaction transaction) {
        txRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialTransaction> getAllTransactions() {
        return txRepository.findAllByOrderByDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteBalanceReport> computeBalancesMatrix(String materialSku) {
        List<String> activeSites = getAllRegisteredSites();
        List<MaterialTransaction> allTx = txRepository.findAll();
        List<SiteBalanceReport> matrixOutput = new ArrayList<>();

        for (String site : activeSites) {
            SiteBalanceReport report = new SiteBalanceReport(site, materialSku);

            for (MaterialTransaction tx : allTx) {
                if (!tx.getMaterialSku().equalsIgnoreCase(materialSku)) {
                    continue;
                }

                if (tx.getType() == TransactionType.INWARD && tx.getDestinationLocation().equalsIgnoreCase(site)) {
                    report.addDirectInward(tx.getQuantity());
                } 
                else if (tx.getType() == TransactionType.CONSUMPTION  && tx.getSourceLocation().equalsIgnoreCase(site)) {
                    report.addDirectOutward(tx.getQuantity());
                } 
                else if (tx.getType() == TransactionType.SCRAP && tx.getSourceLocation().equalsIgnoreCase(site)) {
                    report.addScrapOutward(tx.getQuantity()); 
                } 
                else if (tx.getType() == TransactionType.TRANSFER) {
                    if (tx.getSourceLocation().equalsIgnoreCase(site)) {
                        report.addTransferOutward(tx.getQuantity());
                    }
                    if (tx.getDestinationLocation().equalsIgnoreCase(site)) {
                        report.addTransferInward(tx.getQuantity());
                    }
                }
            }
            report.calculateFinalBalance();
            matrixOutput.add(report);
        }
        return matrixOutput;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllRegisteredSites() {
        return siteRepository.findAll().stream()
                .map(ProjectSite::getName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registerSite(String siteName) {
        if (!siteRepository.existsByNameIgnoreCase(siteName)) {
            siteRepository.save(new ProjectSite(siteName));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllRegisteredMaterials() {
        return skuRepository.findAll().stream()
                .map(MaterialSku::getMaterialSku)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registerMaterial(String materialSku) {
        if (!skuRepository.existsByMaterialSkuIgnoreCase(materialSku)) {
            skuRepository.save(new MaterialSku(materialSku));
        }
    }
    
    @Override
    @Transactional
    public boolean deleteTransactionById(String transactionId) {
        try {
            // Convert the String ID from frontend into a Long
            Long id = Long.parseLong(transactionId);
            
            if (txRepository.existsById(id)) {
            	txRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            // Returns false if frontend accidentally sends a non-numeric string
            return false; 
        }
    }
}

