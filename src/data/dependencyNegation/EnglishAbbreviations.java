package data.dependencyNegation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import scala.collection.mutable.HashSet;

public class EnglishAbbreviations {
    /**
     * A list of abbreviations.
     */
    static HashSet<String> dictionary = dictionary();

    static HashSet<String> dictionary() {
    	
    	
    	HashSet<String> set=new HashSet<String>();
    	BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(
					"data/dependencyNegation/abbreviations_en.txt")));
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

    /**
     * Returns true if this abbreviation dictionary contains the specified element.
     */
    static boolean contains(String s) {
        return dictionary.contains(s);
    }

    /**
     * Returns the number of elements in this abbreviation dictionary.
     */
    static int size() {
        return dictionary.size();
    }

    /**
     * Returns an iterator over the elements in this abbreviation dictionary.
     */
    static scala.collection.Iterator<String> iterator() {
        return dictionary.iterator();
    }
}