package model.fhir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.codesystems.AdministrativeGenderEnumFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IntegerDt;
import ca.uhn.fhir.parser.IParser;
import data.LabtestBean;
import data.Prediciton_PatientBean;

public class FHIRWriter_diagnosis {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	}

	
	public static void collect_diagnosis(String patientList_file, String tableFile,
			String outFile1,String outFile2) throws Exception{
		
		HashSet<BigInteger> mrn_set=new HashSet<>();
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
					mrn_set.add(new BigInteger(csvRecord.get(0)));
				}
			}
		}
		
		System.out.println("foundation_clinInfo_nansu_refined.csv -> "+mrn_set.size());
		
		
		BufferedReader br =new BufferedReader(new FileReader(new File(tableFile)));
		String line=null;
		HashMap<String,HashMap<String,Integer>> id_code_map=new HashMap<String, HashMap<String,Integer>>();
		HashMap<String,HashMap<String,Boolean>> code_id_map=new HashMap<String, HashMap<String,Boolean>>();
		while((line=br.readLine())!=null){
			String[] elements=line.split("\t");
			String id=elements[0].trim();
			String code=elements[2].trim();
			if(!mrn_set.contains(new BigInteger(id))){
				continue;
			}
			
			if(id_code_map.containsKey(id)){
				if(id_code_map.get(id).containsKey(code)){
					id_code_map.get(id).put(code, id_code_map.get(id).get(code)+1);
				}else{
					id_code_map.get(id).put(code, 1);
				}
				
			}else{
				HashMap<String,Integer> map=new HashMap<>();
				map.put(code, 1);
				id_code_map.put(id, map);
			}
			
		}
