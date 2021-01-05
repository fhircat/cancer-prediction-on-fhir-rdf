package data.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.dependencyNegation.NegationDetection;
import data.dependencyNegation.SimpleSentenceSplitter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Parser_historical {

	public static void main(String[] args) throws ParseException, IOException{
	}
	public static HashMap<BigInteger,HashSet<String>> getHistorical(Set<BigInteger> patientList) throws ParseException, IOException{
		
		HashMap<Integer,BigInteger> map= new HashMap<>();
		
		updatePatientList("",
				 map); 
		
		updatePatientList("",
				 map); 
		
		HashMap<String,HashSet<String>> snToICD=getSNtoICD("/fh/concept_dict.json", 
				"ICD9CM_SNOMED_MAP_1TOM_201912.txt",
				"ICD9CM_SNOMED_MAP_1TO1_201912.txt") ;
		
		SimpleSentenceSplitter spliter=new SimpleSentenceSplitter();
		
		NegationDetection detector=new NegationDetection();
		HashSet<String> neg_patterns=detector.getNegTrigger();
		
		HashMap<BigInteger,HashSet<String>> disease_map=new HashMap<>();
		
		for(File file:new File("fh/tokenized_fhir").listFiles()){
			HashSet<String> local_disease= readFhir(file.getAbsolutePath(),  spliter, neg_patterns);
			String name=file.getName();
			name=name.replace(".json.txt", "");
			
			Integer name_i=Integer.valueOf(name);
			BigInteger id = null;
			
			if(map.containsKey(name_i)){
				id=map.get(name_i);
			}
			
			HashSet<String> diseases=new HashSet<>();
			for(String string:local_disease){
				if(snToICD.containsKey(string)){
					diseases.addAll(snToICD.get(string));
				}
			}
			
			if(patientList.contains(id)){
				disease_map.put(id, diseases);	
			}
			
		}
		

		return disease_map;
	}
	
	public static HashSet<String>  readFhir(String file, SimpleSentenceSplitter spliter,HashSet<String> neg_patterns) throws IOException{
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		HashSet<String> diseases=new HashSet<>();
		String line=null;
		NegationDetection detector=new NegationDetection();
		HashSet<String> patterns=detector.getNegTrigger();
		while((line=br.readLine())!=null){
			String[] sens=spliter.split(line);
			
			for(String sen:sens){
				if(sen.contains(" SN_")){
					if(!detector.getNeg(sen, patterns)){
						Pattern pattern = Pattern.compile("\\s+SN_[0-9]+\\s*");
						Matcher matcher = pattern.matcher(sen);
						while (matcher.find())
						{
							diseases.add(matcher.group(0).trim());
						}
						
					}
				}
				
			}
		}
		return diseases;	
	}
	
	public static void updatePatientList(String file,
			HashMap<Integer,BigInteger> map) throws FileNotFoundException, ParseException{
		 	Object obj = new JSONParser().parse(new FileReader(file)); 
	        // typecasting obj to JSONObject 
	        JSONObject jo = (JSONObject) obj; 
	          
	        for(String key:jo.keySet()){
//	        	System.out.println(key+" "+jo.get(key).toString());
	        	String value=jo.get(key).toString().trim();
	        	key=key.trim();
	        	int key_i=Integer.valueOf(key);
	        	if(map.containsKey(key_i)){
	        		if(!map.get(key_i).equals(new BigInteger(value))){
	        			System.exit(0);
	        		}
	        	}else{
	        		map.put(key_i, new BigInteger(value));
	        	}
	        }
		
	}
	
	public static HashMap<String,HashSet<String>> getSNtoICD(String dictFile, String mappingFile_1, String mappingFile_2) throws ParseException, IOException{
		 	Object obj = new JSONParser().parse(new FileReader(dictFile)); 
	        // typecasting obj to JSONObject 
	        JSONObject jo = (JSONObject) obj; 
	        HashSet<String> SNs=new HashSet<>(); 
	        for(String key:jo.keySet()){
	        	String value=jo.get(key).toString().trim();
	        	key=key.trim();
	        	if(key.contains("SN_")){
	        		SNs.add(key.trim());
	        	}
	        }
	        HashMap<String,String> map=new HashMap<>();
	        HashMap<String,HashSet<String>> snToIcd=new HashMap<>();
	        BufferedReader br =new BufferedReader(new FileReader(new File(mappingFile_1)));
	        String line=null;
	        while((line=br.readLine())!=null){
	        	String[] elements=line.split("	");
	        	if(snToIcd.containsKey("SN_"+elements[7].trim())){
	        		snToIcd.get("SN_"+elements[7].trim()).add(elements[0].trim());
	        	}else{
	        		HashSet<String> set=new HashSet<>();
	        		set.add(elements[0].trim());
	        		snToIcd.put("SN_"+elements[7].trim(), set);
	        	}
	        }
	        
	        br =new BufferedReader(new FileReader(new File(mappingFile_2)));
	        line=null;
	        while((line=br.readLine())!=null){
	        	String[] elements=line.split("	");
	        	if(snToIcd.containsKey("SN_"+elements[7].trim())){
	        		snToIcd.get("SN_"+elements[7].trim()).add(elements[0].trim());
	        	}else{
	        		HashSet<String> set=new HashSet<>();
	        		set.add(elements[0].trim());
	        		snToIcd.put("SN_"+elements[7].trim(), set);
	        	}
	        }
	        
	        return snToIcd;
	}
}
