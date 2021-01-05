package data.parser;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import data.Prediciton_PatientBean;

public class Parser_patientList {
	public static HashMap<BigInteger,Prediciton_PatientBean> readPatient(String patientList_file,  
			HashMap<String,HashMap<String,String>> subtypes) throws IOException{
		HashMap<BigInteger,Prediciton_PatientBean> patient_map=new HashMap<>();
		Reader reader = Files.newBufferedReader(Paths.get(patientList_file));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);  
		List<CSVRecord> csvRecords=csvParser.getRecords();
		CSVRecord header=csvRecords.get(0);
		
		int k=0;
		for (CSVRecord csvRecord : csvRecords){ 
			k++;
			List new_csvRecord=new ArrayList();
			if(k==1){
				
			}else{
				String mrn="NULL";
				if(!csvRecord.get(0).equals("NULL")){
					Prediciton_PatientBean bean =new Prediciton_PatientBean();
					BigInteger id=new BigInteger(csvRecord.get(0));  
					
					
					String file_id=csvRecord.get(1);
					String submistted_diagnosis=csvRecord.get(7);
					String gender=csvRecord.get(8);
					String dob=csvRecord.get(9);
					String gene_date=csvRecord.get(18);
					String submitted_general="0.0";
					String general_type_icd9="199";
					String general_type_icd10="C80";
					
					
					if(subtypes.containsKey(submistted_diagnosis)){
						general_type_icd9=	subtypes.get(submistted_diagnosis).get("icd9");
						general_type_icd10=	subtypes.get(submistted_diagnosis).get("icd10");
						submitted_general=subtypes.get(submistted_diagnosis).get("general");
					}
					bean.setClinical_number(id);
					bean.setFile_name(file_id);
					bean.setSubmittedDiagnosis(submistted_diagnosis);
					bean.setSex(gender);
					bean.setBrithday(dob);
					bean.setGeneReportDate(gene_date);
					bean.setGeneral_diagnosis_icd9(general_type_icd9);
					bean.setGeneral_diagnosis_icd10(general_type_icd10);
					bean.setSubmittedDiagnosis_general(submitted_general);
					patient_map.put(id, bean);
				}
			}
		}
		System.out.println("patient_map size: "+patient_map.size());
		
		HashMap<String,Integer> counter=new HashMap<>();
		for(Entry<BigInteger,Prediciton_PatientBean> entry:patient_map.entrySet()){
			String Diagnosis=entry.getValue().getSubmittedDiagnosis_general();
			if(counter.containsKey(Diagnosis)){
				counter.put(Diagnosis, counter.get(Diagnosis)+1);
			}else{
				counter.put(Diagnosis, 1);
			}
		}
		for(Entry<String,Integer> entry:counter.entrySet()){
			System.err.println(entry.getKey()+"\t"+entry.getValue());
		}
		
		return patient_map;
	}
	
	
	
	
	public static HashMap<BigInteger,HashMap<String,String>> readPatient_sexdob(String patientList_file) throws IOException, ParseException{
		HashMap<BigInteger,HashMap<String,String>> patient_map=new HashMap<>();
		Reader reader = Files.newBufferedReader(Paths.get(patientList_file));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);  
		List<CSVRecord> csvRecords=csvParser.getRecords();
		CSVRecord header=csvRecords.get(0);
		
		int k=0;
		for (CSVRecord csvRecord : csvRecords){ 
			k++;
			List new_csvRecord=new ArrayList();
			if(k==1){
				
			}else{
				String mrn="NULL";
				if(!csvRecord.get(0).equals("NULL")){
					Prediciton_PatientBean bean =new Prediciton_PatientBean();
					BigInteger id=new BigInteger(csvRecord.get(0));  
					
					
					String file_id=csvRecord.get(1);
					String submistted_diagnosis=csvRecord.get(7);
					String gender=csvRecord.get(8);
					String dob=csvRecord.get(9);
					String gene_date=csvRecord.get(18);
				
					SimpleDateFormat dateFormat_1 = new SimpleDateFormat("yyyy-mm-dd");
					Date convertedCurrentDate = dateFormat_1.parse(dob);
					SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
					String formatted_date=dateFormat_2.format(convertedCurrentDate );
					
					
					HashMap<String,String> local_map=new HashMap<>();
					local_map.put("sex", gender);
					local_map.put("dob", formatted_date);
					
					patient_map.put(id, local_map);
				}
			}
		}
		System.out.println("patient_map size: "+patient_map.size());
		
		
		return patient_map;
	}
	
	
}
