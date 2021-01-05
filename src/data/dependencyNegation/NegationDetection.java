package data.dependencyNegation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Pattern;

public class NegationDetection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
	}
	
	public boolean getNeg(String string, HashSet<String> patterns){
		
		boolean neg=false;
		
		for(String pattern:patterns){
			if(Pattern.compile(pattern).matcher(string.toLowerCase().trim()).find()){
//				System.err.println("find "+string +" in pattern: "+pattern);
				neg=true;
				break;
			}
		}
		return neg;
	}
	public HashSet<String> getNegTrigger(){
		HashSet<String> set=new HashSet<String>();
    	BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(
					"/data/dependencyNegation/trigger-neg.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String line=null;
    	try {
			while((line=br.readLine())!=null){
				set.add(line.trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return set;
	}

}
