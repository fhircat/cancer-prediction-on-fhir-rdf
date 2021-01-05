package data.fhir;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;


@ResourceDef(name="Observation", profile="https://www.hl7.org/fhir/observation-genetic.html")

public class Observation_Genetics extends Observation{
	
	 private static final long serialVersionUID = 1L;

	  
	   @Child(name="gene", max=Child.MAX_UNLIMITED)   
	   @Extension(url="https://www.hl7.org/fhir/extension-observation-geneticsgene.html", definedLocally=false, isModifier=false)
	   @Description(shortDefinition="The genetic alterations in the genetic reprot")
	   private List<StringType> genes;

	   /**
	    * It is important to override the isEmpty() method, adding a check for any
	    * newly added fields. 
	    */
	   @Override
	   public boolean isEmpty() {
	      return super.isEmpty() && ElementUtil.isEmpty(genes);
	   }
	   
	   public List<StringType> getGenes() {
	      if (genes == null) {
	    	  genes=new ArrayList<StringType>();
	      }
	      return genes;
	   }

	   public void addGene(StringType gene) {
		      if (genes == null) {
		    	  genes=new ArrayList<StringType>();
		      }
		      genes.add(gene);
	   }

}
