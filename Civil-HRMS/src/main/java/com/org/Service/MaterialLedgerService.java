package com.org.Service;

import java.util.List;

import com.org.DTO.SiteBalanceReport;
import com.org.Entity.MaterialTransaction;

public interface MaterialLedgerService {

	void logTransaction(MaterialTransaction transaction);
    List<MaterialTransaction> getAllTransactions();
    List<SiteBalanceReport> computeBalancesMatrix(String materialSku);
    
    List<String> getAllRegisteredSites();
    void registerSite(String siteName);
    
    List<String> getAllRegisteredMaterials();
    void registerMaterial(String materialSku);
    
	boolean deleteTransactionById(String transactionId);


}
