package data.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import data.DateCalculator;
import data.DiagnosisBean;

import java.util.Set;

public class Parser_diagnosis {
	
	
	public static String icd_code_secondCancer(String string){
		
		String d_code="0.0";
		if(string.matches(".*[a-zA-Z]+.*")){
			if(string.toLowerCase().contains("c")){
					boolean continue_process=false;
					if(string.toLowerCase().startsWith("c77")){
						continue_process=true;
					}
					if(string.toLowerCase().startsWith("c78")){
						continue_process=true;
					}
					if(string.toLowerCase().startsWith("c79")){
						continue_process=true;
					}
					if(continue_process){
						d_code=string.toLowerCase();
					}
			}
		}else{
			Double code=Double.valueOf(string);
			if(code>=140 &&code <=209){
				boolean continue_process=false;
				if(string.toLowerCase().startsWith("196")){
					continue_process=true;
				}
				if(string.toLowerCase().startsWith("197")){
					continue_process=true;
				}
				if(string.toLowerCase().startsWith("198")){
					continue_process=true;
				}
				
				if(continue_process){
					d_code=string.toLowerCase();
				}
			}
		}
		return d_code;
	}
	
	
	public static String icd_code(String string){
		
		String d_code="0.0";
		if(string.matches(".*[a-zA-Z]+.*")){
			if(string.toLowerCase().contains("c")){
				if(!string.toLowerCase().startsWith("c7b")){
					boolean continue_process=true;
					if(string.toLowerCase().startsWith("c76")){
						continue_process=false;
					}
					if(string.toLowerCase().startsWith("c77")){
						continue_process=false;
					}
					if(string.toLowerCase().startsWith("c78")){
						continue_process=false;
					}
					if(string.toLowerCase().startsWith("c79")){
						continue_process=false;
					}
					if(string.toLowerCase().startsWith("c80")){
						continue_process=false;
					}
					if(continue_process){
						d_code=string.toLowerCase();
					}
				}
		}
		}else{
			Double code=Double.valueOf(string);
			if(code>=140 &&code <=209){
				boolean continue_process=true;
				if(string.toLowerCase().startsWith("196")){
					continue_process=false;
				}
				if(string.toLowerCase().startsWith("197")){
					continue_process=false;
				}
				if(string.toLowerCase().startsWith("198")){
					continue_process=false;
				}
				if(string.toLowerCase().startsWith("199")){
					continue_process=false;
				}
				if(continue_process){
					d_code=string.toLowerCase();
				}
			}
		}
		return d_code;
	}
	
	public static String icd_code_sevenCases(String string){
		ArrayList<String> code9=new ArrayList<>();
		code9.add("171");
		code9.add("155");
		code9.add("157");
		code9.add("153");
		code9.add("193");
		code9.add("174");
		code9.add("162");
		code9.add("185");
		code9.add("183");
		
		ArrayList<String> code10=new ArrayList<>();
		code10.add("c49");
		code10.add("c22");
		code10.add("c25");
		code10.add("c18");
		code10.add("c73");
		code10.add("c50");
		code10.add("c34");
		code10.add("c61");
		code10.add("c56");
		String d_code="0.0";
		if(string.contains("c")){
			for (int i = 0; i < code10.size(); i++) {
				if(string.startsWith(code10.get(i))){
					d_code=code9.get(i);
				}
			}
			
		}else{
			for (int i = 0; i < code9.size(); i++) {
				if(string.startsWith(code9.get(i))){
					d_code=code9.get(i);
				}
			}
		}
		return d_code;
	}
	
