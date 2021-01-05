package data.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class Parser_mapping {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

	}

	public static HashMap<String, HashSet<String>> readRxnormToDrugBank(String file) throws IOException {
		HashMap<String, HashSet<String>> mapping = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		int i = 0;
		while ((line = br.readLine()) != null) {
			i++;
			if (i == 1) {
				continue;
			}
			String[] elements = line.split("\t");
			String rxnorm = elements[0];
			rxnorm = rxnorm.substring(rxnorm.lastIndexOf("/") + 1, rxnorm.lastIndexOf("\""));
			String drugbank = elements[2];
			drugbank = drugbank.substring(drugbank.lastIndexOf(":") + 1, drugbank.lastIndexOf("\""));
			drugbank="<http://bio2rdf.org/drugbank:"+drugbank+">";
			if (mapping.containsKey(rxnorm)) {
				mapping.get(rxnorm).add(drugbank);
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(drugbank);
				mapping.put(rxnorm, set);
			}

		}
		return mapping;
	}
	
	
	
	/**
	 * GOA -> uniprot -> drugbank
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, HashSet<String>> readUniprotToDrugBank(String goa_file, String drugbank_file) throws IOException {
		
		HashMap<String,HashSet<String>> label_uniprot=new HashMap<>();
		HashMap<String,HashSet<String>> uniprot_drugbank=new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(goa_file)));
		String line=null;
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].getLabel().toString().toLowerCase().trim();
					if(o.contains("_human")){
						o=o.replace("_human", "");
					}
					
					if(p.equals("<http://bio2rdf.org/goa_vocabulary:symbol>")||p.equals("<http://bio2rdf.org/goa_vocabulary:synonym>")){
						if(label_uniprot.containsKey(o)){
							label_uniprot.get(o).add(s);
						}else{
							HashSet<String> set =new HashSet<>();
							set.add(s);
							label_uniprot.put(o, set);
						}
					}
					
				}
				
		}	
		
		br.close();

		br = new BufferedReader(new FileReader(new File(drugbank_file)));
		line=null;
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					
					if(p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")||s.startsWith("<http://bio2rdf.org/drugbank:BE")){
						if(o.startsWith("<http://bio2rdf.org/uniprot:")){
							if(uniprot_drugbank.containsKey(o)){
								uniprot_drugbank.get(o).add(s);
							}else{
								HashSet<String> set =new HashSet<>();
								set.add(s);
								uniprot_drugbank.put(o, set);
							}	
						}
						
					}
			}
		}	
		
		HashMap<String,HashSet<String>> mapping=new HashMap<>();
		
		for(Entry<String,HashSet<String>> entry:label_uniprot.entrySet()){
			for(String string_1:entry.getValue()){
				if(uniprot_drugbank.containsKey(string_1)){
					for(String string_2:uniprot_drugbank.get(string_1)){
						if(mapping.containsKey(entry.getKey())){
							mapping.get(entry.getKey()).add(string_2);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(string_2);
							mapping.put(entry.getKey(), set);
						}
					}
				}
			}
		}
		
//		
//		for(Entry<String,HashSet<String>> entry:mapping.entrySet()){
//			System.out.println(entry);
//		}
		
		System.out.println("gene to drugbank: "+mapping.size());
		return mapping;
		
		
		
	}
	
	
	
	
public static HashMap<String,HashSet<String>> readIcd9ToOmim(String file) throws IOException{
		
		HashMap<String,HashSet<String>> results=new HashMap<>();
		
		HashMap<String,HashSet<String>> omimToDisease=new HashMap<>();
		HashMap<String,HashSet<String>> diseaseToIcd9=new HashMap<>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashMap<String,HashSet<String>> toSider=new HashMap<>();
		HashSet<String> disease=new HashSet<>();
		while((line=br.readLine())!=null){
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].getLabel().toString().trim();
					
					if(p.equals("<http://www.geneontology.org/formats/oboInOwl#hasDbXref>")&&o.contains("OMIM:")){
						String o_value=o.substring(o.indexOf(":")+1,o.length());
						String s_value=s;
							
								if(omimToDisease.containsKey(o_value)){
									omimToDisease.get(o_value).add(s_value);
								}else{
									HashSet<String> set =new HashSet<>();
									set.add(s_value);
									omimToDisease.put(o_value, set);
								}
					}
					
					if(p.equals("<http://www.geneontology.org/formats/oboInOwl#hasDbXref>")&&o.contains("ICD9CM")){
						String o_value=o.substring(o.indexOf(":")+1,o.length());
						String s_value=s;
							
						if(diseaseToIcd9.containsKey(s_value)){
							diseaseToIcd9.get(s_value).add(o_value);
						}else{
							HashSet<String> set =new HashSet<>();
							set.add(o_value);
							diseaseToIcd9.put(s_value, set);
						}
					}	
					
				}
				
			}	
		
//		System.out.println(omimToDisease);
//		
//		System.err.println(diseaseToIcd9);
		
		for(Entry<String,HashSet<String>> entry:omimToDisease.entrySet()){
			for(String string_1:entry.getValue()){
				if(diseaseToIcd9.containsKey(string_1)){
					
					for(String string_2:diseaseToIcd9.get(string_1)){
						String key=entry.getKey();
						if(key.contains("PS")){
							key=key.replaceAll("PS", "");
						}
						key="<http://bio2rdf.org/omim:"+key+">";
						
						
						if(results.containsKey(string_2)){
							results.get(string_2).add(key);
						}else{
							HashSet<String> set=new HashSet<>();
							set.add(key);
							results.put(string_2, set);
						}
					}
				}
			}
		}
		
		
//		for(Entry<String,HashSet<String>> entry:results.entrySet()){
//			System.out.println(entry.getKey()+" -> "+entry.getValue());
//		}
		
		System.out.println("omim mappings: "+results.size()); 
		return results;
	}

	
}
