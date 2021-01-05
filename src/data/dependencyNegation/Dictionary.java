package data.dependencyNegation;

import java.util.Iterator;

public interface Dictionary {

    /**
     * Returns true if this dictionary contains the specified word.
     */
    boolean contains(String word);

    /**
     * Returns the number of elements in this dictionary.
     */
    int size();

    /**
     * Returns an iterator over the elements in this dictionary.
     */
    Iterator<String> iterator();
}