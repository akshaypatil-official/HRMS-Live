package com.org.DTO;

public class SiteBalanceReport {
	private String siteName;
    private String materialSku;
    private double directInwardQty;
    private double transferInwardQty;
    private double directOutwardQty;
    private double transferOutwardQty;
    private double currentAvailableBalance;
    private double scrapOutwardQty;

    
    public SiteBalanceReport() {
    }
    
    public SiteBalanceReport(String siteName, String materialSku) {
        this.siteName = siteName;
        this.materialSku = materialSku;
        this.directInwardQty = 0.0;
        this.transferInwardQty = 0.0;
        this.directOutwardQty = 0.0;
        this.transferOutwardQty = 0.0;
        this.currentAvailableBalance = 0.0;
        this.scrapOutwardQty = 0.0;

    }
    
    public void addDirectInward(double qty) { 
        this.directInwardQty += qty; 
    }
    
    public void addTransferInward(double qty) { 
        this.transferInwardQty += qty; 
    }
    
    public void addDirectOutward(double qty) { 
        this.directOutwardQty += qty; 
    }
    
    public void addTransferOutward(double qty) { 
        this.transferOutwardQty += qty; 
    }
    
    public void addScrapOutward(double quantity) {
        this.scrapOutwardQty += quantity;
    }  

    public void calculateFinalBalance() {
        this.currentAvailableBalance = (this.directInwardQty + this.transferInwardQty) - 
                                       (this.directOutwardQty + this.transferOutwardQty + this.scrapOutwardQty);
    }
    
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getMaterialSku() {
		return materialSku;
	}

	public void setMaterialSku(String materialSku) {
		this.materialSku = materialSku;
	}

	public double getDirectInwardQty() {
		return directInwardQty;
	}

	public void setDirectInwardQty(double directInwardQty) {
		this.directInwardQty = directInwardQty;
	}

	public double getTransferInwardQty() {
		return transferInwardQty;
	}

	public void setTransferInwardQty(double transferInwardQty) {
		this.transferInwardQty = transferInwardQty;
	}

	public double getDirectOutwardQty() {
		return directOutwardQty;
	}

	public void setDirectOutwardQty(double directOutwardQty) {
		this.directOutwardQty = directOutwardQty;
	}

	public double getTransferOutwardQty() {
		return transferOutwardQty;
	}

	public void setTransferOutwardQty(double transferOutwardQty) {
		this.transferOutwardQty = transferOutwardQty;
	}

	public double getCurrentAvailableBalance() {
		return currentAvailableBalance;
	}

	public void setCurrentAvailableBalance(double currentAvailableBalance) {
		this.currentAvailableBalance = currentAvailableBalance;
	}

	public double getScrapOutwardQty() {
		return scrapOutwardQty;
	}

	public void setScrapOutwardQty(double scrapOutwardQty) {
		this.scrapOutwardQty = scrapOutwardQty;
	}

    
    
}
