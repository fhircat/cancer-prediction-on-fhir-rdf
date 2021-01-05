package data.parser;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONException;

import data.PatientBean;
import data.dataCollection.GetPatientList;

public class Parser_geneReport {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static HashMap<BigInteger,HashSet<String>> getGenes(Set<BigInteger> patientList) throws JSONException, IOException, ParseException{
		HashMap<BigInteger, HashSet<String>> mrn_map=initial();
		HashMap<BigInteger, HashSet<String>> gene_all_map=getGeneticData( mrn_map);
		HashMap<BigInteger, HashSet<String>> gene_map=new HashMap<>();
		for(Entry<BigInteger, HashSet<String>> entry:gene_all_map.entrySet()){
			if(patientList.contains(entry.getKey())){
				gene_map.put(entry.getKey(), entry.getValue());
			}
		}
		return gene_map;
	}
	
	
	public static HashMap<BigInteger, HashSet<String>>  initial() throws IOException {
		HashMap<BigInteger, HashSet<String>> mrn_map=new HashMap();
		Reader reader = Files.newBufferedReader(Paths.get(
				"foundation_clinInfo_nansu_refined_validated.csv"));
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
					BigInteger patient_num = new BigInteger(csvRecord.get(0));
					String file = "data/genetics_results/xml/" + csvRecord.get(1)
							+ ".xml";
					if (mrn_map.containsKey(patient_num)) {
						mrn_map.get(patient_num).add(file);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(file);
						mrn_map.put(patient_num, set);

					}
				}
			}
		}
		return mrn_map;
	}

	public static HashMap<BigInteger, HashSet<String>> getGeneticData(HashMap<BigInteger, HashSet<String>> mrn_map) throws IOException, JSONException, ParseException {

		
		HashMap<BigInteger, HashSet<String>> gene_map=new HashMap<>();
		
		HashSet<PatientBean> beans = GetPatientList.getPatientNumberandNameFromGeneReports();
		HashMap<String, PatientBean> map = new HashMap<>();
		for (PatientBean bean : beans) {
			String file = bean.getFile_name();
			map.put(file, bean);
		}
		for (Entry<BigInteger, HashSet<String>> entry : mrn_map.entrySet()) {
			HashSet<String> genes = new HashSet<>();
			String latest_reprot_date = null;
			HashSet<String> genetic_files = new HashSet<>();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			HashSet<String> genetic_diagnosis = new HashSet<>();
			for (String file : entry.getValue()) {
				genetic_files.add(file);
				PatientBean bean = map.get(file);
				HashSet<String> local_genes = bean.getGenes();
				String genetic_reprot_date = bean.getGeneReportDate();
				String submittedDiagnosis = bean.getSubmittedDiagnosis();
				genetic_diagnosis.add(submittedDiagnosis);
				if (latest_reprot_date == null) {
					latest_reprot_date = genetic_reprot_date;
				} else {
					if (dateFormat.parse(latest_reprot_date).before(dateFormat.parse(genetic_reprot_date))) {
						latest_reprot_date = genetic_reprot_date;
					}
				}
				genes.addAll(local_genes);
			}
			if(genes.contains("Tumor Mutation Burden")){
				genes.remove("Tumor Mutation Burden");	
			}
			if(genes.contains("Microsatellite status")){
				genes.remove("Microsatellite status");
			}
			
			gene_map.put(entry.getKey(), genes);
			
		}
		return gene_map;
	}
}
