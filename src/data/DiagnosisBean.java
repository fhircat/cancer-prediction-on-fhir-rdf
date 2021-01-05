package data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class DiagnosisBean {
	
	BigInteger id;  
	
	String date ;
	
	HashSet<String> icd;
	
	String billing_encountger;
	
	
	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public HashSet<String> getIcd() {
		return icd;
	}

	public void setIcd(HashSet<String> icd) {
		this.icd = icd;
	}

	public String getBilling_encountger() {
		return billing_encountger;
	}

	public void setBilling_encountger(String billing_encountger) {
		this.billing_encountger = billing_encountger;
	}

	

}
