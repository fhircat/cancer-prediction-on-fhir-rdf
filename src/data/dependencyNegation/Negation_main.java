package data.dependencyNegation;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.collection.immutable.List;

public class Negation_main {

	public static void main(String[] args){
		String string="### DATE: 2017-01-25 ### His father die SN_56265001 SN_562633133412 . His mother SN_118604003 abdominal lymph node. "
				+ "She myRisk genetic test great SN_562650112 gene include BRCA1 NUM(2) , negative . "
				+ "### DATE: 2018-06-07 ### Per patient , no family history breast SN_363443007 .";
	
		SimpleSentenceSplitter spliter=new SimpleSentenceSplitter();
		String[] sens=spliter.split(string);
		
		NegationDetection detector=new NegationDetection();
		HashSet<String> patterns=detector.getNegTrigger();
		HashSet<String> diseases=new HashSet<>();
		for(String sen:sens){
			System.out.println(sen +" negation: "+detector.getNeg(sen, patterns));
			
			if(sen.contains(" SN_")){
				if(!detector.getNeg(sen, patterns)){
					Pattern pattern = Pattern.compile("(\\s*SN_[0-9]+\\s*)");
					Matcher matcher = pattern.matcher(sen);
					while (matcher.find())
					{
						diseases.add(matcher.group(0).trim());
					}
					
				}
			}
		}
		System.out.println(diseases);
		
	}
}