	public static HashMap<BigInteger,HashMap<String,Integer>> readDiagnosisTab_metastasis(String file, 
			HashMap<BigInteger,String> cases) throws IOException, ParseException{
		
		
		HashMap<String,String> icd9=new HashMap<>();
		icd9.put("171.9", "171");
		icd9.put("155", "155");
		icd9.put("157.9", "157");
		icd9.put("153.9", "153");
		icd9.put("193", "193");
		icd9.put("174.9", "174");
		icd9.put("162.9", "162");
		icd9.put("185", "185");
		icd9.put("183", "183");
		
		HashMap<String,String> icd10=new HashMap<>();
		icd10.put("171.9", "c49");
		icd10.put("155", "c22");
		icd10.put("157.9", "c25");
		icd10.put("153.9", "c18");
		icd10.put("193", "c73");
		icd10.put("174.9", "c50");
		icd10.put("162.9", "c34");
		icd10.put("185", "c61");
		icd10.put("183", "c56");
		
		
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<BigInteger,HashMap<String,String>> map_tmp=new HashMap<>();
		HashMap<BigInteger,String> map_mainDate=new HashMap<>();
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=5){
				continue;
			}
			BigInteger id=new BigInteger(content[0]);  
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(content[1]);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );
			
			String icd=content[2];
			String billing_encountger=content[4];
			
			if(cases.containsKey(id)){
				String main_code=cases.get(id);
				
				if(icd9.containsKey(main_code)){
					String icd_9=icd9.get(main_code);
					String icd_10=icd10.get(main_code);
					if(icd.toLowerCase().startsWith(icd_9)||
							icd.toLowerCase().startsWith(icd_10)){
					}
					if(map_mainDate.containsKey(id)){
						String formal_date=map_mainDate.get(id);
						if(DateCalculator.differenceDate(formal_date,formatted_date)>0){
							map_mainDate.put(id, formatted_date);	
						}
					}else{
						map_mainDate.put(id, formatted_date);	
					}
					
				}
				
				
				if(icd_code_secondCancer(icd)!="0.0"){
					
					if(map_tmp.containsKey(id)){
						
						if(map_tmp.get(id).containsKey(icd_code_secondCancer(icd))){
							String formal_date=map_tmp.get(id).get(icd_code_secondCancer(icd));
							if(DateCalculator.differenceDate(formal_date,formatted_date)>0){
								map_tmp.get(id).put(icd_code_secondCancer(icd),formatted_date);
							}
							
						}else{
							map_tmp.get(id).put(icd_code_secondCancer(icd),formatted_date);
						}
					}else{
						HashMap<String,String> map=new HashMap<>();
						map.put(icd_code_secondCancer(icd),formatted_date);
						map_tmp.put(id, map);
					}
					
				}
			}
			
		}
		
		System.out.println("secondary size: "+ map_tmp.size());
		
		HashMap<BigInteger,String> no_metastasis=new HashMap<>();
		
		for(Entry<BigInteger,String> entry: cases.entrySet()){
			if(!map_tmp.containsKey(entry.getKey())){
				no_metastasis.put(entry.getKey(), entry.getValue());
			}
		}
		
		HashMap<BigInteger,HashMap<String,Integer>> return_map=new HashMap<>(); // metastasis, days
		
		for(Entry<BigInteger,HashMap<String,String>> entry:map_tmp.entrySet()){
			String main_date=map_mainDate.get(entry.getKey());
			
			HashMap<String,Integer> sub_map=new HashMap<>();
			for(Entry<String,String> entry_2:entry.getValue().entrySet()){
				String metastatis_datea=entry_2.getValue();
				long days=DateCalculator.differenceDate( metastatis_datea, main_date);
				sub_map.put(entry_2.getKey(), (int)days);
			}
			return_map.put(entry.getKey(), sub_map);
		}
		
		for(Entry<BigInteger,HashMap<String,Integer>> entry:return_map.entrySet()){
			System.out.println(entry.getKey()+" -> "+entry.getValue());
		}
		
		System.out.println("secondary size: "+ map_tmp.size());
		System.out.println("case size: "+ cases.size());
		System.out.println("no_metastasis size: "+ no_metastasis.size());
		
		

		return return_map;
	}
	
	
	
	public static HashMap<BigInteger,String> readDiagnosisTab_unknownCases(String file, 
			HashSet<BigInteger> cases) throws IOException, ParseException{
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<BigInteger,Tempbean> map_tmp=new HashMap<>();
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=5){
				continue;
			}
			BigInteger id=new BigInteger(content[0]);  
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(content[1]);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );
			
			String icd=content[2];
			String billing_encountger=content[4];
			
			if(cases.contains(id)){
				if(icd_code(icd)!="0.0"){
					
					if(map_tmp.containsKey(id)){
						String best_date=map_tmp.get(id).getDate();
						if(DateCalculator.differenceDate(best_date,formatted_date)>0){
							Tempbean bean=new Tempbean();
							bean.setCode(icd_code(icd));
							bean.setDate(formatted_date);
							map_tmp.put(id, bean);
						}
					}else{
						Tempbean bean=new Tempbean();
						bean.setCode(icd_code(icd));
						bean.setDate(formatted_date);
						map_tmp.put(id, bean);
					}
				}
			}
			
		}
		
		System.out.println("diagnosis size: "+ map_tmp.size());
		
		HashMap<BigInteger, String> map=new HashMap<>();
		HashMap<String,HashSet<BigInteger>> unspecified_counter=new HashMap<>();
		for(Entry<BigInteger,Tempbean> entry:map_tmp.entrySet()){
//			map.put(entry.getKey(),  entry.getValue().getCode());
			if(icd_code_sevenCases(entry.getValue().getCode())!="0.0"){
				map.put(entry.getKey(), icd_code_sevenCases(entry.getValue().getCode()));
			}else{
				String code_tmp=entry.getValue().getCode();
				if(entry.getValue().getCode().contains(".")){
					code_tmp=entry.getValue().getCode().substring(0,entry.getValue().getCode().lastIndexOf("."));
				}
				
				if(unspecified_counter.containsKey(code_tmp)){
					unspecified_counter.get(code_tmp).add(entry.getKey());
				}else{
					HashSet<BigInteger> set=new HashSet<>();
					set.add(entry.getKey());
					unspecified_counter.put(code_tmp, set);
				}
				
			}
		}
		for(Entry<String,HashSet<BigInteger>> entry:unspecified_counter.entrySet()){
			System.out.println(entry.getKey()+" --> "+entry.getValue().size());
		}
		
		HashMap<String,String> code9_mapping=new HashMap<>();
		code9_mapping.put("171", "171.9");
		code9_mapping.put("155", "155");
		code9_mapping.put("157", "157.9");
		code9_mapping.put("153", "153.9");
		code9_mapping.put("193", "193");
		code9_mapping.put("174", "174.9");
		code9_mapping.put("162", "162.9");
		code9_mapping.put("185", "185");
		code9_mapping.put("183", "183");
		
		HashMap<BigInteger,String> map_refine=new HashMap<>();
		for(Entry<BigInteger,String> entry:map.entrySet()){
			if(code9_mapping.containsKey(entry.getValue())){
				map_refine.put(entry.getKey(), code9_mapping.get(entry.getValue()));
			}else{
				System.err.println("icd "+entry.getValue()+" not contained !");
				System.exit(0);
			}
		}
		return map_refine;
		
		
	}
	
	public static HashMap<BigInteger,HashMap<String,String>> readDiagnosisTab_time(String file) throws IOException, ParseException{
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<BigInteger,HashMap<String,String>> map=new HashMap();
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=5){
				continue;
			}
			BigInteger id=new BigInteger(content[0]);  
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(content[1]);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );

			String icd=content[2];
			String billing_encountger=content[4];
			
			if(map.containsKey(id)){
				if(map.get(id).containsKey(billing_encountger)){
					String date=map.get(id).get(billing_encountger);
					if(DateCalculator.differenceWithCurrent(date, formatted_date)>0){
						map.get(id).put(billing_encountger, formatted_date);
					}
				}else{
					map.get(id).put(billing_encountger, formatted_date);
				}
				
			}else{
				HashMap<String,String> local_map=new HashMap<>();
				local_map.put(billing_encountger, formatted_date);
				map.put(id, local_map);
			}
		}
		

		System.out.println("diagnosis size: "+map.size());
		return map;
	}
	
	public static HashMap<BigInteger,HashMap<String,DiagnosisBean>> readDiagnosisTab(String file) throws IOException, ParseException{
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<BigInteger,HashMap<String,DiagnosisBean>> map=new HashMap();
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=5){
				continue;
			}
			BigInteger id=new BigInteger(content[0]);  
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(content[1]);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );
//			String pattern = "MM/dd/yyyy";
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//			Date date = simpleDateFormat.parse(formatted_date);
			
			String icd=content[2];
			String billing_encountger=content[4];
			
			if(map.containsKey(id)){
				if(map.get(id).containsKey(billing_encountger)){
					map.get(id).get(billing_encountger).getIcd().add(icd);
				}else{
					DiagnosisBean bean =new DiagnosisBean();
					bean.setBilling_encountger(billing_encountger);
					bean.setDate(formatted_date);
					HashSet<String> icd_set=new HashSet<>();
					icd_set.add(icd);
					bean.setIcd(icd_set);
					bean.setId(id);
					map.get(id).put(billing_encountger, bean);
				}
				
			}else{
				HashMap<String,DiagnosisBean> local_map=new HashMap<>();
				DiagnosisBean bean =new DiagnosisBean();
				bean.setBilling_encountger(billing_encountger);
				bean.setDate(formatted_date);
				HashSet<String> icd_set=new HashSet<>();
				icd_set.add(icd);
				bean.setIcd(icd_set);
				bean.setId(id);
				local_map.put(billing_encountger, bean);
				map.put(id, local_map);
			}
		}
		

		
		System.out.println("diagnosis size: "+map.size());
		return map;
	}
	
	
	public static HashMap<BigInteger,HashSet<String>> readDiagnosisTab_time(String file, Set<BigInteger> patientList, 
			int numberOfMonth, 
			HashMap<BigInteger,String> diagnoizedDate) throws IOException, ParseException{
		
		int numberOfDays = numberOfMonth*30;
		
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<BigInteger,HashSet<String>> map=new HashMap();
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			BigInteger id=new BigInteger(content[0]);  
			
			if(content.length!=5){
				continue;
			}
			if(!patientList.contains(id)){
				continue;
			}
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(content[1]);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );
//			String pattern = "MM/dd/yyyy";
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//			Date date = simpleDateFormat.parse(formatted_date);
			String icd=content[2];
			String billing_encountger=content[4];
			String code=content[2].toString().trim();
			String base_data=diagnoizedDate.get(id);
			
			if (DateCalculator.differenceDate(base_data, formatted_date) > numberOfDays){
				if(map.containsKey(id)){
					map.get(id).add(code);
					
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(code);
					map.put(id, set);
				}
			}
			
			

		}
		return map;
	}
	
	
	public static HashMap<BigInteger,HashSet<String>> readDiagnosisTab(String file, Set<BigInteger> patientList, 
			HashMap<BigInteger,HashSet<String>> removedencounter) throws IOException, ParseException{
	
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<BigInteger,HashSet<String>> map=new HashMap();
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			BigInteger id=new BigInteger(content[0]);  
			
			if(content.length!=5){
				continue;
			}
			if(!patientList.contains(id)){
				continue;
			}
			
			SimpleDateFormat dateFormat_1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSSSSS a");
			Date convertedCurrentDate = dateFormat_1.parse(content[1]);
			SimpleDateFormat dateFormat_2 = new SimpleDateFormat("MM/dd/yyyy");
			String formatted_date=dateFormat_2.format(convertedCurrentDate );

			String icd=content[2];
			String billing_encountger=content[4];
			String code=content[2].toString().trim();
			if(removedencounter.containsKey(id)){
				if(removedencounter.get(id).contains(billing_encountger)){
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

class Tempbean{
	String code;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	String date;
}


