package data.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import data.DateCalculator;
import data.LabtestBean;

public class Parser_labtest {

	public static void main(String[] args) throws IOException {
	}
	
	
	
	public static void readLabtestRange(String file, Set<BigInteger> patient_list, String outfile)throws IOException{
		HashMap<String,String> name_map=new HashMap<>();
		BufferedReader br =new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<String,HashSet<String>> map_code_range=new HashMap();
		HashMap<String,HashSet<String>> map_code_value=new HashMap();
		while((line=br.readLine())!=null){
			String[] content=line.split("\t");
			
			if(content.length!=12){
				continue;
			}
			BigInteger id=new BigInteger(content[0]);
			if(!patient_list.contains(id)){
				continue;
			}
			
			String range=content[9].trim();
			String code=content[5].trim();
			String name=content[6].trim();
			String value=content[8].trim();
			
			if(code.length()<3){
				continue;
			}
			
			name_map.put(code, name);
			
			if(range.length()>0){
				
				if(map_code_range.containsKey(code)){
					map_code_range.get(code).add(range);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(range);
					map_code_range.put(code,set);
				}
			}else{
				
				if(map_code_value.containsKey(code)){
					map_code_value.get(code).add(value);
				}else{
					HashSet<String> set=new HashSet<>();
					set.add(value);
					map_code_value.put(code,set);
				}
				
			}
		} 
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(outfile));
		CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("code", "name","range", "value"));
    
        
		for(Entry<String,HashSet<String>> entry:map_code_range.entrySet()){
			if(map_code_value.containsKey(entry.getKey())){
				map_code_value.remove(entry.getKey());
			}
		}
		
		System.out.println(map_code_range.size());
		System.out.println(map_code_value.size());
		for(Entry<String,HashSet<String>> entry:map_code_range.entrySet()){
			csvPrinter.printRecord(entry.getKey(), name_map.get(entry.getKey()),entry.getValue(), "");
		}
		System.out.println("*********************************************");
		System.out.println();
		System.out.println("*********************************************");
		
		for(Entry<String,HashSet<String>> entry:map_code_value.entrySet()){
			System.out.println(entry.getKey()+"\t"+entry.getValue());
			csvPrinter.printRecord(entry.getKey(),name_map.get(entry.getKey()), "" ,entry.getValue());
		}
		csvPrinter.flush();
		csvPrinter.close();
	}
	

	
	public static HashMap<BigInteger, HashMap<String, LabtestBean>> readLabtestTab(String file,
			Set<BigInteger> PatientList,HashMap<BigInteger, HashSet<String>> removed_encounters) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		HashMap<BigInteger, HashMap<String, LabtestBean>> map = new HashMap<>();

		while ((line = br.readLine()) != null) {
			String[] content = line.split("\t");
			
			
			BigInteger id = new BigInteger(content[0].toString().trim());
			String encounter = content[3].toString().trim();

			if(!PatientList.contains(id)){
				continue;
			}
			if (content.length != 12) {
				continue;
			}
			
			if (removed_encounters.containsKey(id)) {
				if (removed_encounters.get(id).contains(encounter)) {
					continue;
				}
			}

			String date_tmp = content[4].toString().trim();
			String pattern = "MM/dd/yyyy";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date date_tmp_1 = simpleDateFormat.parse(date_tmp);
			String date = simpleDateFormat.format(date_tmp_1);

			String range = content[9].toString().trim().toLowerCase();
			String code = content[5].toString().trim();
			String value = content[8].toString().trim().toLowerCase();

			if (range.contains("<") && range.contains(">")) {
				range = range.replaceAll("->", "");
				range = range.replaceAll(">=", "");
				range = range.replaceAll("> or=", "");
				range = range.replaceAll(">or=", "");
				range = range.replaceAll(">/=", "");
				range = range.replaceAll(">", "");
				range = range.replaceAll("-", "");
				range = range.replaceAll("=", "");
				range = range.replaceAll("\\s", "");
				range = range.trim();

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");

				HashMap<Double, String> sorting = new HashMap<>();

				if (range.equals("unvaccinated:<10.0;vaccinated:10.0")) {
					sorting.put(10.0, "unvaccinated");
					sorting.put(Double.MAX_VALUE, "vaccinated");
				}
				if (range.equals("acceptable:<90;borderlinehigh:90109;high:")) {
					sorting.put(90.0, "acceptable");
					sorting.put(109.0, "borderlinehigh");
					sorting.put(Double.MAX_VALUE, "high");
				}
				if (range.equals("unvaccinated:<5.0;vaccinated:12.0;")) {
					sorting.put(5.0, "unvaccinated");
					sorting.put(12.0, "undetermined");
					sorting.put(Double.MAX_VALUE, "vaccinated");
				}
				if (range.equals("<5.0negative;5.025.0indeterminate;25.0posit")) {
					sorting.put(5.0, "negative");
					sorting.put(25.0, "indeterminate");
					sorting.put(Double.MAX_VALUE, "posit");
				}
				if (range.equals("unvaccinated:<5.0;vaccinated:12.0")) {
					sorting.put(5.0, "unvaccinated");
					sorting.put(12.0, "undetermined");
					sorting.put(Double.MAX_VALUE, "vaccinated");
				}
				if (range.equals("<100;referencerangesvalidfor;8hourfast.")) {
					sorting.put(100.0, "normal");
					sorting.put(Double.MAX_VALUE, "abnormal");
				}

				Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
				boolean noHit = true;
				for (Entry<Double, String> entry : treeMap.entrySet()) {
					if (Double.valueOf(value) <= entry.getKey()) {
						value = entry.getValue();
						noHit = false;
						break;
					}
				}

				if (noHit) {
					value = "abnormal";
				}

			} else if (range.contains("<")) {

				range = range.replaceAll("-<", "");
				range = range.replaceAll("<=", "");
				range = range.replaceAll("<", "");
				range = range.replaceAll("-", "");
				range = range.replaceAll("=", "");
				range = range.replaceAll("\\s", "");
				range = range.trim();

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");
				if (value.contains("-")) {
					value = value.substring(value.indexOf("-") + 1, value.length()).trim();
				}
				if (onlyDigit(range)) {
					if (onlyDigit(value)) {

						if (Double.valueOf(value) < Double.valueOf(range)) {
							value = "normal";
						} else {
							value = "abnormal";
						}

					} else {
						value = "null";
					}

				} else {
					if (value.contains("neg")) {
						value = "negative";
					} else if (value.contains(":")) {
						range = range.replace("(negative)", "");
						range = range.substring(range.indexOf(":") + 1, range.length());
						value = value.substring(value.indexOf(":") + 1, value.length());

						if (Double.valueOf(value) < Double.valueOf(range)) {
							value = "negative";
						} else {
							value = "positive";
						}
					} else if (range.contains("(")) {
						if (!range.contains(")")) {
							range = range + ")";
						}
						if (value.equals(".") || range.equals("142x10(3)")) {
							continue;
						}

						HashMap<Double, String> sorting = new HashMap<>();
						String[] ranges = range.split(";");

						for (String string : ranges) {
							if (!string.contains(")")) {
								string = string + ")";
							}
							sorting.put(Double.valueOf(string.substring(0, string.lastIndexOf("("))),
									string.substring(string.indexOf("(") + 1, string.lastIndexOf(")")));
						}

						Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
						boolean noHit = true;
						for (Entry<Double, String> entry : treeMap.entrySet()) {
							if (Double.valueOf(value) <= entry.getKey()) {
								value = entry.getValue();
								noHit = false;
								break;
							}
						}

						if (noHit) {
							value = "abnormal";
						}

					} else {
						if (range.contains(":")) {
							if (value.length() == 0) {
								continue;
							}
							HashMap<Double, String> sorting = new HashMap<>();
							String[] ranges = range.split(";");

							for (String string : ranges) {
								String[] elements = string.split(":");
								if (!onlyDigit(elements[1])) {
									elements[1] = keepDigit(elements[1]);
								}
								try {
									sorting.put(Double.valueOf(elements[1]), elements[0]);
								} catch (Exception e) {
									continue;
								}
							}
							Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
							boolean noHit = true;
							for (Entry<Double, String> entry : treeMap.entrySet()) {

								if (Double.valueOf(value) <= entry.getKey()) {
									value = entry.getValue();
									noHit = false;
									break;
								}
							}

							if (noHit) {
								value = "abnormal";
							}

						} else if (range.contains(";")) {
							HashMap<Double, String> sorting = new HashMap<>();

							if (range.matches("[0-9]*\\.?[0-9]*;")) {
								range = keepDigit(range);
								sorting.put(Double.valueOf(range), "normal");
							} else if (range.matches("[[a-zA-Z]+[0-9]+\\.?[0-9]*;]?")
									|| range.matches("[[0-9]+\\.?[0-9]*[a-zA-Z]+;]?")) {
								System.out.println(range);
								for (String string : range.split(";")) {
									sorting.put(Double.valueOf(keepDigit(string)), keepAlp(string));
								}
							} else if (range.equals("33;athyroticindividuals;normallyhavehtgval")) {
								sorting.put(Double.valueOf(33), "athyroticindividuals");
							} else {
								continue;
							}
							Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
							boolean noHit = true;
							for (Entry<Double, String> entry : treeMap.entrySet()) {

								if (Double.valueOf(value) <= entry.getKey()) {
									value = entry.getValue();
									noHit = false;
									break;
								}
							}

							if (noHit) {
								value = "abnormal";
							}

						} else if (containsDigit(range)) {
							range = keepDigit(range);

							try {
								Double.valueOf(value);
							} catch (Exception e) {
								continue;
							}
							if (Double.valueOf(value) < Double.valueOf(range)) {
								value = "normal";
							} else {
								value = "abnormal";
							}
						} else {
							continue;
						}

					}

				}

			} else if (range.contains(">")) {
				range = range.replaceAll("->", "");
				range = range.replaceAll(">=", "");
				range = range.replaceAll("> or=", "");
				range = range.replaceAll(">or=", "");
				range = range.replaceAll(">/=", "");
				range = range.replaceAll(">", "");
				range = range.replaceAll("-", "");
				range = range.replaceAll("=", "");
				range = range.replaceAll("\\s", "");
				range = range.trim();

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");

				if (range.contains(";")) {
					range = range.substring(0, range.indexOf(";"));
				}

				try {
					Double.valueOf(range);
				} catch (Exception e) {
					continue;
				}
				try {
					Double.valueOf(value);
				} catch (Exception e) {
					continue;
				}
				if (Double.valueOf(value) >= Double.valueOf(range)) {
					value = "normal";
				} else {
					value = "abnormal";
				}

			} else if (range.contains("-")) {

				range = range.replaceAll("\\s", "");
				range = range.trim();

				if (range.contains("to+")) {
					range = range.replace("to+", "-");
				}
				if (range.contains("negative-")) {
					range = range.replace("negative", String.valueOf(Integer.MIN_VALUE));
				}
				if (range.contains("na-")) {
					range = range.replace("na", String.valueOf(Integer.MIN_VALUE));
				}
				if (range.equals("+or-2")) {
					continue;
				}
				if (range.equals("neg-trace")) {
					continue;
				}
				if (range.equals("-")) {
					continue;
				}
				if (range.matches("-*[0-9]+\\.?[0-9]*-")) {
					continue;
				}
				if (range.matches("-+[0-9]+\\.?[0-9]*")) {
					continue;
				}

				String[] e = range.split("-");
				String lower = null;
				String upper = null;
				if (e.length > 2) {

					if (range.matches("-[0-9]+\\.?[0-9]*-[0-9]+\\.?[0-9]*")) {
						lower = keepDigit("-" + e[1]);
						upper = keepDigit(e[2]);
					} else if (range.matches("-[0-9]+\\.?[0-9]*--[0-9]+\\.?[0-9]*")) {
						lower = keepDigit("-" + e[1]);
						upper = keepDigit("-" + e[3]);
					} else {
						continue;
					}

				} else {
					lower = keepDigit(e[0]);
					upper = keepDigit(e[1]);
				}

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");


				if (value.contains("neg")) {
					value = "normal";
				} else if (onlyDigit(value)) {

					if ((Double.valueOf(value) < Double.valueOf(lower))
							|| (Double.valueOf(value) > Double.valueOf(upper))) {
						value = "abnormal";
					} else {
						value = "normal";
					}

				} else {
					continue;
				}

			} else {

				Pattern regex = Pattern.compile("-*[0-9]+\\.?[0-9]*");
				Matcher matcher = regex.matcher(range);

				if (matcher.find()) {
					range = keepDigit(range);

					value = value.replaceAll("<", "");
					value = value.replaceAll("=", "");
					value = value.replaceAll(">", "");

					if (value.contains("neg")) {
						value = "normal";
					} else if (onlyDigit(value)) {

						if ((Double.valueOf(value) <= Double.valueOf(range))) {
							value = "normal";
						} else {
							value = "abnormal";
						}

					} else {
						continue;
					}

				} else {
					continue;
				}
			}

			if (map.containsKey(id)) {
				if (map.get(id).containsKey(code)) {

					if (DateCalculator.differenceWithCurrent(date, map.get(id).get(code).getDate()) > 0) {
						LabtestBean bean = new LabtestBean();
						bean.setCode(code);
						bean.setValue(value);
						bean.setDate(date);
						map.get(id).remove(code);
						map.get(id).put(code, bean);
					}

				} else {
					LabtestBean bean = new LabtestBean();
					bean.setCode(code);
					bean.setValue(value);
					bean.setDate(date);
					map.get(id).put(code, bean);
				}
			} else {
				HashMap<String, LabtestBean> local_map = new HashMap<>();
				LabtestBean bean = new LabtestBean();
				bean.setCode(code);
				bean.setValue(value);
				bean.setDate(date);
				local_map.put(code, bean);
				map.put(id, local_map);
			}
		
		}
	
		return map;

	}
	
	
	public static HashMap<BigInteger, HashMap<String, LabtestBean>> readLabtestTab_time(String file,
			Set<BigInteger> PatientList, int numberOfMonth, 
			HashMap<BigInteger,String> diagnoizedDate) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		HashMap<BigInteger, HashMap<String, LabtestBean>> map = new HashMap<>();

		int numberOfDays = numberOfMonth*30;
		while ((line = br.readLine()) != null) {
			String[] content = line.split("\t");
			
			
			BigInteger id = new BigInteger(content[0].toString().trim());
			String encounter = content[3].toString().trim();

			if(!PatientList.contains(id)){
				continue;
			}
			if (content.length != 12) {
				continue;
			}

			String date_tmp = content[4].toString().trim();
			String pattern = "MM/dd/yyyy";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date date_tmp_1 = simpleDateFormat.parse(date_tmp);
			String date = simpleDateFormat.format(date_tmp_1);

			String range = content[9].toString().trim().toLowerCase();
			String code = content[5].toString().trim();
			String value = content[8].toString().trim().toLowerCase();

			if (range.contains("<") && range.contains(">")) {
				range = range.replaceAll("->", "");
				range = range.replaceAll(">=", "");
				range = range.replaceAll("> or=", "");
				range = range.replaceAll(">or=", "");
				range = range.replaceAll(">/=", "");
				range = range.replaceAll(">", "");
				range = range.replaceAll("-", "");
				range = range.replaceAll("=", "");
				range = range.replaceAll("\\s", "");
				range = range.trim();

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");

				HashMap<Double, String> sorting = new HashMap<>();

				if (range.equals("unvaccinated:<10.0;vaccinated:10.0")) {
					sorting.put(10.0, "unvaccinated");
					sorting.put(Double.MAX_VALUE, "vaccinated");
				}
				if (range.equals("acceptable:<90;borderlinehigh:90109;high:")) {
					sorting.put(90.0, "acceptable");
					sorting.put(109.0, "borderlinehigh");
					sorting.put(Double.MAX_VALUE, "high");
				}
				if (range.equals("unvaccinated:<5.0;vaccinated:12.0;")) {
					sorting.put(5.0, "unvaccinated");
					sorting.put(12.0, "undetermined");
					sorting.put(Double.MAX_VALUE, "vaccinated");
				}
				if (range.equals("<5.0negative;5.025.0indeterminate;25.0posit")) {
					sorting.put(5.0, "negative");
					sorting.put(25.0, "indeterminate");
					sorting.put(Double.MAX_VALUE, "posit");
				}
				if (range.equals("unvaccinated:<5.0;vaccinated:12.0")) {
					sorting.put(5.0, "unvaccinated");
					sorting.put(12.0, "undetermined");
					sorting.put(Double.MAX_VALUE, "vaccinated");
				}
				if (range.equals("<100;referencerangesvalidfor;8hourfast.")) {
					sorting.put(100.0, "normal");
					sorting.put(Double.MAX_VALUE, "abnormal");
				}

				Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
				boolean noHit = true;
				for (Entry<Double, String> entry : treeMap.entrySet()) {
					if (Double.valueOf(value) <= entry.getKey()) {
						value = entry.getValue();
						noHit = false;
						break;
					}
				}

				if (noHit) {
					value = "abnormal";
				}

			} else if (range.contains("<")) {

				range = range.replaceAll("-<", "");
				range = range.replaceAll("<=", "");
				range = range.replaceAll("<", "");
				range = range.replaceAll("-", "");
				range = range.replaceAll("=", "");
				range = range.replaceAll("\\s", "");
				range = range.trim();

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");
				if (value.contains("-")) {
					value = value.substring(value.indexOf("-") + 1, value.length()).trim();
				}
				if (onlyDigit(range)) {
					if (onlyDigit(value)) {

						if (Double.valueOf(value) < Double.valueOf(range)) {
							value = "normal";
						} else {
							value = "abnormal";
						}

					} else {
						value = "null";
					}

				} else {
					if (value.contains("neg")) {
						value = "negative";
					} else if (value.contains(":")) {
						range = range.replace("(negative)", "");
						range = range.substring(range.indexOf(":") + 1, range.length());
						value = value.substring(value.indexOf(":") + 1, value.length());

						if (Double.valueOf(value) < Double.valueOf(range)) {
							value = "negative";
						} else {
							value = "positive";
						}
					} else if (range.contains("(")) {
						if (!range.contains(")")) {
							range = range + ")";
						}
						if (value.equals(".") || range.equals("142x10(3)")) {
							continue;
						}

						HashMap<Double, String> sorting = new HashMap<>();
						String[] ranges = range.split(";");

						for (String string : ranges) {
							if (!string.contains(")")) {
								string = string + ")";
							}
							sorting.put(Double.valueOf(string.substring(0, string.lastIndexOf("("))),
									string.substring(string.indexOf("(") + 1, string.lastIndexOf(")")));
						}

						Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
						boolean noHit = true;
						for (Entry<Double, String> entry : treeMap.entrySet()) {
							if (Double.valueOf(value) <= entry.getKey()) {
								value = entry.getValue();
								noHit = false;
								break;
							}
						}

						if (noHit) {
							value = "abnormal";
						}

					} else {
						if (range.contains(":")) {
							if (value.length() == 0) {
								continue;
							}
							HashMap<Double, String> sorting = new HashMap<>();
							String[] ranges = range.split(";");

							// System.out.println(range+" -> "+value);
							for (String string : ranges) {
								String[] elements = string.split(":");
								if (!onlyDigit(elements[1])) {
									elements[1] = keepDigit(elements[1]);
								}
								try {
									sorting.put(Double.valueOf(elements[1]), elements[0]);
								} catch (Exception e) {
									continue;
								}
							}
							Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
							boolean noHit = true;
							for (Entry<Double, String> entry : treeMap.entrySet()) {

								if (Double.valueOf(value) <= entry.getKey()) {
									value = entry.getValue();
									noHit = false;
									break;
								}
							}

							if (noHit) {
								value = "abnormal";
							}

						} else if (range.contains(";")) {
							HashMap<Double, String> sorting = new HashMap<>();

							if (range.matches("[0-9]*\\.?[0-9]*;")) {
								range = keepDigit(range);
								sorting.put(Double.valueOf(range), "normal");
							} else if (range.matches("[[a-zA-Z]+[0-9]+\\.?[0-9]*;]?")
									|| range.matches("[[0-9]+\\.?[0-9]*[a-zA-Z]+;]?")) {
								System.out.println(range);
								for (String string : range.split(";")) {
									sorting.put(Double.valueOf(keepDigit(string)), keepAlp(string));
								}
							} else if (range.equals("33;athyroticindividuals;normallyhavehtgval")) {
								sorting.put(Double.valueOf(33), "athyroticindividuals");
							} else {
								continue;
								// extending for the future.
							}
							Map<Double, String> treeMap = new TreeMap<Double, String>(sorting);
							boolean noHit = true;
							for (Entry<Double, String> entry : treeMap.entrySet()) {

								if (Double.valueOf(value) <= entry.getKey()) {
									value = entry.getValue();
									noHit = false;
									break;
								}
							}

							if (noHit) {
								value = "abnormal";
							}

						} else if (containsDigit(range)) {
							range = keepDigit(range);

							try {
								Double.valueOf(value);
							} catch (Exception e) {
								continue;
							}
							if (Double.valueOf(value) < Double.valueOf(range)) {
								value = "normal";
							} else {
								value = "abnormal";
							}
						} else {
							// extending for the future.
						}

					}

				}

			} else if (range.contains(">")) {
				range = range.replaceAll("->", "");
				range = range.replaceAll(">=", "");
				range = range.replaceAll("> or=", "");
				range = range.replaceAll(">or=", "");
				range = range.replaceAll(">/=", "");
				range = range.replaceAll(">", "");
				range = range.replaceAll("-", "");
				range = range.replaceAll("=", "");
				range = range.replaceAll("\\s", "");
				range = range.trim();

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");

				if (range.contains(";")) {
					range = range.substring(0, range.indexOf(";"));
				}

				try {
					Double.valueOf(range);
				} catch (Exception e) {
					continue;
				}
				try {
					Double.valueOf(value);
				} catch (Exception e) {
					continue;
				}
				if (Double.valueOf(value) >= Double.valueOf(range)) {
					value = "normal";
				} else {
					value = "abnormal";
				}

			} else if (range.contains("-")) {

				range = range.replaceAll("\\s", "");
				range = range.trim();

				if (range.contains("to+")) {
					range = range.replace("to+", "-");
				}
				if (range.contains("negative-")) {
					range = range.replace("negative", String.valueOf(Integer.MIN_VALUE));
				}
				if (range.contains("na-")) {
					range = range.replace("na", String.valueOf(Integer.MIN_VALUE));
				}
				if (range.equals("+or-2")) {
					continue;
				}
				if (range.equals("neg-trace")) {
					continue;
				}
				if (range.equals("-")) {
					continue;
				}
				if (range.matches("-*[0-9]+\\.?[0-9]*-")) {
					continue;
				}
				if (range.matches("-+[0-9]+\\.?[0-9]*")) {
					continue;
				}
				// System.out.println("line ->"+range+"-> "+value);

				String[] e = range.split("-");
				String lower = null;
				String upper = null;
				if (e.length > 2) {

					if (range.matches("-[0-9]+\\.?[0-9]*-[0-9]+\\.?[0-9]*")) {
						lower = keepDigit("-" + e[1]);
						upper = keepDigit(e[2]);
					} else if (range.matches("-[0-9]+\\.?[0-9]*--[0-9]+\\.?[0-9]*")) {
						lower = keepDigit("-" + e[1]);
						upper = keepDigit("-" + e[3]);
					} else {
						continue;
					}

				} else {
					lower = keepDigit(e[0]);
					upper = keepDigit(e[1]);
				}

				value = value.replaceAll("<", "");
				value = value.replaceAll("=", "");
				value = value.replaceAll(">", "");

				// System.out.println("line ->"+range+"-> "+value+"
				// lower:"+lower+" upper:"+upper);

				if (value.contains("neg")) {
					value = "normal";
				} else if (onlyDigit(value)) {

					if ((Double.valueOf(value) < Double.valueOf(lower))
							|| (Double.valueOf(value) > Double.valueOf(upper))) {
						value = "abnormal";
					} else {
						value = "normal";
					}

				} else {
					continue;
				}

			} else {

				Pattern regex = Pattern.compile("-*[0-9]+\\.?[0-9]*");
				Matcher matcher = regex.matcher(range);

				if (matcher.find()) {
					range = keepDigit(range);

					value = value.replaceAll("<", "");
					value = value.replaceAll("=", "");
					value = value.replaceAll(">", "");

					if (value.contains("neg")) {
						value = "normal";
					} else if (onlyDigit(value)) {

						if ((Double.valueOf(value) <= Double.valueOf(range))) {
							value = "normal";
						} else {
							value = "abnormal";
						}

					} else {
						continue;
					}

				} else {
					continue;
					// not do
				}
			}
			
			String base_data=diagnoizedDate.get(id);
			
			if (DateCalculator.differenceDate(base_data, date) > numberOfDays){
				if (map.containsKey(id)) {
					if (map.get(id).containsKey(code)) {
						if (DateCalculator.differenceDate(date, map.get(id).get(code).getDate()) > 0) {
							LabtestBean bean = new LabtestBean();
							bean.setCode(code);
							bean.setValue(value);
							bean.setDate(date);
							map.get(id).remove(code);
							map.get(id).put(code, bean);
						}

					} else {
						LabtestBean bean = new LabtestBean();
						bean.setCode(code);
						bean.setValue(value);
						bean.setDate(date);
						map.get(id).put(code, bean);
					}
				} else {
					HashMap<String, LabtestBean> local_map = new HashMap<>();
					LabtestBean bean = new LabtestBean();
					bean.setCode(code);
					bean.setValue(value);
					bean.setDate(date);
					local_map.put(code, bean);
					map.put(id, local_map);
				}
			}
			
		}

		
		return map;

	}

	
	public static void LabtestTab_checkSize(String file, Set<BigInteger> patientList) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		HashMap<BigInteger, HashSet<String>> map = new HashMap<>();

		while ((line = br.readLine()) != null) {
			String[] content = line.split("\t");
			
			
			BigInteger id = new BigInteger(content[0].toString().trim());
			String encounter = content[3].toString().trim();

			if (content.length != 12) {
				continue;
			}
			
			if(!patientList.contains(id)){
				continue;
			}

			String range = content[9].toString().trim().toLowerCase();
			String code = content[5].toString().trim();
			String value = content[8].toString().trim().toLowerCase();
			
			if(map.containsKey(id)){
				map.get(id).add(code);
			}else{
				HashSet<String> set=new HashSet<>();
				set.add(code);
				map.put(id, set);
			}
			
		}
		System.out.println("all lab test size: "+map.size());
	}

	public final static boolean containsDigit(String s) {
		boolean containsDigit = false;
		if (s.length() > 0) {
			if (s != null && !s.isEmpty()) {
				for (char c : s.toCharArray()) {
					if (containsDigit = Character.isDigit(c)) {
						break;
					}
				}
			}
		}

		return containsDigit;
	}

	public final static boolean onlyDigit(String s) {
		boolean only = false;

		if (s.length() > 0) {

			try {
				Double.valueOf(s);
				only = true;
			} catch (Exception e) {
				// TODO: handle exception
				only = false;
			}

		}

		return only;
	}

	public final static int indexOfNumber(String s) {

		int index = 0;
		if (s != null && !s.isEmpty()) {
			char[] c = s.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (Character.isDigit(c[i])) {
					index = i;
					break;
				}
			}

		}

		return index;
	}

	public final static String keepDigit(String s) {

		Pattern regex = Pattern.compile("-?[0-9]+\\.?[0-9]*");
		Matcher matcher = regex.matcher(s);
		String digits = null;
		while (matcher.find()) {
			digits = matcher.group(0);
		}

		// String digits = string.replaceAll("[^0-9.]", "");
		return digits;
	}

	public final static String keepAlp(String s) {
		String string = new String(s);
		String digits = string.replaceAll("[0-9.]", "");
		return digits;
	}

	public final static boolean findPatern(String s, String validPattern) {
		Pattern pattern = Pattern.compile(validPattern);
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}

	public static String process(String string) {
		String processed = "";
		if (containsDigit(string)) {
			string = string.replaceAll("\\s+", "");

			if (string.contains("-")) {
				processed = string;
			} else if (string.contains(">") && string.contains("<")) {

			} else if (string.contains(">")) {

			} else if (string.contains("<")) {

			}

		}
		return processed;
	}

	public final static int indexOf(String s) {

		int index = 0;
		if (s != null && !s.isEmpty()) {
			char[] c = s.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (Character.isDigit(c[i])) {
					index = i;
					break;
				}
			}

		}

		return index;
	}
}
