package data.dataCollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONException;

import data.PatientBean;

public class GetPatientList {
	
	public static HashSet<PatientBean> getPatientNumberandNameFromGeneReports() throws IOException, JSONException, ParseException{
		HashSet<PatientBean> set=new HashSet();
		for(File file:new File("xml").listFiles()){
			PatientBean bean=PatientNumberandNameFromGeneReport(file.getAbsolutePath());
			set.add(bean);
		}
//		System.out.println(map);
		return set;
	}
	
	public static PatientBean PatientNumberandNameFromGeneReport(String xmlFile) throws IOException, JSONException, ParseException{
		
		PatientBean bean=new PatientBean();
		BigInteger number;
		String birthday;
		String first_name;
		String last_name;
		String file_name=xmlFile;
		String gene_report_date;
		String ordering_md_name;
		HashSet<String> genes=new HashSet<>();
		String submittedDiagnosis;
		String collDate;
		String specSite;
		try{
		Doc_FirstPage_Analysis_withxsd parser=new Doc_FirstPage_Analysis_withxsd(xmlFile);
		parser.parseContent(xmlFile);
		 number=parser.getClinic_number();
		 birthday= parser.getBirthday();
		 first_name=parser.getFirst_name();
		 last_name= parser.getLast_name();
		 gene_report_date=parser.getGene_report_date();
		 ordering_md_name=parser.getOrdering_md_name().toLowerCase().trim();
		 genes.addAll(parser.getAlteration_map().keySet());
		 genes.addAll(parser.getVUS_map().keySet());
		 submittedDiagnosis=parser.getSubmittedDiagnosis();
		 collDate=parser.getColldate();
		 specSite=parser.getSpecSite();
		}catch (Exception e){
		Doc_FirstPage_Analysis_withoutxsd parser=new Doc_FirstPage_Analysis_withoutxsd(xmlFile);
		parser.parseContent(xmlFile);
		 number=parser.getClinic_number();
		 birthday= parser.getBirthday();
		 first_name=parser.getFirst_name();
		 last_name= parser.getLast_name();
		 gene_report_date=parser.getGene_report_date();
		 ordering_md_name=parser.getOrdering_md_name().toLowerCase().trim();
		 genes.addAll(parser.getAlteration_map().keySet());
		 genes.addAll(parser.getVUS_map().keySet());
		 submittedDiagnosis=parser.getSubmittedDiagnosis();
		 collDate=parser.getCollDate();
		 specSite=parser.getSpecSite();
		}
		bean.setCollDate(collDate);
		bean.setSpecSite(specSite);
		bean.setBrithday(birthday);
		bean.setClinical_number(number);
		bean.setFirst_name(first_name);
		bean.setLast_name(last_name);
		bean.setFile_name(file_name);
		bean.setGeneReportDate(gene_report_date);
		bean.setOrdering_md_name(ordering_md_name);
		
		bean.setGenes(genes);
		bean.setSubmittedDiagnosis(submittedDiagnosis.toLowerCase());
		return bean;
	}

}
