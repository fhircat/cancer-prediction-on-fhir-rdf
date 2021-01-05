package method;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

public class FHIRtoRDF_Writer {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		HashSet<String> triples =new HashSet<>();
		FHIRtoRDF_Writer.convert("example_1_simple.json", triples);
		FHIRtoRDF_Writer.convert("example_2_simple.json", triples);
		writeToFile(triples , "example_merge_simple.nt"); 
	}

	public static void writeToFile(HashSet<String> triples , String outfile) throws IOException{
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File(outfile)));
		for(String triple:triples){
			bw.write(triple+" \n");
		}
		bw.flush();
		bw.close();
		
	}
	public static void convert(String inputFile, 
			HashSet<String> triples) throws UnsupportedEncodingException, IOException{
		
		String uri_label="<http://www.w3.org/2000/01/rdf-schema#label>";
		String uri_type="<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
		
		String content = new String(Files.readAllBytes(Paths.get(inputFile)), "UTF-8");
		FhirContext ctx = FhirContext.forR4();

		IParser parser = ctx.newJsonParser();
		
		Bundle bundle = parser.parseResource(Bundle.class, content);

		List<BundleEntryComponent> list_entry = bundle.getEntry();
		
		String uri_patient="";
		for (int i = 0; i < list_entry.size(); i++) {

			BundleEntryComponent component = list_entry.get(i);
			
			if(component.getResource().getResourceType().toString().equals("Patient")){
				String id=((Patient)component.getResource()).getId().toString().toLowerCase();
				String gender=((Patient)component.getResource()).getGender().toString().toLowerCase();
				String age=((Patient)component.getResource()).getExtension().get(0).getValue().toString().toLowerCase();
				String uri_id="<https://BD2KOnFHIR/fhirtordf/Patient/"+id+">";
				String uri_age="<https://BD2KOnFHIR/fhirtordf/Patient/Age/"+age+">";
				String uri_gender="<https://BD2KOnFHIR/fhirtordf/Patient/Gender/"+gender+">";
				String uri_hasAge="<https://BD2KOnFHIR/fhirtordf/hasAge>";
				String uri_hasGender="<https://BD2KOnFHIR/fhirtordf/hasGender>";
				
				uri_patient=uri_id;
				
				triples.add(uri_id+" "+uri_hasAge+" "+uri_age+" .");
				triples.add(uri_id+" "+uri_hasGender+" "+uri_gender+" .");
				
				triples.add(uri_age+" "+uri_label+" \""+age+"\" .");
				triples.add(uri_gender+" "+uri_label+" \""+gender+"\" .");
				triples.add(uri_id+" "+uri_type+" "+"<https://BD2KOnFHIR/fhirtordf/Patient>"+" .");
			}
			
			if(component.getResource().hasMeta()){

				if(component.getResource().getMeta().getProfile().get(0).getValue().equals("https://www.hl7.org/fhir/observation-genetic.html")){
					
					List<Extension> extensions=((Observation)component.getResource()).getExtension();
					String uri_hasMutatedGene="<https://BD2KOnFHIR/fhirtordf/hasMutatedGene>";
					for(Extension extension:extensions){
						String uri_gene="<https://BD2KOnFHIR/fhirtordf/Gene/"+extension.getValue()+">";
						triples.add(uri_patient+" "+uri_hasMutatedGene+" "+uri_gene+" .");
						triples.add(uri_gene+" "+uri_label+" \""+extension.getValue()+"\" .");
						triples.add(uri_gene+" "+uri_type+" "+"<https://BD2KOnFHIR/fhirtordf/Gene>"+" .");
						
					}
				}
			}
			
			
			if(component.getResource().getResourceType().toString().equals("Observation")){
				if (((Observation) component.getResource()).hasCode()) {
					if (((Observation) component.getResource()).getCode().hasCoding()) {
						for (Coding coding : ((Observation) component.getResource()).getCode().getCoding()) {
							String code=coding.getCode();
							String uri_labtest="<https://BD2KOnFHIR/fhirtordf/Labtest/"+code+">";
							String value=((Observation) component.getResource()).getValue().toString();
							String uri_value="<https://BD2KOnFHIR/fhirtordf/Labtest_value/"+code+"("+value+")"+">";
							triples.add(uri_patient+" "+uri_labtest+" "+uri_value+" .");
							triples.add(uri_value+" "+uri_label+" \""+code+"("+value+")"+"\" .");
							triples.add(uri_value+" "+uri_type+" "+"<https://BD2KOnFHIR/fhirtordf/Labtest_value>"+" .");
						}
					}
				}
			}
			
			if(component.getResource().getResourceType().toString().equals("Condition")){
				if (((Condition) component.getResource()).hasCode()) {
					if (((Condition) component.getResource()).getCode().hasCoding()) {
						for (Coding coding : ((Condition) component.getResource()).getCode().getCoding()) {
							String code=coding.getCode();
							String uri_code="<https://BD2KOnFHIR/fhirtordf/Diagnosis/"+code+">";
							String uri_hasCondition="<https://BD2KOnFHIR/fhirtordf/hasCondition>";
							triples.add(uri_patient+" "+uri_hasCondition+" "+uri_code+" .");
							triples.add(uri_code+" "+uri_label+" \""+((Condition) component.getResource()).getCode().getText()+"\" .");
							triples.add(uri_code+" "+uri_type+" "+"<https://BD2KOnFHIR/fhirtordf/Diagnosis>"+" .");
						}
					}
				}
			}
			
			if(component.getResource().getResourceType().toString().equals("c")){
				if (((Medication) component.getResource()).hasCode()) {
					if (((Medication) component.getResource()).getCode().hasCoding()) {
						for (Coding coding : ((Medication) component.getResource()).getCode().getCoding()) {
							String code=coding.getCode();
							String uri_code="<https://BD2KOnFHIR/fhirtordf/Medication/"+code+">";
							String uri_hasMedication="<https://BD2KOnFHIR/fhirtordf/hasMedication>";
							triples.add(uri_patient+" "+uri_hasMedication+" "+uri_code+" .");
							triples.add(uri_code+" "+uri_label+" \""+((Medication) component.getResource()).getCode().getText()+"\" .");
							triples.add(uri_code+" "+uri_type+" "+"<https://BD2KOnFHIR/fhirtordf/Medication>"+" .");
						}
					}
				}
			}

			if(component.getResource().getResourceType().toString().equals("FamilyMemberHistory")){
				if (((FamilyMemberHistory) component.getResource()).hasCondition()) {
					for(FamilyMemberHistoryConditionComponent condition:((FamilyMemberHistory) component.getResource()).getCondition()){
						if (condition.getCode().hasCoding()) {
							for (Coding coding : condition.getCode().getCoding()) {
								String code=coding.getCode();
								String uri_code="<https://BD2KOnFHIR/fhirtordf/Diagnosis/"+code+">";
								String uri_FamilyHistoryCondition="<https://BD2KOnFHIR/fhirtordf/hasFamilyHistoryCondition>";
								triples.add(uri_patient+" "+uri_FamilyHistoryCondition+" "+uri_code+" .");
								triples.add(uri_code+" "+uri_label+" \""+condition.getCode().getText()+"\" .");
								triples.add(uri_code+" "+uri_type+" "+"<https://BD2KOnFHIR/fhirtordf/Diagnosis>"+" .");
							}
						}
					}
					
				}
			}
			
		}

	}
	
}
