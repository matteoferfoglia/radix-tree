package radixtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

/**
 * Main class.
 * 12th Aug 2021
 * @author Matteo Ferfoglia
 */
public class Main {
    
    public static void main(String[] args) {
        RadixTree<String> t = new RadixTree<>();


        t.insert("X9iYE", "X9iYE_");
        t.insert("X9eqJ", "X9eqJ_");

        t.insert("c", "c_");
        t.insert("c", "c2");
        t.insert("a", "a_");
        t.insert("b", "b_");
        t.insert("abba", "abba_");
        t.insert("abla", "abla_");
        t.insert("ab", "ab_");
        t.insert("abc", "abc_");
        t.insert("abd", "abd_");
        t.insert("abe","abe_");

        System.out.println(t.get("abba"));    // abba_
        System.out.println(t.get("abc"));     // abc_
        System.out.println(t.get("abe"));     // abe_
        System.out.println(t.get("ab"));      // ab_
        System.out.println(t.get("abbaaa"));  // null
        System.out.println(t.get("0"));       // null
        System.out.println("X9eqJ");          // X9eqJ
        System.out.println("X9iYE");          // X9iYE

        System.out.println(Arrays.toString(t.allKeys()));
        System.out.println(t.allEntries());


        // Comparison with HashTable
        Hashtable<String, String> hashTable = new Hashtable<>();
        RadixTree<String> radixTree = new RadixTree<>();
        long start, stop;

        // Generates keys and values (duplicates might be present)
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        int N = 2000000;  // number of string to generate
        int length = 10;
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        rnd.setSeed(0);
        for(int j=0; j<N; j++) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(AB.charAt(rnd.nextInt(AB.length())));
            }
            String str = sb.toString();
            keys.add(str);
            values.add(str+"_");
        }

        // insertion
        start = System.nanoTime();
        for(int i=0; i<keys.size(); i++) {
            hashTable.put(keys.get(i), values.get(i));
        }
        stop = System.nanoTime();
        System.out.println("INSERTION in HashTable: " + (stop-start)/1000.0 + " us, " + keys.size() + " elements, " + hashTable.size() + " distinct values");
        start = System.nanoTime();
        for(int i=0; i<keys.size(); i++) {
            radixTree.insert(keys.get(i), values.get(i));
        }
        stop = System.nanoTime();
        System.out.println("INSERTION in RadixTree: " + (stop-start)/1000.0 + " us, " + keys.size() + " elements, " + hashTable.size() + " distinct values");


        // System.out.println(radixTree.allEntries());

        // search
        String s;
        start = System.nanoTime();
        for(int i=0; i<keys.size(); i++) {
            s = hashTable.get(keys.get(i));
            if(s==null || !s.equals(values.get(i))) {
                throw new RuntimeException("Error! Wrong value found");
            }
        }
        stop = System.nanoTime();
        System.out.println("SEARCH in HashTable: " + (stop-start)/1000.0 + " us, " + keys.size() + " elements with duplicates, " + hashTable.size() + " distinct values");

        start = System.nanoTime();
        for(int i=0; i<keys.size(); i++) {
            s = radixTree.get(keys.get(i));
            if(s==null || !s.equals(values.get(i))) {
                throw new RuntimeException("Error! Wrong value found: \"" + values.get(i) + "\" expected, \"" + s + "\" found");
            }
        }
        stop = System.nanoTime();
        System.out.println("SEARCH in RadixTree: " + (stop-start)/1000.0 + " us, " + keys.size() + " elements with duplicates, " + hashTable.size() + " distinct values");

    }
    
}
