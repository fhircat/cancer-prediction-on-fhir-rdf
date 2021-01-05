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
import java.util.Set;

import data.DateCalculator;

public class Parser_medication {
	
	public static HashMap<BigInteger, HashSet<String>> readMedication_time(String file, Set<BigInteger> patientList,
			int numberOfMonth, 
			HashMap<BigInteger,String> diagnoizedDate) throws IOException, ParseException{
		int numberOfDays = numberOfMonth*30;
		
		HashMap<BigInteger, HashSet<String>> map=new HashMap<>();
		HashMap<BigInteger, HashSet<String>> map_all=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=9){
				continue;
			}
			
			BigInteger id=new BigInteger(content[0]);  
			String encounter=content[1].toString().trim();
			String code=content[3].toString().trim();
			
			
			
			if(!patientList.contains(id)){
				continue;
			}
			
			String date_tmp = content[6].toString().trim();
			String pattern = "MM/dd/yyyy";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date date_tmp_1 = simpleDateFormat.parse(date_tmp);
			String date = simpleDateFormat.format(date_tmp_1);
			
			String base_data=diagnoizedDate.get(id);
			
			if (DateCalculator.differenceDate(base_data, date) > numberOfDays){
				if(map_all.containsKey(id)){
					map_all.get(id).add(code);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(code);
					map_all.put(id, set);
				}
				
				if(map.containsKey(id)){
					map.get(id).add(code);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(code);
					map.put(id, set);
				}
			
			}
		}
		
		System.out.println("medication all: "+map_all.size());
		return map;
		
	}
	
	
	public static HashMap<BigInteger, HashSet<String>> readMedication(String file, Set<BigInteger> patientList,HashMap<BigInteger,HashSet<String>> removedEncounter) throws IOException{
		HashMap<BigInteger, HashSet<String>> map=new HashMap<>();
		HashMap<BigInteger, HashSet<String>> map_all=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=9){
				continue;
			}
			
			BigInteger id=new BigInteger(content[0]);  
			String encounter=content[1].toString().trim();
			String code=content[3].toString().trim();
			
			
			
			if(!patientList.contains(id)){
				continue;
			}
			
			
			if(map_all.containsKey(id)){
				map_all.get(id).add(code);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(code);
				map_all.put(id, set);
			}
			
			if(removedEncounter.containsKey(id)){
				if(removedEncounter.get(id).contains(encounter)){
					continue;
				}
			}
				
			if(map.containsKey(id)){
					map.get(id).add(code);
			}else{
					HashSet<String> set=new HashSet<>();
					set.add(code);
					map.put(id, set);
			}
			
		}
		
		return map;
	}
}
