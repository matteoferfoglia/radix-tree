package radixtree;

import java.util.*;
import java.util.stream.Collectors;

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


        // Search by prefix

        String prefixToFind = "abc";

        start = System.nanoTime();
        Hashtable<String, String> hashtablePrefix = new Hashtable<>();
        String data;
        for(String aKey : hashTable.keySet()) {
            if((data = hashTable.get(aKey)).startsWith(prefixToFind)) {
                hashtablePrefix.put(aKey, data);
            }
        }
        stop = System.nanoTime();
        System.out.println("SEARCH in HashTable by prefix: " + (stop-start)/1000.0 + " us, " +
                hashtablePrefix.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList()) +   // needs sorting, too
                "\n\t" + hashtablePrefix.size() + " elements");

        start = System.nanoTime();
        RadixTree<String> radixTreePrefix = radixTree.getByPrefix(prefixToFind);
        stop = System.nanoTime();
        System.out.println("SEARCH in RadixTree by prefix: " + (stop-start)/1000.0 + " us, " + radixTreePrefix.allEntries() +
                "\n\t" + radixTreePrefix.allEntries().size() + " elements");

    }
    
}


/* OUTPUT:

    abba_
    abc_
    abe_
    ab_
    null
    null
    X9eqJ
    X9iYE
    [X9eqJ, X9iYE, a, ab, abba, abc, abd, abe, abla, b, c]
    {X9eqJ=X9eqJ_, X9iYE=X9iYE_, a=a_, ab=ab_, abba=abba_, abc=abc_, abd=abd_, abe=abe_, abla=abla_, b=b_, c=c2}
    INSERTION in HashTable: 518295.7 us, 2000000 elements, 2000000 distinct values
    INSERTION in RadixTree: 5227935.8 us, 2000000 elements, 2000000 distinct values
    SEARCH in HashTable: 150490.7 us, 2000000 elements with duplicates, 2000000 distinct values
    SEARCH in RadixTree: 4778426.8 us, 2000000 elements with duplicates, 2000000 distinct values
    SEARCH in HashTable by prefix: 336540.9 us, [abc5892wOg=abc5892wOg_, abc6R7Jatf=abc6R7Jatf_, abcL3tcG66=abcL3tcG66_, abcOAttnrD=abcOAttnrD_, abcOQM2WdG=abcOQM2WdG_, abccU5MmFe=abccU5MmFe_, abcgN5wFvg=abcgN5wFvg_, abchAaKAAF=abchAaKAAF_, abclGGxkKB=abclGGxkKB_]
        9 elements
    SEARCH in RadixTree by prefix: 16.9 us, {c5892wOg=abc5892wOg_, c6R7Jatf=abc6R7Jatf_, cL3tcG66=abcL3tcG66_, cOAttnrD=abcOAttnrD_, cOQM2WdG=abcOQM2WdG_, ccU5MmFe=abccU5MmFe_, cgN5wFvg=abcgN5wFvg_, chAaKAAF=abchAaKAAF_, clGGxkKB=abclGGxkKB_}
        9 elements

    Process finished with exit code 0


 */