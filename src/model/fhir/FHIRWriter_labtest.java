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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class FHIRWriter_labtest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	}

	public static void collect_diagnosis(String input,String outdir) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		String line = null;
		HashSet<String> clinic_number_set = new HashSet<>();
		HashSet<String> encounter_set = new HashSet<>();
		HashMap<String, HashSet<String>> local_test_map = new HashMap<>();
		HashMap<String, HashMap<String, HashMap<String, String>>> standard_test_map = new HashMap<>();
		ArrayList<String> patient_list = new ArrayList<>();
		HashMap<String, String> description_map = new HashMap<>();
		while ((line = br.readLine()) != null) {
			
			String[] elements = line.split("\t");
			String clinic_number = elements[0].trim();
			String local_lab_test_code = elements[1].trim();
			String local_lab_description = elements[2].trim();
			String encounter = elements[3].trim();
			String order=elements[4];
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(order);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );
			
			String standard_lab_test_code = "";
			String standard_lab_test_description = "";
			String standard_lab_test_system = "";
			String lab_test_result = "";
			// System.out.println(line);
			
			
			if (elements.length >= 8) {
				standard_lab_test_code = elements[5].trim();
				standard_lab_test_description = elements[6].trim();
				standard_lab_test_system = elements[7].trim();
				lab_test_result = elements[8].trim();
			}

			if (!patient_list.contains(clinic_number)) {
				patient_list.add(clinic_number);
			}

			// lab_order_time.add(order); // 90% order are too much
			clinic_number_set.add(clinic_number);
			encounter_set.add(encounter);
			if (local_test_map.containsKey(local_lab_test_code)) {
				// local_test_map.get(local_lab_test_code).add(order);
				local_test_map.get(local_lab_test_code).add(clinic_number);
			} else {
				HashSet<String> set = new HashSet<>();
				// set.add(order);
				set.add(clinic_number);
				local_test_map.put(local_lab_test_code, set);
			}
			if (elements.length >= 8) {
				if (standard_lab_test_system.equals("LOINC") && standard_lab_test_code.length() > 1) {
					description_map.put(standard_lab_test_code, standard_lab_test_description);
					if (standard_test_map.containsKey(standard_lab_test_code)) {
						// standard_test_encounter_map.get(standard_lab_test_code).add(encounter);
						if (standard_test_map.get(standard_lab_test_code).containsKey(clinic_number)) {
							standard_test_map.get(standard_lab_test_code).get(clinic_number).put(formatted_date, lab_test_result);
						} else {
							HashMap<String,String> map = new HashMap<>();
							map.put(formatted_date, lab_test_result);
							standard_test_map.get(standard_lab_test_code).put(clinic_number, map);
						}

					} else {
						// HashSet<String> set_tmp=new HashSet<>();
						// set_tmp.add(encounter);
						// standard_test_encounter_map.put(standard_lab_test_code,
						// set_tmp);
						HashMap<String,String> set = new HashMap<>();
						set.put(formatted_date, lab_test_result);
						HashMap<String, HashMap<String,String>> map = new HashMap<>();
						map.put(clinic_number, set);
						standard_test_map.put(standard_lab_test_code, map);
					}
				}
			}
		}

		int rate = (int) (clinic_number_set.size() * 0.1);

		HashSet<String> local_code_list = new HashSet<>();
		ArrayList<String> standard_code_list = new ArrayList<>();

		for (Entry<String, HashSet<String>> entry : local_test_map.entrySet()) {
			if (entry.getValue().size() > rate) {
				local_code_list.add(entry.getKey());
			}
		}

		for (Entry<String, HashMap<String, HashMap<String,String>>> entry : standard_test_map.entrySet()) {
			if (entry.getValue().size() >= rate) {
				if (!standard_code_list.contains(entry.getKey())) {
					standard_code_list.add(entry.getKey());
				}
			}
		}
		System.out.println("encounter: " + encounter_set.size());
		System.out.println("rate :" + rate + " all: " + clinic_number_set.size());
		System.out.println("standard test code all: " + standard_test_map.size());
		System.out.println("standard test code filtered: " + standard_code_list.size());

		
		HashMap<String,HashMap<String,LabtestBean>> labtest_map=new HashMap<>();
		
		for (int i = 0; i < patient_list.size(); i++) {
			StringBuffer sb_1 = new StringBuffer();
			HashMap<String,LabtestBean > local_labtest_map=new HashMap<>();
			labtest_map.put(patient_list.get(i), local_labtest_map);
			for (int j = 0; j < standard_code_list.size(); j++) {
				// System.out.println(id_code_map.get(id_list.get(i)).get(code_list.get(j)));
				if (standard_test_map.get(standard_code_list.get(j)).containsKey(patient_list.get(i))) {

					Double mean_value = 0.0;
					HashMap<String, Integer> value_counter = new HashMap<>();
					int is_numeric = 0;
					int is_categorical = 0;
					
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");  
					long min=Long.MAX_VALUE;
					String latest_value="";
					String latest_date="";
					
					for (Entry<String,String> value : standard_test_map.get(standard_code_list.get(j)).get(patient_list.get(i)).entrySet()) {
						if(value.equals("")||value.equals("-")||value.equals(".")){
							continue;
						}
						String pattern = "MM/dd/yyyy";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

						String date = simpleDateFormat.format(new Date());
						Date d1 = format.parse(date);
			            Date d2 = format.parse(value.getKey());
						
			            long diff=Math.abs(d1.getTime() - d2.getTime());
			            if(diff<min){
			            	latest_date=d2.toString();
			            	min=diff;
			            	latest_value=value.getValue();
			            }
					}
					LabtestBean bean =new LabtestBean();
					bean.setCode(standard_code_list.get(j));
					bean.setValue(latest_value);
					bean.setDate(latest_date);
					local_labtest_map.put(standard_code_list.get(j), bean);
				}

			}
		}
		new File(outdir).mkdirs();
			
		writeFHIR_labtest(patient_list,
				labtest_map,
				description_map,
				 outdir) ;

	}
	
	
	
	public static void writeFHIR_labtest(ArrayList<String> patientList,
			HashMap<String, HashMap<String, LabtestBean>> data_labtests,
			HashMap<String,String> labtest_descrtiption,
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
			
			ArrayList<Observation> list_observation=new ArrayList();
			if(data_labtests.containsKey(id)){
				for(Entry<String, LabtestBean> entry:data_labtests.get(id).entrySet()){
					Observation observation=new Observation();
					bundle.addEntry().setResource(observation);
					observation.setValue(new StringType(entry.getValue().getValue()));
						CodeableConcept concept=new CodeableConcept();
							concept.setText(labtest_descrtiption.get(entry.getValue().getCode().toLowerCase()));
								Coding coding=new Coding();
								coding.setSystem("https://fhir.loinc.org/CodeSystem/?url=http://loinc.org");
								coding.setCode(entry.getKey());
							concept.addCoding(coding);
					observation.setCode(concept);
					list_observation.add(observation);
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
	
	
	
}
