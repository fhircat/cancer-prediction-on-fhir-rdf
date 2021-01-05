package data;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;

public class PatientBean {
	String sex;
	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	String collDate;
	public String getCollDate() {
		return collDate;
	}
	public void setCollDate(String collDate) {
		this.collDate = collDate;
	}
	public String getSpecSite() {
		return specSite;
	}
	public void setSpecSite(String specSite) {
		this.specSite = specSite;
	}
	String specSite;
	
	
	String submittedDiagnosis;
	public String getSubmittedDiagnosis() {
		return submittedDiagnosis;
	}
	public void setSubmittedDiagnosis(String submittedDiagnosis) {
		this.submittedDiagnosis = submittedDiagnosis;
	}
	String first_name;
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getBrithday() {
		return brithday;
	}
	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}
	public BigInteger getClinical_number() {
		return clinical_number;
	}
	public void setClinical_number(BigInteger clinical_number) {
		this.clinical_number = clinical_number;
	}
	String last_name;
	String brithday;
	BigInteger clinical_number;
	String file_name;
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	String geneReportDate;
	
	
	String diagnosisDate;
	public String getDiagnosisDate() {
		return diagnosisDate;
	}
	public void setDiagnosisDate(String diagnosisDate) {
		this.diagnosisDate = diagnosisDate;
	}
	public String getLabTestDate() {
		return labTestDate;
	}
	public void setLabTestDate(String labTestDate) {
		this.labTestDate = labTestDate;
	}
	String labTestDate;
	String general_diagnosis;
	
	public String getGeneReportDate() {
		return geneReportDate;
	}
	public void setGeneReportDate(String geneReportDate) {
		this.geneReportDate = geneReportDate;
	}
	
	String ordering_md_name;
	public String getOrdering_md_name() {
		return ordering_md_name;
	}
	public void setOrdering_md_name(String ordering_md_name) {
		this.ordering_md_name = ordering_md_name;
	}

	HashSet<String> genes;
	public HashSet<String> getGenes() {
		return genes;
	}
	public void setGenes(HashSet<String> genes) {
		this.genes = genes;
	}
	
	
}
