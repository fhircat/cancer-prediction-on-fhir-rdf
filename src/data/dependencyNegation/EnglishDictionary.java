package data.dependencyNegation;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A concise dictionary of common terms in English.
 *
 * @author Haifeng Li
 */
public enum EnglishDictionary implements Dictionary {
    /**
     * A concise dictionary of common terms in English.
     */
//    CONCISE("/smile/nlp/dictionary/dictionary_en.txt");
	
	CONCISE("data/dependencyNegation/dictionary_en.txt");
	
    /**
     * A list of abbreviations.
     */
    private HashSet<String> dict;

    /**
     * Constructor.
     * @param resource the file name of dictionary. The file should be in plain
     * text, in which each line is a word.
     */
    EnglishDictionary(String resource) {
    	
    	System.out.println(resource);
    	
        dict = new HashSet<>();

//        try (BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(resource)))) {
        try (BufferedReader input = new BufferedReader(new FileReader(new File(resource)))) {
            String line = null;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                // Remove blank line or single capital characters from dictionary.
                if (!line.isEmpty() && !line.matches("^[A-Z]$")) {
                    dict.add(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean contains(String s) {
        return dict.contains(s);
    }

    @Override
    public int size() {
        return dict.size();
    }

    @Override
    public Iterator<String> iterator() {
        return dict.iterator();
    }
}