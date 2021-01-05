package model.fhir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


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
import ca.uhn.fhir.parser.IParser;

import data.LabtestBean;
import data.Prediciton_PatientBean;
import data.parser.Parser_diagnosis;
import data.parser.Parser_geneReport;
import data.parser.Parser_historical;
import data.parser.Parser_labtest;
import data.parser.Parser_medication;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class FHIRWriter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void update_others(HashMap<BigInteger,HashSet<String>> map, ArrayList<String> value_list){
		value_list.add("null");
		for(Entry<BigInteger,HashSet<String>> entry:map.entrySet()){
			for(String string:entry.getValue()){
				if(!value_list.contains(string)){
					value_list.add(string);
				}
			}
		}
	}
	
	public static void update_lab(HashMap<BigInteger, HashMap<String, LabtestBean>> map, ArrayList<String> value_list){
		HashMap<String,HashSet<String>> control_map=new HashMap<>();
		for(Entry<BigInteger,HashMap<String, LabtestBean>> entry_1:map.entrySet()){
			for(Entry<String, LabtestBean> entry_2:entry_1.getValue().entrySet()){
				LabtestBean bean=entry_2.getValue();
				String value=bean.getCode()+"("+bean.getValue()+")";
				
				if(control_map.containsKey(bean.getCode())){
					control_map.get(bean.getCode()).add(value);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(value);
					control_map.put(bean.getCode(), set);
				}
			}
		}
		
		for(Entry<String,HashSet<String>> entry:control_map.entrySet()){
			for(String string:entry.getValue()){
				value_list.add(string);
			}
			String value=entry.getKey()+"(null)";
			if(!value_list.contains(value)){
				value_list.add(value);
			}
		}
		
	}
	
	/**
	 * featureSourceType 1= genetic only
	 * 
	 * featureSourceType 2= labtest only
	 * 
	 * featureSourceType 3= diagnosis only
	 * 
	 * featureSourceType 4= medication only
	 *
	 * featureSourceType 5= historical only
	 * 
	 * 
	 * @param data_map
	 * @param type
	 * @param arffTrain
	 * @throws Exception
	 */
	public static void writeFHIR_all(ArrayList<BigInteger> patientList,
			HashMap<BigInteger, Prediciton_PatientBean> data_patient, 
			HashMap<BigInteger, String> data_sex,
			HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis,
			HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals,
			int reducted_dimension,
			HashMap<String,String> medication_descrtiption,
			HashMap<String,String> labtest_descrtiption,
			HashMap<String,String> diagnosis_descrtiption,
			HashMap<String,String> historical_descrtiption,
			String outdir) throws Exception {
		
		
		for(BigInteger id:patientList){
			
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
			
			if(data_sex.containsKey(id)){
				String sex=data_sex.get(id);
				if(sex.equals("female")){
					patient.setGender(AdministrativeGender.FEMALE);
				}else{
					patient.setGender(AdministrativeGender.MALE);
				}	
			}
			
			if(data_age.containsKey(id)){
				String age=data_age.get(id);
				Extension e=new Extension();
				e.setId("age");
				e.setValue(new StringType(age));
				patient.addExtension(e);
			}
			
			Observation_Genetics observation_genetics=new Observation_Genetics();
			bundle.addEntry()
			   .setResource(observation_genetics);
			if(data_genes.containsKey(id)){
				
				for(String string:data_genes.get(id)){
					observation_genetics.addGene(new StringType(string));
				}
			}
			
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
			
			ArrayList<Condition> list_codition=new ArrayList();
			
			if(data_diagnosis.containsKey(id)){
				for(String string :data_diagnosis.get(id)){
					Condition condition=new Condition();
					bundle.addEntry().setResource(condition);
					CodeableConcept concept=new CodeableConcept();
					concept.setText(diagnosis_descrtiption.get(string.toLowerCase()));
					Coding coding=new Coding();
					coding.setSystem("http://hl7.org/fhir/sid/icd-9-cm");
					coding.setCode(string);
					concept.addCoding(coding);
					condition.setCode(concept);
					list_codition.add(condition);
				}
			}
			
			ArrayList<Medication> list_medication=new ArrayList();
			
			if(data_medications.containsKey(id)){
				for(String string :data_medications.get(id)){
					Medication medication=new Medication();
					bundle.addEntry().setResource(medication);
					CodeableConcept concept=new CodeableConcept();
					concept.setText(medication_descrtiption.get(string.toLowerCase()));
					Coding coding=new Coding();
					coding.setSystem("http://www.nlm.nih.gov/research/umls/rxnorm");
					coding.setCode(string);
					concept.addCoding(coding);
					medication.setCode(concept);
					list_medication.add(medication);
				}
			}
			
			FamilyMemberHistory hisotry=new FamilyMemberHistory();
			bundle.addEntry().setResource(hisotry);
			if(data_historicals.containsKey(id)){
				for(String string :data_historicals.get(id)){
					FamilyMemberHistoryConditionComponent component=new FamilyMemberHistoryConditionComponent();
					CodeableConcept concept=new CodeableConcept();
					concept.setText(historical_descrtiption.get(string.toLowerCase()));
					Coding coding=new Coding();
					coding.setSystem("http://hl7.org/fhir/sid/icd-9-cm");
					coding.setCode(string);
					concept.addCoding(coding);
					component.setCode(concept);
					hisotry.addCondition(component);
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
	
	
	
	public static HashMap<BigInteger, String> readLabel(String label_file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(label_file)));
		String line=null;
		HashMap<BigInteger, String> map=new HashMap<>();
		
		while((line=br.readLine())!=null){
			String[] elements=line.split(" ");
			map.put(new BigInteger(elements[0]), elements[1]);
		}
		br.close();
		return map;
	}
	
	public static HashMap<BigInteger, String> readSex(String sex_file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(sex_file)));
		String line=null;
		HashMap<BigInteger, String> map=new HashMap<>();
		
		while((line=br.readLine())!=null){
			String[] elements=line.split(" ");
			map.put(new BigInteger(elements[0]), elements[1]);
		}
		br.close();
		return map;
	}
	
	public static HashMap<BigInteger, String> readAge(String age_file) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(age_file)));
		String line=null;
		HashMap<BigInteger, String> map=new HashMap<>();
		
		while((line=br.readLine())!=null){
			String[] elements=line.split(" ");
			map.put(new BigInteger(elements[0]), elements[1]);
		}
		br.close();
		return map;
	}
	
	public static HashMap<String, BigInteger> readnode_patient(String node_idx_file) throws IOException{
		
		HashMap<String, BigInteger> map=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(node_idx_file)));
		String line=null;
		
		while((line=br.readLine())!=null){
			String[] elements=line.split("\t");
//			System.out.println(line);
			if(line.contains("Patient")){
				String id=elements[1].substring(elements[1].indexOf("<")+1,elements[1].indexOf(">"));
				map.put(elements[0], new BigInteger(id));	
			}
			
		}
		br.close();
		return map;
	}
	
	
	public static HashMap<BigInteger, ArrayList<String>> readembedding(String embedding, HashMap<String,BigInteger> node_idex) throws IOException{
		System.out.println("read embedding: "+embedding);
		HashMap<BigInteger, ArrayList<String>> embeddings=new HashMap<>();
		HashMap<String, BigInteger> map=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(embedding)));
		String line=null;
		int counter=0;
		while((line=br.readLine())!=null){
			counter++;
			if(counter==1){
				continue;
			}
				
			String[] elements=line.split(" ");
			ArrayList<String> list=new ArrayList<>();
			for (int j = 1; j < elements.length; j++) {
				list.add(elements[j]);
			}
			if(node_idex.containsKey(elements[0])){
				embeddings.put(node_idex.get(elements[0]), list);	
			}
		}
		br.close();
		return embeddings;
	}

	
	/**
	 * featureSourceType 1= genetic only
	 * 
	 * featureSourceType 2= labtest only
	 * 
	 * featureSourceType 3= diagnosis only
	 * 
	 * featureSourceType 4= medication only
	 *
	 * featureSourceType 5= historical only
	 * 
	 * 
	 * @param data_map
	 * @param type
	 * @param arffTrain
	 * @throws Exception
	 */
	
	
	public static ArrayList<String> getAverage(HashMap<BigInteger, ArrayList<String>> embeddings){
		ArrayList<Double> average=new ArrayList<>();
		ArrayList<String> average_return=new ArrayList<>();
		int i=0;
		for(Entry<BigInteger, ArrayList<String>> entry:embeddings.entrySet()){
			
			i++;
			if(i==1){
				for (int j = 0; j < entry.getValue().size(); j++) {
					average.add(j, Double.valueOf(entry.getValue().get(j)));
				}
			}else{
				for (int j = 0; j < entry.getValue().size(); j++) {
					Double tmp=average.get(j)+Double.valueOf(entry.getValue().get(j));
					average.set(j, tmp);
				}
			}
		}
		
		for (int j = 0; j < average.size(); j++) {
			average_return.add(j,String.valueOf(average.get(j)/embeddings.size()));
		}
		
		
		return average_return;
	}
	
	
	public static void writeARFF_embedding(String sex_file, String age_file, String label_file, String node_idx_file,String embedding_file,
			String outfile_arff) throws Exception {
		
		HashMap<BigInteger, String> sex=readSex(sex_file) ;
		HashMap<BigInteger, String> age=readAge(age_file) ;
		HashMap<BigInteger, String> lable=readLabel(label_file) ;
		HashMap<String, BigInteger> node_idx=readnode_patient(node_idx_file);
		HashMap<BigInteger, ArrayList<String>> embeddings=readembedding( embedding_file, node_idx);
		ArrayList<String> average_embedding=getAverage(embeddings);
		int embedding_size=0;
		for(Entry<BigInteger,ArrayList<String>> entry:embeddings.entrySet()){
			embedding_size=entry.getValue().size();
			break;
		}
		
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile_arff)));
		bw.write("@relation 'associations'\n");
		
	
		bw.write("@attribute 'Sex' { 'female', 'male' } \n");
		bw.write("@attribute 'Age' { 'very_old', 'old' , 'middle_age' , 'senior' , 'junior' } \n");
		
		for (int i = 0; i < embedding_size; i++) {
			bw.write("@attribute 'Attribute_" + i + "' numeric \n");
		}

		ArrayList<String> all_tumor = new ArrayList<>();
		
		all_tumor.add("153.9"); //   Malignant neoplasm of colon, unspecified site
		all_tumor.add("155"); //    Malignant neoplasm of liver, primary
		all_tumor.add("162.9"); //    PMalignant neoplasm of bronchus and lung, unspecified
		all_tumor.add("157.9"); //    Malignant neoplasm of pancreas, part unspecified
		all_tumor.add("174.9"); //     Malignant neoplasm of breast (female), unspecified
		all_tumor.add("171.9"); //    Malignant neoplasm of connective and other soft tissue, site unspecified
		all_tumor.add("193"); //    Malignant neoplasm of thyroid gland
		all_tumor.add("183"); //    Malignant neoplasm of ovary
		all_tumor.add("185"); //    Malignant neoplasm of prostate
		
		StringBuffer sb_4 = new StringBuffer();
		for (int i = 0; i < all_tumor.size(); i++) {
			sb_4.append("'" + all_tumor.get(i) + "'").append(",");
		}
		String value=sb_4.toString().trim();
		value=value.substring(0,value.length()-1);
		
		bw.write("@attribute 'Class' { " + value + "} \n");

		bw.write("@data\n");
		
		for(Entry<BigInteger, String> entry:lable.entrySet()){
			StringBuffer sb =new StringBuffer();
			
			String features=null;
					
			if(!all_tumor.contains(entry.getValue())){
				continue;
			}
					if(sex.containsKey(entry.getKey())&&age.containsKey(entry.getKey())){
						features=sex.get(entry.getKey())+" "+age.get(entry.getKey())+" ";
					}else{
						System.err.println("not contain id (sex & age): "+entry.getKey());
						System.exit(0);
					}
				
					if(embeddings.containsKey(entry.getKey())){
						ArrayList<String> list=embeddings.get(entry.getKey());
						
						StringBuffer sb_e = new StringBuffer();
						for (int i = 0; i < list.size(); i++) {
							sb_e.append(list.get(i)).append(" ");
						}
						features+=sb_e.toString().trim();
						
					}else{
						System.err.println("not contain id in embdding: "+entry.getKey());
						System.exit(0);
					}
			
					bw.write(features+" "+entry.getValue()+ "\n");
		}
		bw.flush();
		bw.close();
		
	}
	
	public static Instances getInstance(String arff_input) throws Exception{
		DataSource source = new DataSource(arff_input);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		return data;
	}
	
	 public static Instances removeClass(Instances inst) {
		  Remove af = new Remove();
		  Instances retI = null;
		  try {
		    if (inst.classIndex() < 0) {
		      retI = inst;
		    } else {
		      af.setAttributeIndices("" + (inst.classIndex() + 1));
		      af.setInvertSelection(false);
		      af.setInputFormat(inst);
		      retI = Filter.useFilter(inst, af);
		    }
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		  return retI;
		}
	 
	 public static Instances removeDemograph(Instances inst) throws Exception {
		  Remove af = new Remove();
		  Instances retI = null;
		      int[] ranges=new int[2];
		      ranges[0]= inst.attribute("Sex").index();
		      ranges[1]= inst.attribute("Age").index();
		      System.out.println("ranges[0]: "+ranges[0]);
		      System.out.println("ranges[1]: "+ranges[1]);
		      af.setAttributeIndicesArray(ranges);
		      af.setInvertSelection(false);
		      af.setInputFormat(inst);
		      retI = Filter.useFilter(inst, af);
		 
		  return retI;
		}
	 
	public static void merge(String file_1,String file_2, String outfile) throws Exception {
		Instances instance_1=getInstance(file_1);
		Instances instance_2_tmp=getInstance(file_2);
		Instances instance_2=removeClass(instance_2_tmp);
		Instances instance_3=removeDemograph(instance_2) ;
		Instances instance_final=Instances.mergeInstances(instance_3, instance_1);
		instance_final.setClassIndex(instance_final.numAttributes()-1);
		
		DataSink.write(outfile, instance_final);
	}
	
	public static void writeARFF_embedding_average(ArrayList<BigInteger> p_list, String sex_file, String age_file, String label_file, String node_idx_file,String embedding_file,
			String outfile_arff) throws Exception {
		
		HashMap<BigInteger, String> sex=readSex(sex_file) ;
		HashMap<BigInteger, String> age=readAge(age_file) ;
		HashMap<BigInteger, String> lable=readLabel(label_file) ;
		HashMap<String, BigInteger> node_idx=readnode_patient(node_idx_file);
		HashMap<BigInteger, ArrayList<String>> embeddings=readembedding( embedding_file, node_idx);
		ArrayList<String> average_embedding=getAverage(embeddings);
		int embedding_size=0;
		for(Entry<BigInteger,ArrayList<String>> entry:embeddings.entrySet()){
			embedding_size=entry.getValue().size();
			break;
		}
		
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile_arff)));
		bw.write("@relation 'associations'\n");
		
	
		bw.write("@attribute 'Sex' { 'female', 'male' } \n");
		bw.write("@attribute 'Age' { 'very_old', 'old' , 'middle_age' , 'senior' , 'junior' } \n");
		
		for (int i = 0; i < embedding_size; i++) {
			bw.write("@attribute 'Attribute_" + i + "' numeric \n");
		}

		ArrayList<String> all_tumor = new ArrayList<>();
		
		all_tumor.add("153.9"); //   Malignant neoplasm of colon, unspecified site
		all_tumor.add("155"); //    Malignant neoplasm of liver, primary
		all_tumor.add("162.9"); //    PMalignant neoplasm of bronchus and lung, unspecified
		all_tumor.add("157.9"); //    Malignant neoplasm of pancreas, part unspecified
		all_tumor.add("174.9"); //     Malignant neoplasm of breast (female), unspecified
		all_tumor.add("171.9"); //    Malignant neoplasm of connective and other soft tissue, site unspecified
		all_tumor.add("193"); //    Malignant neoplasm of thyroid gland
		all_tumor.add("183"); //    Malignant neoplasm of ovary
		all_tumor.add("185"); //    Malignant neoplasm of prostate
		
		StringBuffer sb_4 = new StringBuffer();
		for (int i = 0; i < all_tumor.size(); i++) {
			sb_4.append("'" + all_tumor.get(i) + "'").append(",");
		}
		String value=sb_4.toString().trim();
		value=value.substring(0,value.length()-1);
		
		bw.write("@attribute 'Class' { " + value + "} \n");

		bw.write("@data\n");
		
		for(BigInteger patient:p_list ){
			
			String label =lable.get(patient);
			
			if(!all_tumor.contains(label)){
				continue;
			}
			StringBuffer sb =new StringBuffer();
			
			String features=null;
					
			
					if(sex.containsKey(patient)&&age.containsKey(patient)){
						features=sex.get(patient)+" "+age.get(patient)+" ";
					}else{
						System.err.println("not contain id (sex & age): "+patient);
						System.exit(0);
					}
				
					if(embeddings.containsKey(patient)){
						ArrayList<String> list=embeddings.get(patient);
						
						StringBuffer sb_e = new StringBuffer();
						for (int i = 0; i < list.size(); i++) {
							sb_e.append(list.get(i)).append(" ");
						}
						features+=sb_e.toString().trim();
						
					}else{
						
//						System.err.println("not contain id in embdding: "+patient+", using average embedding alternatively");
						StringBuffer sb_t = new StringBuffer();
						for (int i = 0; i < average_embedding.size(); i++) {
							sb_t.append(average_embedding.get(i)).append(" ");
						}
						features+=sb_t.toString().trim();
					}
			
					bw.write(features+" "+label+ "\n");
		}
		bw.flush();
		bw.close();
		
	}

	
}
