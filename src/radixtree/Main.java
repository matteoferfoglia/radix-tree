package radixtree;

/**
 * Main class.
 * 12th Aug 2021
 * @author Matteo Ferfoglia
 */
public class Main {
    
    public static void main(String[] args) {
        RadixTree<String> t = new RadixTree<>();
        t.insert("c", "c_");
        t.insert("c", "c_");
        t.insert("a", "a_");
        t.insert("b", "b_");
        t.insert("abba", "abba_");
        t.insert("ab", "ab_");
        t.insert("abc", "abc_");
        t.insert("abd", "abd_");
        System.out.println(t.get("abba"));    // abba_
        System.out.println(t.get("abbaaa"));  // null
    }
    
}
