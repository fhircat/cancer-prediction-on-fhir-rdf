package method;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import data.DateCalculator;
import data.DiagnosisBean;
import data.EncounterBean;
import data.LabtestBean;
import data.PatientBean;
import data.Prediciton_PatientBean;
import data.arffWriter.ARFFWriter;
import data.parser.Parser_diagnosis;
import data.parser.Parser_encounter;
import data.parser.Parser_geneReport;
import data.parser.Parser_historical;
import data.parser.Parser_labtest;
import data.parser.Parser_medication;
import data.parser.Parser_patientList;

public class InputMatrix_generator {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String medicationfile = "";
		String labtestfile = "";
		String encounterfile = "";
		String patientfile = "";
		String diagnosisfile = "";
		String subtypefile = "";

		HashMap<String, HashMap<String, String>> subtypes = readSubTyping(subtypefile);
		HashMap<String, HashSet<String>> icdtohistorical = readSubTyping_icdTohistoryMapping(subtypefile);
		HashMap<String, HashSet<String>> labeltohistorical = readSubTyping_lablelTohistoryMapping(subtypefile);

		System.out.println(icdtohistorical);

		createDataMatrixAndARFF(patientfile, diagnosisfile, encounterfile, labtestfile, medicationfile, subtypes,
				icdtohistorical, labeltohistorical);
	}

	/**
	 * change to hit all possible sub-diseases
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */

	public static HashMap<String, HashMap<String, String>> readSubTyping(String file) throws IOException {
		HashMap<String, HashMap<String, String>> subtypes = new HashMap<>();
		HashMap<BigInteger, PatientBean> mrn_set = new HashMap<>();
		Reader reader = Files.newBufferedReader(Paths.get(file));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> csvRecords = csvParser.getRecords();
		CSVRecord header = csvRecords.get(0);

		int k = 0;
		for (CSVRecord csvRecord : csvRecords) {
			k++;
			List new_csvRecord = new ArrayList();
			if (k == 1) {
			} else {
				String mrn = "NULL";
				if (!csvRecord.get(0).equals("NULL")) {

					String submitted_diagnosis = csvRecord.get(1);
					String icd_9 = csvRecord.get(2);
					String icd_10_all = csvRecord.get(6);
					String icd_9_all = csvRecord.get(5);

					HashMap<String, String> local_map = new HashMap<>();
					local_map.put("icd9", icd_9_all);
					local_map.put("icd10", icd_10_all);
					local_map.put("general", icd_9);

					subtypes.put(submitted_diagnosis, local_map);
				}
			}
		}
		return subtypes;
	}

	public static HashMap<String, HashSet<String>> readSubTyping_icdTohistoryMapping(String file) throws IOException {
		HashMap<String, HashSet<String>> icdtohistorical = new HashMap<>();
		Reader reader = Files.newBufferedReader(Paths.get(file));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> csvRecords = csvParser.getRecords();
		CSVRecord header = csvRecords.get(0);

		int k = 0;
		for (CSVRecord csvRecord : csvRecords) {
			k++;
			List new_csvRecord = new ArrayList();
			if (k == 1) {
			} else {
				String mrn = "NULL";
				if (!csvRecord.get(0).equals("NULL")) {

					String submitted_diagnosis = csvRecord.get(1);
					String icd_9 = csvRecord.get(2);
					String icd_9_all = csvRecord.get(5);
					String icd_10_all = csvRecord.get(6);

					String icd_9_hisrtorical = csvRecord.get(7);
					String icd_10_hisrtorical = csvRecord.get(8);
					HashSet<String> set = new HashSet<>();
					set.add(icd_9_hisrtorical);
					set.add(icd_10_hisrtorical);

					icdtohistorical.put(icd_9_all, set);
					icdtohistorical.put(icd_10_all, set);
				}
			}
		}
		return icdtohistorical;
	}

	public static HashMap<String, HashSet<String>> readSubTyping_lablelTohistoryMapping(String file)
			throws IOException {
		HashMap<String, HashSet<String>> labeltohistorical = new HashMap<>();
		Reader reader = Files.newBufferedReader(Paths.get(file));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> csvRecords = csvParser.getRecords();
		CSVRecord header = csvRecords.get(0);

		int k = 0;
		for (CSVRecord csvRecord : csvRecords) {
			k++;
			List new_csvRecord = new ArrayList();
			if (k == 1) {
			} else {
				String mrn = "NULL";
				if (!csvRecord.get(0).equals("NULL")) {

					String submitted_diagnosis = csvRecord.get(1);
					String icd_9 = csvRecord.get(2);
					String icd_9_all = csvRecord.get(5);
					String icd_10_all = csvRecord.get(6);

					String icd_9_hisrtorical = csvRecord.get(7);
					String icd_10_hisrtorical = csvRecord.get(8);
					HashSet<String> set = new HashSet<>();
					set.add(icd_9_hisrtorical);
					set.add(icd_10_hisrtorical);

					labeltohistorical.put(icd_9, set);
				}
			}
		}
		return labeltohistorical;
	}

	public static HashSet<String> get_icd_cancers() {
		HashSet<String> set = new HashSet<>();
		for (int i = 10; i < 100; i++) {
			set.add("C" + String.valueOf(i));
		}
		for (int i = 10; i < 50; i++) {
			set.add("D" + String.valueOf(i));
		}
		set.add("C00");
		set.add("C01");
		set.add("C02");
		set.add("C03");
		set.add("C04");
		set.add("C05");
		set.add("C06");
		set.add("C07");
		set.add("C08");
		set.add("C09");
		set.add("C7A");
		set.add("C7B");

		set.add("D00");
		set.add("D01");
		set.add("D02");
		set.add("D03");
		set.add("D04");
		set.add("D05");
		set.add("D06");
		set.add("D07");
		set.add("D08");
		set.add("D09");
		set.add("D3A");

		for (int i = 140; i < 240; i++) {
			set.add(String.valueOf(i));
		}
		return set;
	}

	public static void createDataMatrixAndARFF_expried(String patientList_file, String diagnosisTabFile,
			String encounterTabFile, String labtestTabFile, String medicationTabFile,
			HashMap<String, HashMap<String, String>> subtypes, HashMap<String, String> icdtohistorical)
			throws Exception {

		HashMap<BigInteger, HashMap<String, String>> patient_sexdob = Parser_patientList
				.readPatient_sexdob(patientList_file);
		HashMap<BigInteger, Prediciton_PatientBean> patient_map = Parser_patientList.readPatient(patientList_file,
				subtypes);

		HashMap<BigInteger, HashMap<String, DiagnosisBean>> diagnosis_map = Parser_diagnosis
				.readDiagnosisTab(diagnosisTabFile);

		HashMap<BigInteger, HashMap<String, String>> diagnosis_encounter_date = Parser_diagnosis
				.readDiagnosisTab_time(diagnosisTabFile);

		HashMap<BigInteger, HashMap<String, HashSet<EncounterBean>>> encounter_map = Parser_encounter
				.readEncounterTab(encounterTabFile); // id, billing encounter,
														// bean

		HashMap<BigInteger, HashSet<String>> first_encounter_map = new HashMap<>();// id,
																					// billing
																					// encounter

		HashMap<BigInteger, HashSet<String>> encounter_withcancer_map = new HashMap<>();// id,
		// billing encounter
		HashSet<String> cancer_icds = get_icd_cancers();

		HashMap<BigInteger, String> diagnozed_time = new HashMap<>();

		for (Entry<BigInteger, Prediciton_PatientBean> entry_1 : patient_map.entrySet()) {
			String icd_general_10 = entry_1.getValue().getGeneral_diagnosis_icd10();
			String icd_general_9 = entry_1.getValue().getGeneral_diagnosis_icd9();

			HashMap<String, DiagnosisBean> map = diagnosis_map.get(entry_1.getKey());

			long days = 0;
			HashSet<String> local_set = new HashSet<>();
			HashSet<String> cancer_set = new HashSet<>();
			StringBuffer sb = new StringBuffer();

			if (map != null) {
				for (Entry<String, DiagnosisBean> entry_2 : map.entrySet()) {
					String date = entry_2.getValue().getDate();
					HashSet<String> icd = entry_2.getValue().getIcd();
					boolean contains = false;
					boolean contains_cancer = false;
					for (String code : icd) {
						sb.append(code).append(" ");
						if (code.toLowerCase().startsWith(icd_general_9.toLowerCase())
								|| code.toLowerCase().startsWith(icd_general_10.toLowerCase())) {
							contains = true;
						}

						String icd9_historial = icdtohistorical.get(icd_general_9);
						String icd10_historial = icdtohistorical.get(icd_general_10);

						if (code.toLowerCase().startsWith(icd9_historial.toLowerCase())
								|| code.toLowerCase().startsWith(icd10_historial.toLowerCase())) {

							contains = true;
						}

						for (String cancer_icd : cancer_icds) {
							if (code.contains(cancer_icd)) {
								contains_cancer = true;
							}
						}

					}
					if (contains) {
						local_set.add(entry_2.getKey());
					}
					if (contains_cancer) {
						cancer_set.add(entry_2.getKey());
					}
				}
			}

			first_encounter_map.put(entry_1.getKey(), local_set);
			encounter_withcancer_map.put(entry_1.getKey(), cancer_set);

			if (local_set.size() == 0) {
				Prediciton_PatientBean bean = patient_map.get(entry_1.getKey());
				bean.setSubmittedDiagnosis_general("0.0");
				// System.err.println(bean.getClinical_number()+"
				// "+bean.getSubmittedDiagnosis()+
				// " "+bean.getSubmittedDiagnosis_general()+"
				// "+bean.getGeneral_diagnosis_icd9()+"
				// "+bean.getGeneral_diagnosis_icd10()+" -> "
				// +sb.toString());
			} else {
				for (String string : local_set) {
					String time = diagnosis_encounter_date.get(entry_1.getKey()).get(string);

					if (diagnozed_time.containsKey(entry_1.getKey())) {
						String last_time = diagnozed_time.get(entry_1.getKey());
						if (DateCalculator.differenceWithCurrent(last_time, time) > 0) {
							diagnozed_time.put(entry_1.getKey(), time);
						}
					} else {
						diagnozed_time.put(entry_1.getKey(), time);
					}
				}
			}
		}

		HashSet<BigInteger> removed_patitent = new HashSet<>();
		HashMap<String, Integer> counter = new HashMap<>();
		for (Entry<BigInteger, Prediciton_PatientBean> entry : patient_map.entrySet()) {
			String Diagnosis = entry.getValue().getSubmittedDiagnosis_general();
			if (counter.containsKey(Diagnosis)) {
				counter.put(Diagnosis, counter.get(Diagnosis) + 1);
			} else {
				counter.put(Diagnosis, 1);
			}
			if (Diagnosis.equals("0.0")) {
				removed_patitent.add(entry.getKey());
			}
		}

		for (Entry<String, Integer> entry : counter.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}

		for (BigInteger p : removed_patitent) {
			if (patient_map.containsKey(p)) {
				patient_map.remove(p);
			}
			if (encounter_withcancer_map.containsKey(p)) {
				encounter_withcancer_map.remove(p);
			}
		}

		System.out.println("patient_map: " + patient_map.size());
		System.out.println("first_encounter_map: " + first_encounter_map.size());

		HashMap<BigInteger, String> patient_sex = new HashMap<>();
		HashMap<BigInteger, String> patient_age = new HashMap<>();

		for (Entry<BigInteger, String> entry : diagnozed_time.entrySet()) {
			String sex = patient_sexdob.get(entry.getKey()).get("sex").toLowerCase().trim();
			String dob = patient_sexdob.get(entry.getKey()).get("dob");
			String age = DateCalculator.getAge(entry.getValue(), dob).toLowerCase().trim();
			patient_sex.put(entry.getKey(), sex);
			patient_age.put(entry.getKey(), age);
		}

		/**
		 * patient_map + first_encounter_map both removed the unused patient
		 * 
		 * 
		 */

		HashMap<BigInteger, HashSet<String>> encounter_map_removed = new HashMap<BigInteger, HashSet<String>>();

		for (Entry<BigInteger, HashSet<String>> entry_1 : encounter_withcancer_map.entrySet()) {
			if (entry_1.getValue().size() == 0) {
				continue;
			}
			HashSet<String> removed_encounters = entry_1.getValue();
			encounter_map_removed.put(entry_1.getKey(), removed_encounters);
			for (Entry<String, HashSet<EncounterBean>> entry_2 : encounter_map.get(entry_1.getKey()).entrySet()) {
				if (removed_encounters.contains(entry_2.getKey())) {

					HashSet<String> encounters = new HashSet<>();

					for (EncounterBean bean : entry_2.getValue()) {
						encounters.add(bean.getBilling_encounter());
						encounters.add(bean.getEHR_encounter());
					}

					encounter_map_removed.get(entry_1.getKey()).addAll(encounters);
				}
			}
		}

		System.out.println("encounter_map_removed: " + encounter_map_removed.size());

		HashMap<BigInteger, HashMap<String, LabtestBean>> labtest_matrix = Parser_labtest.readLabtestTab(labtestTabFile,
				patient_map.keySet(), encounter_map_removed);

		System.out.println("labtest_matrix: " + labtest_matrix.size());

		Parser_labtest.LabtestTab_checkSize(labtestTabFile, patient_map.keySet());

		HashMap<BigInteger, HashSet<String>> gene_matrix = Parser_geneReport.getGenes(patient_map.keySet());

		System.out.println("gene_matrix: " + gene_matrix.size());

		HashMap<BigInteger, HashSet<String>> medication_matrix = Parser_medication.readMedication(medicationTabFile,
				patient_map.keySet(), encounter_map_removed);

		System.out.println("medication_matrix: " + medication_matrix.size());

		HashMap<BigInteger, HashSet<String>> diagnosisi_matrix = Parser_diagnosis.readDiagnosisTab(diagnosisTabFile,
				patient_map.keySet(), encounter_map_removed);

		System.out.println("diagnosisi_matrix: " + diagnosisi_matrix.size());

		HashMap<BigInteger, HashSet<String>> historical_matrix = Parser_historical.getHistorical(patient_map.keySet());

		System.out.println("diagnosisi_matrix: " + historical_matrix.size());

		HashMap<BigInteger, HashSet<String>> gene_matrix_lowercase = lowercase_others(gene_matrix);
		HashMap<BigInteger, HashSet<String>> diagnosisi_matrix_lowercase = lowercase_others(diagnosisi_matrix);
		HashMap<BigInteger, HashSet<String>> medication_matrix_lowercase = lowercase_others(medication_matrix);
		HashMap<BigInteger, HashSet<String>> historical_matrix_lowercase = lowercase_others(historical_matrix);

		HashMap<BigInteger, String> patient_sex_lowercase = lowercase_sexddob(patient_sex);
		HashMap<BigInteger, String> patient_age_lowercase = lowercase_sexddob(patient_age);

		HashMap<BigInteger, HashMap<String, LabtestBean>> labtest_matrix_lowercase = lowercase_lab(labtest_matrix);

		ArrayList<Integer> dimensions = new ArrayList<>();
		// dimensions.add(10);
		// dimensions.add(20);
		// dimensions.add(30);
		// dimensions.add(40);
		// dimensions.add(50);
		// dimensions.add(60);
		// dimensions.add(70);
		// dimensions.add(80);
		// dimensions.add(90);
		dimensions.add(100);

		ArrayList<BigInteger> p_list = getpatientList();

		for (int dimension : dimensions) {
			ARFFWriter.writeARFF_all(p_list, patient_map, patient_sex_lowercase, patient_age_lowercase,
					gene_matrix_lowercase, labtest_matrix_lowercase, diagnosisi_matrix_lowercase,
					medication_matrix_lowercase, historical_matrix_lowercase, dimension);
		}

	}

	public static void createDataMatrixAndARFF(String patientList_file, String diagnosisTabFile,
			String encounterTabFile, String labtestTabFile, String medicationTabFile,
			HashMap<String, HashMap<String, String>> subtypes, HashMap<String, HashSet<String>> icdtohistorical,
			HashMap<String, HashSet<String>> labeltohistorical) throws Exception {

		HashMap<BigInteger, HashMap<String, String>> patient_sexdob = Parser_patientList
				.readPatient_sexdob(patientList_file);
		HashMap<BigInteger, Prediciton_PatientBean> patient_map = Parser_patientList.readPatient(patientList_file,
				subtypes);

		HashMap<BigInteger, HashMap<String, DiagnosisBean>> diagnosis_map = Parser_diagnosis
				.readDiagnosisTab(diagnosisTabFile);

		HashMap<BigInteger, HashMap<String, String>> diagnosis_encounter_date = Parser_diagnosis
				.readDiagnosisTab_time(diagnosisTabFile);

		HashMap<BigInteger, HashMap<String, HashSet<EncounterBean>>> encounter_map = Parser_encounter
				.readEncounterTab(encounterTabFile); // id, billing encounter,
														// bean

		HashMap<BigInteger, HashSet<String>> first_encounter_map = new HashMap<>();// id,
																					// billing
																					// encounter

		HashMap<BigInteger, HashSet<String>> encounter_withcancer_map = new HashMap<>();// id,
		// billing encounter
		HashSet<String> cancer_icds = get_icd_cancers();

		HashMap<BigInteger, String> diagnozed_time = new HashMap<>();

		for (Entry<BigInteger, Prediciton_PatientBean> entry_1 : patient_map.entrySet()) {
			String icd_general_10 = entry_1.getValue().getGeneral_diagnosis_icd10();
			String icd_general_9 = entry_1.getValue().getGeneral_diagnosis_icd9();

			HashMap<String, DiagnosisBean> map = diagnosis_map.get(entry_1.getKey());

			long days = 0;
			HashSet<String> local_set = new HashSet<>();
			HashSet<String> cancer_set = new HashSet<>();
			StringBuffer sb = new StringBuffer();

			if (map != null) {
				for (Entry<String, DiagnosisBean> entry_2 : map.entrySet()) {
					String date = entry_2.getValue().getDate();
					HashSet<String> icd = entry_2.getValue().getIcd();
					boolean contains = false;
					boolean contains_cancer = false;
					for (String code : icd) {
						sb.append(code).append(" ");
						if (code.toLowerCase().startsWith(icd_general_9.toLowerCase())
								|| code.toLowerCase().startsWith(icd_general_10.toLowerCase())) {
							contains = true;
						}

						HashSet<String> icd9_historial = icdtohistorical.get(icd_general_9);
						HashSet<String> icd10_historial = icdtohistorical.get(icd_general_10);

						for (String string : icd9_historial) {
							if (code.toLowerCase().startsWith(string.toLowerCase())) {
								// contains = true;
								contains_cancer = true;
							}
						}
						for (String string : icd10_historial) {
							if (code.toLowerCase().startsWith(string.toLowerCase())) {
								// contains = true;
								contains_cancer = true;
							}
						}

						for (String cancer_icd : cancer_icds) {
							if (code.contains(cancer_icd)) {
								contains_cancer = true;
							}
						}

					}
					if (contains) {
						local_set.add(entry_2.getKey());
					}
					if (contains_cancer) {
						cancer_set.add(entry_2.getKey());
					}
				}
			}

			first_encounter_map.put(entry_1.getKey(), local_set);
			encounter_withcancer_map.put(entry_1.getKey(), cancer_set);

			if (local_set.size() == 0) {
				Prediciton_PatientBean bean = patient_map.get(entry_1.getKey());
				bean.setSubmittedDiagnosis_general("0.0");

			} else {
				for (String string : local_set) {
					String time = diagnosis_encounter_date.get(entry_1.getKey()).get(string);

					if (diagnozed_time.containsKey(entry_1.getKey())) {
						String last_time = diagnozed_time.get(entry_1.getKey());
						if (DateCalculator.differenceWithCurrent(last_time, time) > 0) {
							diagnozed_time.put(entry_1.getKey(), time);
						}
					} else {
						diagnozed_time.put(entry_1.getKey(), time);
					}
				}
			}
		}

		HashSet<BigInteger> removed_patitent = new HashSet<>();
		HashMap<String, Integer> counter = new HashMap<>();
		for (Entry<BigInteger, Prediciton_PatientBean> entry : patient_map.entrySet()) {
			String Diagnosis = entry.getValue().getSubmittedDiagnosis_general();
			if (counter.containsKey(Diagnosis)) {
				counter.put(Diagnosis, counter.get(Diagnosis) + 1);
			} else {
				counter.put(Diagnosis, 1);
			}
			if (Diagnosis.equals("0.0")) {
				removed_patitent.add(entry.getKey());
			}
		}

		for (BigInteger p : removed_patitent) {
			if (patient_map.containsKey(p)) {
				patient_map.remove(p);
			}
			if (encounter_withcancer_map.containsKey(p)) {
				encounter_withcancer_map.remove(p);
			}
		}

		HashMap<BigInteger, String> patient_sex = new HashMap<>();
		HashMap<BigInteger, String> patient_age = new HashMap<>();

		for (Entry<BigInteger, String> entry : diagnozed_time.entrySet()) {
			String sex = patient_sexdob.get(entry.getKey()).get("sex").toLowerCase().trim();
			String dob = patient_sexdob.get(entry.getKey()).get("dob");
			String age = DateCalculator.getAge(entry.getValue(), dob).toLowerCase().trim();
			patient_sex.put(entry.getKey(), sex);
			patient_age.put(entry.getKey(), age);
		}

		HashMap<BigInteger, HashSet<String>> encounter_map_removed = new HashMap<BigInteger, HashSet<String>>();

		for (Entry<BigInteger, HashSet<String>> entry_1 : encounter_withcancer_map.entrySet()) {
			if (entry_1.getValue().size() == 0) {
				continue;
			}
			HashSet<String> removed_encounters = entry_1.getValue();
			encounter_map_removed.put(entry_1.getKey(), removed_encounters);
			for (Entry<String, HashSet<EncounterBean>> entry_2 : encounter_map.get(entry_1.getKey()).entrySet()) {
				if (removed_encounters.contains(entry_2.getKey())) {

					HashSet<String> encounters = new HashSet<>();

					for (EncounterBean bean : entry_2.getValue()) {
						encounters.add(bean.getBilling_encounter());
						encounters.add(bean.getEHR_encounter());
					}

					encounter_map_removed.get(entry_1.getKey()).addAll(encounters);
				}
			}
		}

		HashMap<BigInteger, HashMap<String, LabtestBean>> labtest_matrix = Parser_labtest.readLabtestTab(labtestTabFile,
				patient_map.keySet(), encounter_map_removed);

		HashMap<BigInteger, HashSet<String>> gene_matrix = Parser_geneReport.getGenes(patient_map.keySet());

		HashMap<BigInteger, HashSet<String>> medication_matrix = Parser_medication.readMedication(medicationTabFile,
				patient_map.keySet(), encounter_map_removed);

		HashMap<BigInteger, HashSet<String>> diagnosisi_matrix = Parser_diagnosis.readDiagnosisTab(diagnosisTabFile,
				patient_map.keySet(), encounter_map_removed);

		HashMap<BigInteger, HashSet<String>> historical_matrix = Parser_historical.getHistorical(patient_map.keySet());

		HashMap<BigInteger, HashSet<String>> gene_matrix_lowercase = lowercase_others(gene_matrix);
		HashMap<BigInteger, HashSet<String>> diagnosisi_matrix_lowercase = lowercase_others(diagnosisi_matrix);
		HashMap<BigInteger, HashSet<String>> medication_matrix_lowercase = lowercase_others(medication_matrix);
		HashMap<BigInteger, HashSet<String>> historical_matrix_lowercase = lowercase_others(historical_matrix);

		HashMap<BigInteger, String> patient_sex_lowercase = lowercase_sexddob(patient_sex);
		HashMap<BigInteger, String> patient_age_lowercase = lowercase_sexddob(patient_age);

		HashMap<BigInteger, HashMap<String, LabtestBean>> labtest_matrix_lowercase = lowercase_lab(labtest_matrix);

		ArrayList<BigInteger> p_list = getpatientList();

		ARFFWriter.writeARFF_all(p_list, patient_map, patient_sex_lowercase, patient_age_lowercase,
				gene_matrix_lowercase, labtest_matrix_lowercase, diagnosisi_matrix_lowercase,
				medication_matrix_lowercase, historical_matrix_lowercase, 100);

	}

	
	public static ArrayList<BigInteger> getpatientList() throws IOException {
		ArrayList<BigInteger> list = new ArrayList<>();
		String file = "classification_patient.csv";
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] elemnets = line.split("\t");
			list.add(new BigInteger(elemnets[1].trim()));
		}
		return list;
	}

	public static HashMap<BigInteger, HashSet<String>> lowercase_others(HashMap<BigInteger, HashSet<String>> map) {

		HashMap<BigInteger, HashSet<String>> lowercase = new HashMap<>();

		for (Entry<BigInteger, HashSet<String>> entry : map.entrySet()) {
			HashSet<String> local_set = new HashSet<>();

			for (String string : entry.getValue()) {
				local_set.add(string.toLowerCase().trim());
			}
			lowercase.put(entry.getKey(), local_set);
		}
		return lowercase;
	}

	public static HashMap<BigInteger, String> lowercase_sexddob(HashMap<BigInteger, String> map) {
		HashMap<BigInteger, String> lowercase = new HashMap<>();
		for (Entry<BigInteger, String> entry : map.entrySet()) {

			lowercase.put(entry.getKey(), entry.getValue().toLowerCase().trim());
		}
		return lowercase;
	}

	public static HashMap<BigInteger, HashMap<String, LabtestBean>> lowercase_lab(
			HashMap<BigInteger, HashMap<String, LabtestBean>> map) {
		HashMap<BigInteger, HashMap<String, LabtestBean>> lowercase = new HashMap<>();
		for (Entry<BigInteger, HashMap<String, LabtestBean>> entry_1 : map.entrySet()) {
			HashMap<String, LabtestBean> local_map = new HashMap<>();

			for (Entry<String, LabtestBean> entry_2 : entry_1.getValue().entrySet()) {
				LabtestBean bean = entry_2.getValue();
				LabtestBean new_bean = entry_2.getValue();
				new_bean.setCode(bean.getCode().toLowerCase().trim());
				new_bean.setDate(bean.getDate());
				new_bean.setValue(bean.getValue().toLowerCase().trim());
				local_map.put(entry_2.getKey().toLowerCase().trim(), new_bean);
			}
			lowercase.put(entry_1.getKey(), local_map);
		}
		return lowercase;
	}

	public static void readFile(String tableFile, String outFile1, String outFile2) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(tableFile)));
		String line = null;
		HashMap<String, HashMap<String, Integer>> id_code_map = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, HashMap<String, Boolean>> code_id_map = new HashMap<String, HashMap<String, Boolean>>();
		while ((line = br.readLine()) != null) {
			String[] elements = line.split("\t");
			String id = elements[0].trim();
			String code = elements[2].trim();

			if (id_code_map.containsKey(id)) {
				if (id_code_map.get(id).containsKey(code)) {
					id_code_map.get(id).put(code, id_code_map.get(id).get(code) + 1);
				} else {
					id_code_map.get(id).put(code, 1);
				}

			} else {
				HashMap<String, Integer> map = new HashMap<>();
				map.put(code, 1);
				id_code_map.put(id, map);
			}

		}
		// System.out.println(id_code_map);

		for (Entry<String, HashMap<String, Integer>> entry_1 : id_code_map.entrySet()) {
			for (Entry<String, Integer> entry_2 : entry_1.getValue().entrySet()) {

				if (code_id_map.containsKey(entry_2.getKey())) {
					if (entry_2.getValue() > 3) {
						code_id_map.get(entry_2.getKey()).put(entry_1.getKey(), true);
					} else {
						code_id_map.get(entry_2.getKey()).put(entry_1.getKey(), false);
					}

				} else {
					HashMap<String, Boolean> map = new HashMap<>();
					if (entry_2.getValue() > 3) {
						map.put(entry_1.getKey(), true);
					} else {
						map.put(entry_1.getKey(), false);
					}
					code_id_map.put(entry_2.getKey(), map);
				}
			}
		}

		ArrayList<String> code_list = new ArrayList<>();
		ArrayList<String> id_list = new ArrayList<>();
		HashSet<String> all_code = new HashSet<>();
		for (Entry<String, HashMap<String, Boolean>> entry_1 : code_id_map.entrySet()) {
			int true_count = 0;
			int false_count = 0;
			for (Entry<String, Boolean> entry_2 : entry_1.getValue().entrySet()) {
				if (entry_2.getValue()) {
					true_count++;
				} else {
					false_count++;
				}
			}
			all_code.add(entry_1.getKey());
			if (true_count >= 10 || false_count >= 10) { // original is 200
				if (!code_list.contains(entry_1.getKey())) {
					code_list.add(entry_1.getKey());
				}
			}
			// System.out.println(entry_1.getKey()+" "+true_count+"
			// "+false_count);
		}

		for (Entry<String, HashMap<String, Integer>> entry : id_code_map.entrySet()) {
			if (!id_list.contains(entry.getKey())) {
				id_list.add(entry.getKey());
			}
		}

		System.out.println("all code size: " + all_code.size());
		System.out.println("code_list size: " + code_list.size());
		System.out.println("id_list size: " + id_list.size());

		HashMap<String, HashSet<String>> icd_phecode_map = readICDTOPHECODE(
				"phecode_icd9_map_unrolled.csv");
		HashMap<String, String> phecode_description_map = readPHECODEDESCRIPTION(
				"phecode_definitions1.2.csv");
		ArrayList<String> phecode_list = new ArrayList<>();
		for (String string : code_list) {
			if (icd_phecode_map.containsKey(string)) {
				for (String p_code : icd_phecode_map.get(string)) {
					if (!phecode_list.contains(p_code)) {
						phecode_list.add(p_code);
					}
				}
			}
		}

		HashMap<String, HashMap<String, Integer>> id_phecode_map = new HashMap<String, HashMap<String, Integer>>();

		for (Entry<String, HashMap<String, Integer>> entry_1 : id_code_map.entrySet()) {
			HashMap<String, Integer> map_tmp = new HashMap<>();
			for (Entry<String, Integer> entry_2 : entry_1.getValue().entrySet()) {
				if (icd_phecode_map.containsKey(entry_2.getKey())) {
					for (String p_code : icd_phecode_map.get(entry_2.getKey())) {

						if (map_tmp.containsKey(p_code)) {
							map_tmp.put(p_code, map_tmp.get(p_code) + entry_2.getValue());
						} else {
							map_tmp.put(p_code, entry_2.getValue());
						}

					}
				}

			}

			id_phecode_map.put(entry_1.getKey(), map_tmp);
		}

		BufferedWriter bw_1 = new BufferedWriter(new FileWriter(new File(outFile1)));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < code_list.size(); i++) {
			sb.append(code_list.get(i)).append(" \t ");
		}
		bw_1.write("patient \t " + sb.toString() + "\n");

		for (int i = 0; i < id_list.size(); i++) {
			StringBuffer sb_1 = new StringBuffer();
			for (int j = 0; j < code_list.size(); j++) {
				Integer value = id_code_map.get(id_list.get(i)).get(code_list.get(j));
				if (value != null) {
					sb_1.append(value).append(" \t ");
				} else {
					sb_1.append(0).append(" \t ");
				}

			}
			bw_1.write(id_list.get(i) + " \t " + sb_1.toString() + "\n");
		}
		bw_1.flush();
		bw_1.close();

		BufferedWriter bw_2 = new BufferedWriter(new FileWriter(new File(outFile2)));
		sb = new StringBuffer();
		for (int i = 0; i < phecode_list.size(); i++) {
			sb.append(phecode_list.get(i)).append(" \t ");
		}
		bw_2.write("patient \t " + sb.toString() + "\n");

		System.out.println(phecode_description_map);
		sb = new StringBuffer();
		for (int i = 0; i < phecode_list.size(); i++) {
			System.out.println(phecode_list.get(i) + " " + phecode_description_map.get(phecode_list.get(i)));
			sb.append(phecode_description_map.get(phecode_list.get(i))).append("\t");
		}
		bw_2.write(" \t " + sb.toString() + "\n");

		for (int i = 0; i < id_list.size(); i++) {
			StringBuffer sb_1 = new StringBuffer();
			for (int j = 0; j < phecode_list.size(); j++) {
				Integer value = id_phecode_map.get(id_list.get(i)).get(phecode_list.get(j));
				if (value != null) {
					sb_1.append(value).append("\t");
				} else {
					sb_1.append(0).append("\t");
				}

			}
			bw_2.write(id_list.get(i) + "\t" + sb_1.toString() + "\n");
		}
		bw_2.flush();
		bw_2.close();
	}

	public static HashMap<String, HashSet<String>> readICDTOPHECODE(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		int i = 0;
		HashMap<String, HashSet<String>> map = new HashMap<>();
		while ((line = br.readLine()) != null) {
			i++;
			if (i == 1) {
				continue;
			}
			line = line.replaceAll("\"", "");
			String[] elements = line.split(",");

			if (map.containsKey(elements[0])) {
				map.get(elements[0]).add(elements[1]);
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(elements[1]);
				map.put(elements[0], set);
			}
		}
		return map;
	}

	public static HashMap<String, String> readPHECODEDESCRIPTION(String file) throws IOException {

		Reader reader = Files.newBufferedReader(Paths.get(file));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> csvRecords = csvParser.getRecords();
		HashMap<String, String> map = new HashMap<>();
		int i = 0;
		for (CSVRecord csvRecord : csvRecords) {
			i++;
			if (i > 1) {
				String code = csvRecord.get(0);
				String description = csvRecord.get(1).trim();
				map.put(code, description);
			}

		}
		return map;
	}

}
