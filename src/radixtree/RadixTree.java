package radixtree;

import java.util.Map;
import java.util.Objects;

/**
 * RadixTree.
 * 12 Aug 2021
 * 
 * @author Matteo Ferfoglia
 */
public class RadixTree<T> {

    /** The root node. */
    private final RadixTreeNode<T> root;
    
    /** Constructor. */
    public RadixTree() {
        this.root = new RadixTreeNode<>();
    }
    
    /**
     * @param stringToFind The string to find.
     * @return the data (if present) associated to specified string, null
     *          otherwise.
     */
    public T get(@NotNull String stringToFind) {
        
        RadixTreeNode<T> found = root.findPrefix(Objects.requireNonNull(stringToFind, "The given string cannot be null."));
        if( found!=null && stringToFind.endsWith(found.getKey()) ) {
            return found.getData().orElse(null);
        } else {
            return null;
        }
        
    }

    /**
     * @return the array of all keys which are currently stored (corresponding to
     *          non-null data), alphabetically ordered.
     */
    public String[] allKeys() {
        var allValues = root.exploreSubTree("");
        allValues.remove(""); // "" is the root (no info)
        return allValues.keySet().toArray(String[]::new);
    }

    /**
     * @return the {@link java.util.Map} of all keys associated to some data which are
     *          currently stored, alphabetically ordered.
     */
    public Map<String,T> allEntries() {
        var allValues = root.exploreSubTree("");
        allValues.remove(""); // "" is the root (no info)
        return allValues;
    }

    /**
     * @param stringToInsert The string to insert.
     * @param data The data associated with the string.
     * @return the previously stored data associated to the same string if 
     *          present, null otherwise.
     * @throws IllegalArgumentException If the given string is empty.
     */
    @SuppressWarnings("UnusedReturnValue")  // can be useful
    public T insert(@NotNull String stringToInsert, @NotNull T data) {
        if(Objects.requireNonNull(stringToInsert, "The given string cannot be null").length()==0) {
            throw new IllegalArgumentException("The given string cannot be null");
        }

        return root.insert( stringToInsert,
                            Objects.requireNonNull(data, "The data cannot be null") );
    }
    
}
