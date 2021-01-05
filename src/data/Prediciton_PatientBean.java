package data;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Prediciton_PatientBean {
	String sex;
	String submittedDiagnosis;
	String submittedDiagnosis_general;
	
	
	String brithday;
	BigInteger clinical_number;
	String file_name;
	String geneReportDate;
	HashSet<String> ordering_md_name;
	HashSet<String> genes;

	HashSet<String> previous_diagnosis_code;
	HashMap<String,String> previous_labtest_averageValue;
	HashMap<String,String> previous_latest_averageValue;
	String general_diagnosis_icd9;
	String general_diagnosis_icd10;
	
	public String getGeneral_diagnosis_icd9() {
		return general_diagnosis_icd9;
	}
	public void setGeneral_diagnosis_icd9(String general_diagnosis_icd9) {
		this.general_diagnosis_icd9 = general_diagnosis_icd9;
	}
	public String getGeneral_diagnosis_icd10() {
		return general_diagnosis_icd10;
	}
	public void setGeneral_diagnosis_icd10(String general_diagnosis_icd10) {
		this.general_diagnosis_icd10 = general_diagnosis_icd10;
	}
	
	
	public HashSet<String> getPrevious_diagnosis_code() {
		return previous_diagnosis_code;
	}
	public void setPrevious_diagnosis_code(HashSet<String> previous_diagnosis_code) {
		this.previous_diagnosis_code = previous_diagnosis_code;
	}
	public HashMap<String, String> getPrevious_labtest_averageValue() {
		return previous_labtest_averageValue;
	}
	public void setPrevious_labtest_averageValue(HashMap<String, String> previous_labtest_averageValue) {
		this.previous_labtest_averageValue = previous_labtest_averageValue;
	}
	public HashMap<String, String> getPrevious_latest_averageValue() {
		return previous_latest_averageValue;
	}
	public void setPrevious_latest_averageValue(HashMap<String, String> previous_latest_averageValue) {
		this.previous_latest_averageValue = previous_latest_averageValue;
	}
	
	
	
	
	
	public String getSubmittedDiagnosis_general() {
		return submittedDiagnosis_general;
	}
	public void setSubmittedDiagnosis_general(String submittedDiagnosis_general) {
		this.submittedDiagnosis_general = submittedDiagnosis_general;
	}
	
	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	
	public String getSubmittedDiagnosis() {
		return submittedDiagnosis;
	}
	public void setSubmittedDiagnosis(String submittedDiagnosis) {
		this.submittedDiagnosis = submittedDiagnosis;
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
	
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	
	
	
	public String getGeneReportDate() {
		return geneReportDate;
	}
	public void setGeneReportDate(String geneReportDate) {
		this.geneReportDate = geneReportDate;
	}
	
	
	public HashSet<String> getOrdering_md_name() {
		return ordering_md_name;
	}
	public void setOrdering_md_name(HashSet<String> ordering_md_name) {
		this.ordering_md_name = ordering_md_name;
	}

	
	public HashSet<String> getGenes() {
		return genes;
	}
	public void setGenes(HashSet<String> genes) {
		this.genes = genes;
	}
	
	
}
