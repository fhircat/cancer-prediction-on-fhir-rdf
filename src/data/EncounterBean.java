package data;

import java.math.BigInteger;

public class EncounterBean {

		BigInteger id;
		String EHR_encounter;
		String Billing_encounter;
		String date;
		
		public BigInteger getId() {
			return id;
		}
		public void setId(BigInteger id) {
			this.id = id;
		}
		public String getEHR_encounter() {
			return EHR_encounter;
		}
		public void setEHR_encounter(String eHR_encounter) {
			EHR_encounter = eHR_encounter;
		}
		public String getBilling_encounter() {
			return Billing_encounter;
		}
		public void setBilling_encounter(String billing_encounter) {
			Billing_encounter = billing_encounter;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		
	
}
