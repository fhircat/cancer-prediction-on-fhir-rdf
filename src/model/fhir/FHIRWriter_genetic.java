package model.fhir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent;
import org.json.JSONException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import data.LabtestBean;
import data.PatientBean;
import data.Prediciton_PatientBean;
import data.dataCollection.GetPatientList;

public class FHIRWriter_genetic {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	}
	
	public static void getGeneticData(String outdir) throws Exception {

		HashSet<PatientBean> beans = GetPatientList.getPatientNumberandNameFromGeneReports();
		HashMap<String, HashSet<String>> map = new HashMap<>();
		for (PatientBean bean : beans) {
			String id=bean.getClinical_number().toString();
			HashSet<String> local_genes = bean.getGenes();
			map.put(id, local_genes);
		}
		new File(outdir).mkdirs();
		writeFHIR_gene(
				map,
				 outdir);
	}	
	
	public static void writeFHIR_gene(
			HashMap<String, HashSet<String>> data_genes,
			String outdir) throws Exception {
		
		
		for(Entry<String,HashSet<String>> entry:data_genes.entrySet()){
			
			String id=entry.getKey();
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
			
			
			Observation_Genetics observation_genetics=new Observation_Genetics();
			bundle.addEntry()
			   .setResource(observation_genetics);
			if(data_genes.containsKey(id)){
				
				for(String string:data_genes.get(id)){
					observation_genetics.addGene(new StringType(string));
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
