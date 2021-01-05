package data.fhir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import data.LabtestBean;
import data.Prediciton_PatientBean;

public class NetworkWriter {

	private HashMap<BigInteger, String> data_sex_refine;
	private HashMap<BigInteger, String> data_age_refine;
	private HashMap<BigInteger, HashSet<String>> data_genes_refine;
	private HashMap<BigInteger, HashSet<String>> data_labtests_refine;
	private HashMap<BigInteger, HashSet<String>> data_diagnosis_refine;
	private HashMap<BigInteger, HashSet<String>> data_medications_refine;
	private HashMap<BigInteger, HashSet<String>> data_historicals_refine;

	public HashSet<String> read_knowledgeGraph_nodes(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		HashSet<String> nodes = new HashSet<>();

		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				nodes.add(s);
				nodes.add(o);
			}
		}
		ArrayList<String> graph_node = new ArrayList<>();
		for (String string : nodes) {
			graph_node.add(string);
		}
		return nodes;
	}

	public void writeNetwork_refine(HashMap<BigInteger, Prediciton_PatientBean> data_patient,
			HashMap<BigInteger, String> data_sex, HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis, HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals, HashSet<String> mapping_diagnosis_omim,
			HashSet<String> mapping_history_omim, HashSet<String> mapping_medication_drugbank,
			HashSet<String> mapping_genetic_drugbank, HashSet<String> network, String idxFile, String outfile)
			throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfile)));
		HashMap<String, Integer> idx = readIdx(idxFile);

		HashSet<String> set_1 = new HashSet<>();
		for (String string : mapping_diagnosis_omim) {
			String[] elements = string.split("\t");
			if (idx.containsKey(elements[0]) && idx.containsKey(elements[1])) {
				bw.write(idx.get(elements[0]) + " " + idx.get(elements[1]) + "\n");
				set_1.add(idx.get(elements[0]) + " " + idx.get(elements[1]));
			}
		}
		System.out.println("write mapping_diagnosis_omim : " + set_1.size());
		HashSet<String> set_2 = new HashSet<>();
		for (String string : mapping_history_omim) {
			String[] elements = string.split("\t");
			if (idx.containsKey(elements[0]) && idx.containsKey(elements[1])) {
				bw.write(idx.get(elements[0]) + " " + idx.get(elements[1]) + "\n");
				set_2.add(idx.get(elements[0]) + " " + idx.get(elements[1]));
			}
		}
		System.out.println("write mapping_history_omim : " + set_2.size());

		HashSet<String> set_3 = new HashSet<>();
		for (String string : mapping_medication_drugbank) {
			String[] elements = string.split("\t");
			if (idx.containsKey(elements[0]) && idx.containsKey(elements[1])) {
				bw.write(idx.get(elements[0]) + " " + idx.get(elements[1]) + "\n");
				set_3.add(idx.get(elements[0]) + " " + idx.get(elements[1]));
			}
		}
		System.out.println("write mapping_medication_drugbank : " + set_3.size());

		HashSet<String> set_4 = new HashSet<>();
		for (String string : mapping_genetic_drugbank) {
			String[] elements = string.split("\t");
			if (idx.containsKey(elements[0]) && idx.containsKey(elements[1])) {
				bw.write(idx.get(elements[0]) + " " + idx.get(elements[1]) + "\n");
				set_4.add(idx.get(elements[0]) + " " + idx.get(elements[1]));
			}
		}
		System.out.println("write mapping_genetic_drugbank : " + set_4.size());

		HashSet<String> set_5 = new HashSet<>();
		for (String string : network) {
			String[] elements = string.split("\t");
			if (idx.containsKey(elements[0]) && idx.containsKey(elements[1])) {
				bw.write(idx.get(elements[0]) + " " + idx.get(elements[1]) + "\n");
				set_5.add(idx.get(elements[0]) + " " + idx.get(elements[1]));
			}
		}
		System.out.println("write network : " + set_5.size());

		ArrayList<HashMap<BigInteger, String>> demongraphic = new ArrayList<>();
		demongraphic.add(data_sex_refine);
		demongraphic.add(data_age_refine);
		ArrayList<HashMap<BigInteger, HashSet<String>>> others = new ArrayList<>();
		others.add(data_genes_refine);
		others.add(data_labtests_refine);
		others.add(data_diagnosis_refine);
		others.add(data_medications_refine);
		others.add(data_historicals_refine);

		// for(HashMap<BigInteger, String> map:demongraphic){
		// for(Entry<BigInteger, String> entry:map.entrySet()){
		// String patient_id="Patient<"+String.valueOf(entry.getKey())+">";
		// if (idx.containsKey(patient_id) && idx.containsKey(entry.getValue()))
		// {
		// bw.write(idx.get(patient_id) + " " + idx.get(entry.getValue()) +
		// "\n");
		// }
		// else {
		// System.out.println("not contained in idx : " + patient_id+"
		// "+entry.getValue());
		// System.exit(0);
		// }
		// }
		// }
		HashSet<Integer> nodes = new HashSet<>();
		HashSet<String> edges = new HashSet<>();
		for (HashMap<BigInteger, HashSet<String>> map : others) {
			for (Entry<BigInteger, HashSet<String>> entry : map.entrySet()) {
				String patient_id = "Patient<" + String.valueOf(entry.getKey()) + ">";
				for (String string : entry.getValue()) {
					if (idx.containsKey(patient_id) && idx.containsKey(string)) {
						nodes.add(idx.get(patient_id));
						nodes.add(idx.get(string));
						edges.add(idx.get(patient_id) + " " + idx.get(string));
						bw.write(idx.get(patient_id) + " " + idx.get(string) + "\n");
					}
					// else {
					// System.out.println("not contained in idx : " +
					// patient_id+" "+string);
					// System.exit(0);
					// }
				}
			}
		}

		System.out.println("nodes.size(): " + nodes.size());
		System.out.println("edges.size(): " + edges.size());
		bw.flush();
		bw.close();
	}

	public void writeNetwork(HashMap<BigInteger, Prediciton_PatientBean> data_patient,
			HashMap<BigInteger, String> data_sex, HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis, HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals, String idxFile, String outfile) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfile)));
		HashMap<String, Integer> idx = readIdx(idxFile);

		ArrayList<HashMap<BigInteger, String>> demongraphic = new ArrayList<>();
		demongraphic.add(data_sex_refine);
		demongraphic.add(data_age_refine);
		ArrayList<HashMap<BigInteger, HashSet<String>>> others = new ArrayList<>();
		others.add(data_genes_refine);
		others.add(data_labtests_refine);
		others.add(data_diagnosis_refine);
		others.add(data_medications_refine);
		others.add(data_historicals_refine);

		// for(HashMap<BigInteger, String> map:demongraphic){
		// for(Entry<BigInteger, String> entry:map.entrySet()){
		// String patient_id="Patient<"+String.valueOf(entry.getKey())+">";
		// if (idx.containsKey(patient_id) && idx.containsKey(entry.getValue()))
		// {
		// bw.write(idx.get(patient_id) + " " + idx.get(entry.getValue()) +
		// "\n");
		// }
		// else {
		// System.out.println("not contained in idx : " + patient_id+"
		// "+entry.getValue());
		// System.exit(0);
		// }
		// }
		// }
		HashSet<Integer> nodes = new HashSet<>();
		HashSet<String> edges = new HashSet<>();
		for (HashMap<BigInteger, HashSet<String>> map : others) {
			for (Entry<BigInteger, HashSet<String>> entry : map.entrySet()) {
				String patient_id = "Patient<" + String.valueOf(entry.getKey()) + ">";
				for (String string : entry.getValue()) {
					if (idx.containsKey(patient_id) && idx.containsKey(string)) {
						nodes.add(idx.get(patient_id));
						nodes.add(idx.get(string));
						edges.add(idx.get(patient_id) + " " + idx.get(string));
						bw.write(idx.get(patient_id) + " " + idx.get(string) + "\n");
					}
					// else {
					// System.out.println("not contained in idx : " +
					// patient_id+" "+string);
					// System.exit(0);
					// }
				}
			}
		}

		System.out.println("nodes.size(): " + nodes.size());
		System.out.println("edges.size(): " + edges.size());
		bw.flush();
		bw.close();
	}

	public void writeNetwork_all(HashMap<BigInteger, Prediciton_PatientBean> data_patient,
			HashMap<BigInteger, String> data_sex, HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis, HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals, String idxFile, String outdir) throws Exception {

		HashMap<String, Integer> idx = readIdx(idxFile);

		ArrayList<HashMap<BigInteger, String>> demongraphic = new ArrayList<>();
		demongraphic.add(data_sex_refine);
		demongraphic.add(data_age_refine);

		HashSet<String> set  = new HashSet<>();
		set.add("gene");
		set.add("labtest");
		set.add("diagnosis");
		set.add("medication");
		set.add("history");

			ArrayList<HashMap<BigInteger, HashSet<String>>> others = new ArrayList<>();

			if (set.size() > 0) {
				if (set.contains("gene")) {
					others.add(data_genes_refine);
				}
				if (set.contains("labtest")) {
					others.add(data_labtests_refine);
				}
				if (set.contains("diagnosis")) {
					others.add(data_diagnosis_refine);
				}
				if (set.contains("medication")) {
					others.add(data_medications_refine);
				}
				if (set.contains("history")) {
					others.add(data_historicals_refine);
				}
			}
			StringBuffer sb = new StringBuffer();
			for (String string : set) {
				sb.append(string).append("_");
			}
			System.out.println(outdir + "/network_" + sb.toString() + ".edgelist");
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(outdir + "/network_" + sb.toString() + ".edgelist")));
			HashSet<Integer> nodes = new HashSet<>();
			HashSet<String> edges = new HashSet<>();
			for (HashMap<BigInteger, HashSet<String>> map : others) {
				for (Entry<BigInteger, HashSet<String>> entry : map.entrySet()) {
					String patient_id = "Patient<" + String.valueOf(entry.getKey()) + ">";
					for (String string : entry.getValue()) {
						if (idx.containsKey(patient_id) && idx.containsKey(string)) {
							nodes.add(idx.get(patient_id));
							nodes.add(idx.get(string));
							edges.add(idx.get(patient_id) + " " + idx.get(string));
							bw.write(idx.get(patient_id) + " " + idx.get(string) + "\n");
						}

					}
				}
			}

			System.out.println("nodes.size(): " + nodes.size());
			System.out.println("edges.size(): " + edges.size());
			bw.flush();
			bw.close();

	}

	
	
	public HashMap<String, Integer> readIdx(String file) throws IOException {
		HashMap<String, Integer> map = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] elements = line.split("\t");
			map.put(elements[1], Integer.valueOf(elements[0]));
		}
		br.close();
		return map;
	}

	public void generateNewIndx(HashMap<BigInteger, Prediciton_PatientBean> data_patient,
			HashMap<BigInteger, String> data_sex, HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis, HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals, ArrayList<String> graph_nodes, String idxFile)
			throws IOException {

		ArrayList<String> all_genes = new ArrayList<>();
		ArrayList<String> all_labtests = new ArrayList<>();
		ArrayList<String> all_diagnosis = new ArrayList<>();
		ArrayList<String> all_medication = new ArrayList<>();
		ArrayList<String> all_history = new ArrayList<>();
		ArrayList<String> all_sex = new ArrayList<>();
		ArrayList<String> all_age = new ArrayList<>();

		// data_sex_refine=new HashMap<>();
		// data_age_refine=new HashMap<>();
		data_genes_refine = new HashMap<>();
		data_labtests_refine = new HashMap<>();
		data_diagnosis_refine = new HashMap<>();
		data_medications_refine = new HashMap<>();
		data_historicals_refine = new HashMap<>();

		/**
		 * returns
		 */
		// update_refine_sexddob(data_sex, data_sex_refine, "Sex", all_sex);
		// update_refine_sexddob(data_age, data_age_refine, "Age",all_age);
		update_refine_others(data_genes, data_genes_refine, "Gene", all_genes);
		update_refine_others(data_diagnosis, data_diagnosis_refine, "Diagnosis", all_diagnosis);
		update_refine_others(data_medications, data_medications_refine, "Medication", all_medication);
		update_refine_others(data_historicals, data_historicals_refine, "History", all_history);
		update_refine_lab(data_labtests, data_labtests_refine, "Labtest", all_labtests);
		ArrayList<String> all_nodes = new ArrayList<>();

		merge(all_nodes, all_sex);
		merge(all_nodes, all_age);
		merge(all_nodes, all_genes);
		merge(all_nodes, all_diagnosis);
		merge(all_nodes, all_medication);
		merge(all_nodes, all_history);
		merge(all_nodes, all_labtests);
		merge(all_nodes, graph_nodes);

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(idxFile)));
		int i = 0;
		for (String node : all_nodes) {
			bw.write(i + "\t" + node + "\n");
			i++;
		}

		for (BigInteger node : data_patient.keySet()) {
			bw.write(i + "\t" + "Patient<" + node + ">\n");
			i++;
		}

		bw.flush();
		bw.close();
	}

	public void generateNewIndx(HashMap<BigInteger, Prediciton_PatientBean> data_patient,
			HashMap<BigInteger, String> data_sex, HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis, HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals, String idxFile) throws IOException {

		ArrayList<String> all_genes = new ArrayList<>();
		ArrayList<String> all_labtests = new ArrayList<>();
		ArrayList<String> all_diagnosis = new ArrayList<>();
		ArrayList<String> all_medication = new ArrayList<>();
		ArrayList<String> all_history = new ArrayList<>();
		ArrayList<String> all_sex = new ArrayList<>();
		ArrayList<String> all_age = new ArrayList<>();

		// data_sex_refine=new HashMap<>();
		// data_age_refine=new HashMap<>();
		data_genes_refine = new HashMap<>();
		data_labtests_refine = new HashMap<>();
		data_diagnosis_refine = new HashMap<>();
		data_medications_refine = new HashMap<>();
		data_historicals_refine = new HashMap<>();

		/**
		 * returns
		 */
		// update_refine_sexddob(data_sex, data_sex_refine, "Sex", all_sex);
		// update_refine_sexddob(data_age, data_age_refine, "Age",all_age);
		update_refine_others(data_genes, data_genes_refine, "Gene", all_genes);
		update_refine_others(data_diagnosis, data_diagnosis_refine, "Diagnosis", all_diagnosis);
		update_refine_others(data_medications, data_medications_refine, "Medication", all_medication);
		update_refine_others(data_historicals, data_historicals_refine, "History", all_history);
		update_refine_lab(data_labtests, data_labtests_refine, "Labtest", all_labtests);
		ArrayList<String> all_nodes = new ArrayList<>();

		merge(all_nodes, all_sex);
		merge(all_nodes, all_age);
		merge(all_nodes, all_genes);
		merge(all_nodes, all_diagnosis);
		merge(all_nodes, all_medication);
		merge(all_nodes, all_history);
		merge(all_nodes, all_labtests);

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(idxFile)));
		int i = 0;
		for (String node : all_nodes) {
			bw.write(i + "\t" + node + "\n");
			i++;
		}

		for (BigInteger node : data_patient.keySet()) {
			bw.write(i + "\t" + "Patient<" + node + ">\n");
			i++;
		}

		bw.flush();
		bw.close();
	}

	public void generateNewIndx_refine(HashMap<BigInteger, Prediciton_PatientBean> data_patient,
			HashMap<BigInteger, String> data_sex, HashMap<BigInteger, String> data_age,
			HashMap<BigInteger, HashSet<String>> data_genes,
			HashMap<BigInteger, HashMap<String, LabtestBean>> data_labtests,
			HashMap<BigInteger, HashSet<String>> data_diagnosis, HashMap<BigInteger, HashSet<String>> data_medications,
			HashMap<BigInteger, HashSet<String>> data_historicals, HashSet<String> network_nodes, String idxFile)
			throws IOException {

		ArrayList<String> all_genes = new ArrayList<>();
		ArrayList<String> all_labtests = new ArrayList<>();
		ArrayList<String> all_diagnosis = new ArrayList<>();
		ArrayList<String> all_medication = new ArrayList<>();
		ArrayList<String> all_history = new ArrayList<>();
		ArrayList<String> all_sex = new ArrayList<>();
		ArrayList<String> all_age = new ArrayList<>();

		// data_sex_refine=new HashMap<>();
		// data_age_refine=new HashMap<>();
		data_genes_refine = new HashMap<>();
		data_labtests_refine = new HashMap<>();
		data_diagnosis_refine = new HashMap<>();
		data_medications_refine = new HashMap<>();
		data_historicals_refine = new HashMap<>();

		/**
		 * returns
		 */
		// update_refine_sexddob(data_sex, data_sex_refine, "Sex", all_sex);
		// update_refine_sexddob(data_age, data_age_refine, "Age",all_age);
		update_refine_others(data_genes, data_genes_refine, "Gene", all_genes);
		update_refine_others(data_diagnosis, data_diagnosis_refine, "Diagnosis", all_diagnosis);
		update_refine_others(data_medications, data_medications_refine, "Medication", all_medication);
		update_refine_others(data_historicals, data_historicals_refine, "History", all_history);
		update_refine_lab(data_labtests, data_labtests_refine, "Labtest", all_labtests);
		ArrayList<String> all_nodes = new ArrayList<>();

		merge(all_nodes, all_sex);
		merge(all_nodes, all_age);
		merge(all_nodes, all_genes);
		merge(all_nodes, all_diagnosis);
		merge(all_nodes, all_medication);
		merge(all_nodes, all_history);
		merge(all_nodes, all_labtests);

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(idxFile)));
		int i = 0;
		for (String node : all_nodes) {
			bw.write(i + "\t" + node + "\n");
			i++;
		}

		for (BigInteger node : data_patient.keySet()) {
			bw.write(i + "\t" + "Patient<" + node + ">\n");
			i++;
		}
		for (String string : network_nodes) {
			bw.write(i + "\t" + string + "\n");
			i++;
		}

		bw.flush();
		bw.close();
	}
		
	public void writeNetwork_byRDF(String rdffile, String idxFile, String outfile) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfile)));
		HashMap<String, Integer> idx = readIdx(idxFile);

		BufferedReader br =new BufferedReader(new FileReader(new File(rdffile)));
		String line=null;
		String uri_type="<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
		String uri_hasAge="<https://BD2KOnFHIR/fhirtordf/hasAge>";
		String uri_hasGender="<https://BD2KOnFHIR/fhirtordf/hasGender>";
		String uri_hasMutatedGene="<https://BD2KOnFHIR/fhirtordf/hasMutatedGene>";
		String uri_labtest="<https://BD2KOnFHIR/fhirtordf/Labtest/";
		String uri_hasCondition="<https://BD2KOnFHIR/fhirtordf/hasCondition>";
		String uri_hasMedication="<https://BD2KOnFHIR/fhirtordf/hasMedication>";
		String uri_FamilyHistoryCondition="<https://BD2KOnFHIR/fhirtordf/hasFamilyHistoryCondition>";
		
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					
					if(p.equals(uri_hasAge)||p.equals(uri_hasGender)||p.startsWith(uri_labtest)||
						p.equals(uri_hasMutatedGene)||p.equals(uri_hasCondition)||p.equals(uri_hasMedication)||
						p.equals(uri_FamilyHistoryCondition)){
						bw.write(idx.get(s) + " " + idx.get(o) + "\n");
					}
					
					
				}
			}
		}
		
		
		bw.flush();
		bw.close();
	}

	
	public void generateNewIndx_byRDF(String rdffile, String idxFile)
			throws IOException {
		
		HashSet<String> all_nodes=new HashSet<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(rdffile)));
		String line=null;
		String uri_type="<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
		String uri_hasAge="<https://BD2KOnFHIR/fhirtordf/hasAge>";
		String uri_hasGender="<https://BD2KOnFHIR/fhirtordf/hasGender>";
		String uri_labtest="<https://BD2KOnFHIR/fhirtordf/Labtest/";
		String uri_hasCondition="<https://BD2KOnFHIR/fhirtordf/hasCondition>";
		String uri_hasMedication="<https://BD2KOnFHIR/fhirtordf/hasMedication>";
		String uri_FamilyHistoryCondition="<https://BD2KOnFHIR/fhirtordf/hasFamilyHistoryCondition>";
		
		while((line=br.readLine())!=null){
			if(!line.contains("\"")){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					
					if(p.equals(uri_type)){
						all_nodes.add(s);
					}
					
					if(p.equals(uri_hasAge)||p.equals(uri_hasGender)||p.startsWith(uri_labtest)
							||p.equals(uri_hasCondition)||p.equals(uri_hasMedication)||p.equals(uri_FamilyHistoryCondition)){
						all_nodes.add(o);
						all_nodes.add(s);
					}
					
				}
			}
		}
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(idxFile)));
		int i = 0;
		for (String node : all_nodes) {
			bw.write(i + "\t" + node + "\n");
			i++;
		}
		
		bw.flush();
		bw.close();
	}


	public void merge(ArrayList<String> all_nodes, ArrayList<String> local_nodes) {
		for (String string : local_nodes) {
			if (!all_nodes.contains(string)) {
				all_nodes.add(string);
			}
		}
	}

	public void update_refine_others(HashMap<BigInteger, HashSet<String>> map,
			HashMap<BigInteger, HashSet<String>> refine_map, String type, ArrayList<String> value_list) {
		value_list.add(type + "<null>");
		for (Entry<BigInteger, HashSet<String>> entry : map.entrySet()) {
			HashSet<String> local_set = new HashSet<>();

			for (String string : entry.getValue()) {
				String refined_value = type + "<" + string + ">";
				if (!value_list.contains(refined_value)) {
					value_list.add(refined_value);
				}
				local_set.add(refined_value);
			}

			// if(local_set.size()==0){
			// local_set.add(type+"<null>");
			// }
			refine_map.put(entry.getKey(), local_set);
		}
	}

	public void update_refine_sexddob(HashMap<BigInteger, String> map, HashMap<BigInteger, String> refined_map,
			String type, ArrayList<String> value_list) {
		for (Entry<BigInteger, String> entry : map.entrySet()) {
			String refined_value = type + "<" + entry.getValue() + ">";
			if (!value_list.contains(refined_value)) {
				value_list.add(refined_value);
			}
			refined_map.put(entry.getKey(), refined_value);
		}
	}

	public void update_refine_lab(HashMap<BigInteger, HashMap<String, LabtestBean>> map,
			HashMap<BigInteger, HashSet<String>> refine_map, String type, ArrayList<String> value_list) {
		HashMap<String, HashSet<String>> control_map = new HashMap<>();
		for (Entry<BigInteger, HashMap<String, LabtestBean>> entry_1 : map.entrySet()) {
			HashSet<String> local_set = new HashSet<>();

			for (Entry<String, LabtestBean> entry_2 : entry_1.getValue().entrySet()) {
				LabtestBean bean = entry_2.getValue();
				String value = bean.getCode() + "(" + bean.getValue() + ")";
				String refine_value = type + "<" + value + ">";
				local_set.add(refine_value);
				if (control_map.containsKey(bean.getCode())) {
					control_map.get(bean.getCode()).add(refine_value);
				} else {
					HashSet<String> set = new HashSet<>();
					set.add(refine_value);
					control_map.put(bean.getCode(), set);
				}
			}
			refine_map.put(entry_1.getKey(), local_set);
			value_list.addAll(local_set);
		}

		

	}
}
