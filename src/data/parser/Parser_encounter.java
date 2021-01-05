package data.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import data.EncounterBean;

public class Parser_encounter {
	
	public static void main(String[] args) throws IOException, ParseException{
	
	}
	public static HashMap<BigInteger,HashMap<String,HashSet<EncounterBean>>> readEncounterTab(String file) throws IOException, ParseException{
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<BigInteger,HashMap<String,HashSet<EncounterBean>>> map=new HashMap();
		HashSet<String> b_encounters=new HashSet<>();
		HashSet<String> e_encounters=new HashSet<>();
		
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=5){
				continue;
			}
			BigInteger id=new BigInteger(content[0]);  
			String ehr_encounter=content[1];  
			String billing_encounter=content[2];
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(content[4]);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );
			
			b_encounters.add(billing_encounter);
			e_encounters.add(ehr_encounter);
			
			if(!billing_encounter.equals("")){
				if(map.containsKey(id)){
					
					
						if(map.get(id).containsKey(billing_encounter)){
							EncounterBean bean =new EncounterBean();
							bean.setBilling_encounter(billing_encounter);
							bean.setEHR_encounter(ehr_encounter);
							bean.setDate(formatted_date);
							bean.setId(id);
							map.get(id).get(billing_encounter).add(bean);		
						}else{
							HashSet<EncounterBean> local_set=new HashSet<>();
							EncounterBean bean =new EncounterBean();
							bean.setBilling_encounter(billing_encounter);
							bean.setEHR_encounter(ehr_encounter);
							bean.setDate(formatted_date);
							bean.setId(id);
							local_set.add(bean);
							map.get(id).put(billing_encounter, local_set);
						}
					
				}else{
					HashMap<String,HashSet<EncounterBean>> local_map=new HashMap<>();
					HashSet<EncounterBean> local_set=new HashSet<>();
					EncounterBean bean =new EncounterBean();
					bean.setBilling_encounter(billing_encounter);
					bean.setEHR_encounter(ehr_encounter);
					bean.setDate(formatted_date);
					bean.setId(id);
					local_set.add(bean);
					local_map.put(billing_encounter, local_set);
					map.put(id, local_map);
				}
			}
			
		}
		
		System.out.println("b encounter size: "+b_encounters.size());
		System.out.println("e encounter size: "+e_encounters.size());
		System.out.println("encounter size: "+map.size());
		return map;
	}
}
