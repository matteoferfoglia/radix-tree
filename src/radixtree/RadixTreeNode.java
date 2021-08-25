package radixtree;

import java.lang.reflect.Field;
import java.util.*;

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

    /** Copy constructor.
     * @param node The node to copy. */
    @SuppressWarnings("CopyConstructorMissesField") // Copy ctor uses another method which copies all fields with reflections
    public RadixTreeNode(RadixTreeNode<T> node) {
        copyFields(node, this);
    }

    /**
     * @param stringToFind The string to find in the subtree having this node as
     *                      root.
     * @return The node having as children only nodes for which the given string
     *          is the prefix of their absolute path or null if it is not found.
     */
    RadixTreeNode<T> findPrefix(String stringToFind) {

        if ( ! stringToFind.startsWith(this.key.substring(0, Math.min(stringToFind.length(), this.key.length())) )) {
            return null;    // no match
        }

        if( stringToFind.length()<=key.length() ) {
            return this;    // found
        }

        // otherwise: search among children
        String suffixToFindInChildren = stringToFind.substring(this.key.length());
        RadixTreeNode<T> result = null;

        // aChild has generic type T (because all children of this node (generic of type T) have the same generic type)
        //noinspection unchecked
        for(RadixTreeNode<T> aChild : children) {
            result = aChild.findPrefix(suffixToFindInChildren);
            if(result!=null) {
                break;
            }
        }
        return result;

    }
        
    /**
     * @param stringToInsert The string to insert.
     * @param data The data associated with the string.
     * @return the previously stored data associated to the same string if
     *          present, null otherwise.
     */
    T insert(@NotNull String stringToInsert, @NotNull T data) {

        // First step: find the parent of the new node to insert

        RadixTreeNode<T> parent = this,
                         oldParent = this;


        for(int i=1; i<=stringToInsert.length(); i++ ) {
            oldParent = parent;
            parent = oldParent.findPrefix(stringToInsert.substring(0,i));
            if(parent==null) {
                break;
            }
            if(parent!=oldParent) {
                stringToInsert = stringToInsert.substring(oldParent.key.length());
                i-=oldParent.key.length();  // part of the string was cut off
            }
        }
        if(parent==null) {
            parent = oldParent; // parent found
        }

        // Second step: insert the new node

        // The new node can have a common root with the parent
        int minLength = Math.min(parent.key.length(), stringToInsert.length()),
            commonRootLength;

        // for-loop used to find the length of the common root
        //noinspection StatementWithEmptyBody
        for( commonRootLength=0;
             commonRootLength<minLength && parent.key.charAt(commonRootLength)==stringToInsert.charAt(commonRootLength);
             commonRootLength++) {} // now commonRootLength is the length of the common root

        RadixTreeNode<T> newNode = new RadixTreeNode<>(stringToInsert.substring(commonRootLength), data);

        if( stringToInsert.equals(parent.key) ) {
            // Same key: the new node will replace the parent, but children are kept
            newNode.key = stringToInsert;
            newNode.children = parent.children;
            return parent.replace(newNode).data.orElse(null);
        }

        if( commonRootLength>0 && commonRootLength<minLength) {
            // a new node with the common root must be inserted: the parent and the new node will be children of this node
            RadixTreeNode<T> commonRootNode = new RadixTreeNode<>(parent.key.substring(0,commonRootLength));
            parent.key = parent.key.substring(commonRootLength);
            RadixTreeNode<T> parentBeforeInsertion = parent.replace(commonRootNode);    // now, parent is the common root
            if(parentBeforeInsertion.key.compareTo(newNode.key)<0) {
                parent.children = new RadixTreeNode[] {parentBeforeInsertion, newNode};
            } else {
                parent.children = new RadixTreeNode[] {newNode, parentBeforeInsertion};
            }
            return null;    // successful insertion
        }

        if(commonRootLength==stringToInsert.length()) {
            // the new node will replace the parent
            parent.key = parent.key.substring(commonRootLength);
            parent.children = new RadixTreeNode[] { parent.replace(newNode) };  // the new node becomes the parent
            return null;    // successful insertion
        }


        // If here, the new node must be inserted as child of the parent, at the correct position

        @SuppressWarnings("unchecked")  // all nodes and their children should have the same generic
        RadixTreeNode<T>[] newChildren = new RadixTreeNode[parent.children.length+1];

        int counter=0;

        // all nodes and their children have the same generic type
        //noinspection unchecked
        for(RadixTreeNode<T> aChild : parent.children) {    // for-loop to find the correct position where to insert the new child
            if(aChild.key.compareTo(newNode.key)<0) {
                counter++;
            } else {
                break;
            }
        }
        System.arraycopy(parent.children, 0, newChildren, 0, counter);
        newChildren[counter] = newNode;
        System.arraycopy(parent.children, counter, newChildren, counter+1, parent.children.length-counter);
        parent.children = newChildren;

        return null;

    }

    /** Replaces the current node with the given one.
     * @param newNode The new node which will replace the current one.
     * @return The old node*/
    private RadixTreeNode<T> replace(RadixTreeNode<T> newNode) {
        RadixTreeNode<T> old = new RadixTreeNode<>(this);
        copyFields(newNode, this);
        return old;
    }

    private static<T> void copyFields(RadixTreeNode<T> source, RadixTreeNode<T> dest) {
        Field[] fields = source.getClass().getDeclaredFields();
        for(Field f : fields) {
            f.setAccessible(true);
            try {
                f.set(dest,f.get(source));
            } catch (IllegalAccessException e) {
                e.printStackTrace();    // should never happen
            }
        }
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

    /**
     * @param parentKey The key of the parent. It is used to build the absolute key,
     *                  because each node stores only its relative key.
     * @return a {@link java.util.LinkedHashMap} with all children of this node (included),
     *          alphabetically ordered, having the absolute key as key and the corresponding
     *          non-null data as value.*/
    LinkedHashMap<String, T> exploreSubTree(String parentKey) {
        LinkedHashMap<String, T> subtreeMap = new LinkedHashMap<>(children.length+1);

        String absoluteKeyThisNode = parentKey+key;
        if(data.isPresent()) {
            subtreeMap.put(absoluteKeyThisNode, data.orElse(null));
        }

        // ALl children are of the generic type RadixTreeNode<T>
        //noinspection unchecked
        for(RadixTreeNode<T> aChild : children) {
             subtreeMap.putAll(aChild.exploreSubTree(absoluteKeyThisNode));
        }

        return subtreeMap;
    }

    @Override
    public String toString() {
        return '{' + key +
                (data.isPresent() ? (": " + data.orElse(null)) : "") +
                (children.length>0 ? " [" + children.length + " children]" : "") +
                '}';
    }
}