package radixtree;

import java.util.Optional;

/**
 * Class representing a node of a {@link RadixTree}.
 * 12th Aug 2021.
 * 
 * @param <T> Type of data associated to a node if it corresponds to a word.
 * @author Matteo Ferfoglia
 */
class RadixTreeNode<T> {
    
    /** The relative key of this node.
     * The absolute key, instead, is given by concatenating all keys from the
     * root of the {@link RadixTree} to this node.*/
    private String key;
    
    /** The data eventually contained by this node. */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<T> data;
    
    /** Children of this node. */
    @SuppressWarnings("rawtypes")
    private RadixTreeNode[] children;
    
    /** Constructor.
     * Parameters can be null.
     * @param key The key of this node.
     * @param data The data associated to this node.
     *             Setting this parameter to null implies that the node is not
     *             associated to any data.
     */
    RadixTreeNode(String key, T data) {
        this.key = key==null ? "" : key;
        this.data = Optional.ofNullable(data);
        children = new RadixTreeNode[0];
    }
    
    /** Constructor.
     * Creates an empty node, without any key, to use to create the root of a
     * {@link RadixTree}.
     */
    RadixTreeNode() {
        this(null, null);
    }
    
    /** Constructor.
     * @param key The key of this node.
     */
    RadixTreeNode(String key) {
        this(key, null);
    }

    /**
     * @param stringToFind The string to find in the subtree having this node as
     *                      root.
     * @return The node having as children only nodes for which the given string
     *          is the prefix of their absolute path or null if it is not found.
     */
    RadixTreeNode<T> findPrefix(String stringToFind) {
        
        if( stringToFind.length()<=key.length() && key.startsWith(stringToFind) ) {
            // found
            return this;
        }
        
        if(stringToFind.startsWith(key)) {
            String suffixToFindInChildren = stringToFind.substring(key.length());
            // All elements of children are always of the generic type T
            //noinspection unchecked
            for(RadixTreeNode<T> aChild : children) {
                RadixTreeNode<T> result = aChild.findPrefix(suffixToFindInChildren);
                if(result!=null) {
                    return result;
                }
            }
        }
        
        // If here: the key of this node does not start with the input string or
        // all children were explored
        return null;
        
    }
        
    /**
     * @param stringToInsert The string to insert.
     * @param data The data associated with the string.
     * @return the previously stored data associated to the same string if
     *          present, null otherwise.
     */
    T insert(@NotNull String stringToInsert, @NotNull T data) {
        RadixTreeNode<T> oldParent = this,
                         parent;
        
        // Find the node were to insert
        String relativeStrToInsert = new RadixTreeNode<>(stringToInsert.substring(0,1)).key;    // the input string will be processed as a key for a node (so it will be possible to compare it with other keys, being sure they are comparable, in the sense that they were processed in the same way
        int i,
            inputStringLength = stringToInsert.length();
        for( i=1;
             !oldParent.key.equals(relativeStrToInsert) 
                && (parent=oldParent.findPrefix(relativeStrToInsert))!=null;
             ) {
            oldParent = parent;
            if(i<inputStringLength) {
                relativeStrToInsert = stringToInsert.substring(i-1,++i);
            } else {
                break;
            }
        }
        relativeStrToInsert = stringToInsert.substring(--i);    // stringToInsert.substring(i) made false the condition of the for-loop, hence --i to restore the substring of the previous iteration
        
        if(oldParent.key.equals(stringToInsert)) {
            // Replace old data (same key)
            T toReturn = oldParent.data.orElse(null);
            oldParent.data = Optional.of(data);
            return toReturn;
        }
        
        
        RadixTreeNode<T> newNode;
        
        // Check if a common root is present in the key
        String parentRelativeKey = oldParent.key;
        int minLength = Math.min(relativeStrToInsert.length(), parentRelativeKey.length()),
            j;

        // for-loop needed to find if there is a common root
        //noinspection StatementWithEmptyBody
        for(j=0; j<minLength && relativeStrToInsert.charAt(j)==parentRelativeKey.charAt(j); j++) {}
        if(j>0){
            // there is a common root
            
            if(j==minLength) {
                // one node is the root of the other one                
                if( relativeStrToInsert.length()<parentRelativeKey.length() ) {
                    // the node to insert is the new parent
                    newNode = new RadixTreeNode<>(parentRelativeKey.substring(j), oldParent.data.orElse(null));
                    newNode.children = oldParent.children;
                    oldParent.key  = relativeStrToInsert;
                    oldParent.data = Optional.of(data);
                    oldParent.children = new RadixTreeNode[]{newNode};
                    return null;    // new node correctly inserted
                }
            }
        }
        
        // Update the children with the new node
        
        String keyToInsert = relativeStrToInsert.substring(j);
        int oldNumberOfChildren = oldParent.children.length;
        var newChildren = new RadixTreeNode[oldNumberOfChildren+1];
        int k;
        for( k=0;
             k<oldNumberOfChildren &&
                oldParent.children[k].key.compareTo(keyToInsert)<0;
             k++ ) {
            newChildren[k] = oldParent.children[k];
        }
        newChildren[k] = new RadixTreeNode<>(keyToInsert,data);
        System.arraycopy(oldParent.children, k, newChildren, k+1, oldNumberOfChildren-k);
        oldParent.children = newChildren;
        
        return null;
        
    }
    
    /**
     * @return The data associated to this node, as an {@link Optional} value.
     */
    Optional<T> getData() {
        return data;
    }

    /**
     * @return The relative key associated to this node.
     */
    String getKey() {
        return key;
    }
    
}