//		System.out.println(id_code_map);
		
		for(Entry<String,HashMap<String,Integer>> entry_1:id_code_map.entrySet()){
			for(Entry<String,Integer> entry_2:entry_1.getValue().entrySet()){
				
				
				if(code_id_map.containsKey(entry_2.getKey())){
					if(entry_2.getValue()>3){
						code_id_map.get(entry_2.getKey()).put(entry_1.getKey(), true);	
					}else{
						code_id_map.get(entry_2.getKey()).put(entry_1.getKey(), false);
					}
					
				}else{
					HashMap<String,Boolean> map=new HashMap<>();
					if(entry_2.getValue()>3){
						map.put(entry_1.getKey(), true);	
					}else{
						map.put(entry_1.getKey(), false);
					}
					code_id_map.put(entry_2.getKey(), map);
				}
			}
		}
		
		ArrayList<String> code_list=new ArrayList<>();
		ArrayList<String> id_list=new ArrayList<>();
		HashSet<String> all_code=new HashSet<>();
		
		for(Entry<String,HashMap<String,Boolean>> entry_1:code_id_map.entrySet()){
			int true_count=0;
			int false_count=0;
			for(Entry<String,Boolean> entry_2:entry_1.getValue().entrySet()){
				if(entry_2.getValue()){
					true_count++;
				}else{
					false_count++;
				}
			}
			all_code.add(entry_1.getKey());
			if(true_count>=10||false_count>=10){ // original is 200
				if(!code_list.contains(entry_1.getKey())){
					code_list.add(entry_1.getKey());			
				}
			}
//			System.out.println(entry_1.getKey()+" "+true_count+" "+false_count);
		}
		
		for(Entry<String,HashMap<String,Integer>> entry:id_code_map.entrySet()){
			if(!id_list.contains(entry.getKey())){
				id_list.add(entry.getKey());	
			}
		}
		
		
		System.out.println("all code size: "+all_code.size());
		System.out.println("code_list size: "+code_list.size());
		System.out.println("id_list size: "+id_list.size());
		
		
		HashMap<String,HashSet<String>> icd_phecode_map=readICDTOPHECODE("phecode_icd9_map_unrolled.csv");
		HashMap<String,String> phecode_description_map=readPHECODEDESCRIPTION("phecode_definitions1.2.csv");
		ArrayList<String> phecode_list=new ArrayList<>();
		for(String string:code_list){
			if(icd_phecode_map.containsKey(string)){
				for(String p_code:icd_phecode_map.get(string)){
					if(!phecode_list.contains(p_code)){
						phecode_list.add(p_code);
					}
				}
			}
		}
		System.out.println("phecode_list size: "+phecode_list.size());
		
		
		HashMap<String,HashMap<String,Integer>> id_phecode_map=new HashMap<String, HashMap<String,Integer>>();
		
		for(Entry<String,HashMap<String,Integer>> entry_1:id_code_map.entrySet()){
			HashMap<String,Integer> map_tmp=new HashMap<>();
			for(Entry<String,Integer> entry_2:entry_1.getValue().entrySet()){
				if(icd_phecode_map.containsKey(entry_2.getKey())){
					for(String p_code:icd_phecode_map.get(entry_2.getKey())){

						if(map_tmp.containsKey(p_code)){
							map_tmp.put(p_code, map_tmp.get(p_code)+entry_2.getValue());
						}else{
							map_tmp.put(p_code, entry_2.getValue());
						}
						
					}
				}
				
			}
			
			id_phecode_map.put(entry_1.getKey(), map_tmp);
		}
		
		new File(outFile1).mkdirs();
		new File(outFile2).mkdirs();
		writeFHIR_diagnosis(id_list,
				id_code_map,new HashMap<String,String>(), "icd9",
				outFile1);
		writeFHIR_diagnosis(id_list,
				id_phecode_map,phecode_description_map,"phecode",
				outFile2);	
		

	}
	
	
	public static void writeFHIR_diagnosis(ArrayList<String> patientList,
			HashMap<String, HashMap<String,Integer>> data_diagnosis,
			HashMap<String,String> diagnosis_descrtiption, String code_type,
			String outdir) throws Exception {
		
		for(String id:patientList){
			
			Bundle bundle = new Bundle();
			bundle.setType(Bundle.BundleType.COLLECTION);
			
			// Add the patient as an entry. This entry is a POST with an 
			// If-None-Exist header (conditional create) meaning that it
			// will only be created if there isn't already a Patient with
			// the identifier 12345

			Patient patient	=new Patient();
			bundle.addEntry()
			   .setResource(patient);
			
			patient.setId(id.toString());
			
			ArrayList<Condition> list_codition=new ArrayList();
			
			if(data_diagnosis.containsKey(id)){
				for(Entry<String,Integer> entry :data_diagnosis.get(id).entrySet()){
					Condition condition=new Condition();
					bundle.addEntry().setResource(condition);
					CodeableConcept concept=new CodeableConcept();
					if(diagnosis_descrtiption.containsKey(entry.getKey())){
						concept.setText(diagnosis_descrtiption.get(entry.getKey()));	
					}
					Extension e=new Extension();
					e.setValue(new StringType(String.valueOf(entry.getValue())));
					e.setId("frequency");
					concept.addExtension(e);
					Coding coding=new Coding();
					if(code_type.equals("icd9")){
						coding.setSystem("http://hl7.org/fhir/sid/icd-9-cm");	
					}
					if(code_type.equals("phecode")){
						coding.setSystem("https://phewascatalog.org/phecodes");	
					}
					
					coding.setCode(entry.getKey());
					concept.addCoding(coding);
					condition.setCode(concept);
					list_codition.add(condition);
				}
			}
			
			FhirContext ctx = FhirContext.forR4();
			IParser parser = ctx.newJsonParser();
			// Indent the output
			parser.setPrettyPrint(true);
			// Serialize it
			String serialized = parser.encodeResourceToString(bundle);
//			System.out.println(serialized);
			FileWriter fw=new FileWriter(new File(outdir+"/"+id+".json"));
			fw.write(serialized);
			fw.flush();
			fw.close();
		}
		
	}

	
	
	public static HashMap<String,HashSet<String>> readICDTOPHECODE(String file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		int i=0;
		HashMap<String,HashSet<String>> map=new HashMap<>();
		while((line=br.readLine())!=null){
			i++;
			if(i==1){
				continue;
			}
			line=line.replaceAll("\"", "");
			String[] elements=line.split(",");
			
			if(map.containsKey(elements[0])){
				map.get(elements[0]).add(elements[1]);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(elements[1]);
				map.put(elements[0], set);
			}
		}
		return map;
	}
	
	public static HashMap<String,String> readPHECODEDESCRIPTION(String file) throws IOException{
		
		Reader reader = Files.newBufferedReader(Paths.get(file));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);  
		List<CSVRecord> csvRecords=csvParser.getRecords();
		HashMap<String,String> map=new HashMap<>();
		int i=0;
		for (CSVRecord csvRecord : csvRecords){ 
			i++;
			if(i>1){
				String code=csvRecord.get(0);
				String description=csvRecord.get(1).trim();
				map.put(code, description);	
			}
			
		}
		return map;
	}

	
}
