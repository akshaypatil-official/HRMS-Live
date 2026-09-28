package com.org.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.org.DTO.SiteBalanceReport;
import com.org.Entity.MaterialTransaction;

public interface MaterialLedgerService {

	void logTransaction(MaterialTransaction transaction);
    Page<MaterialTransaction> getAllTransactions(Pageable pageable);
    List<SiteBalanceReport> computeBalancesMatrix(String materialSku);
    
    List<String> getAllRegisteredSites();
    void registerSite(String siteName);
    
    List<String> getAllRegisteredMaterials();
    void registerMaterial(String materialSku);
    
	boolean deleteTransactionById(String transactionId);

}
