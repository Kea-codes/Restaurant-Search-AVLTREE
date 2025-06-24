/**
 * A representation of an AVL tree. This tree extends a BinaryTree and makes of 
 * AVLNodes to store entries.
 * 
 * This code works differently than the code provided in the textbook, however
 * the algorithms are the same. In all cases you can use the textbook to help
 * you get the the correct solution.
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !! DO NOT COPY THE CODE DIRECTLY FROM THE TEXTBOOK AS THIS WILL RESULT IN A !!
 * !! SOLUTION THAT DOES NOT WORK AND WILL BE A WASTE OF YOUR TIME.            !!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 *
 * @param <K> The type of the key - should be Comparable
 * @param <V> The type of the value.
 */
//Overall AVL Tree Class (35 marks) ********************************
public class AVLTree<K extends Comparable<? super K>,V> extends BinaryTree<Entry<K,V>> {
    
    /**
     * Construct a new AVL Tree
     */
    public AVLTree() {
        root = new AVLNode<Entry<K,V>>(null, null, null, null);
        size = 0;
    }
    
    /**
     * Inserts an item into the AVL tree. This will insert a new Key and Value
     * or will update an existing key or value for simplicity.
     * @param key the key to add
     * @param value the value to add
     * @return The Position where the new node has been inserted
     * 
     */
    public BTPosition<Entry<K,V>> insert(K key, V value) {
        BTPosition<Entry<K,V>> insertPos = treeSearch(key, root());
        AVLNode<Entry<K,V>> myInsertPos = checkPosition(insertPos);
        //****************** 5 marks ********************************
        //TODO: COMPLETE CODE HERE
        
     // Handle null node or external node with null element
        if (insertPos == null || isExternal(insertPos) || insertPos.element() == null) {
            return insertPos;
        }
        
        if (!isExternal(myInsertPos)) {
            // Key already exists, update value
            myInsertPos.element().setValue(value);
        } else {
            // Insert new node
            myInsertPos.setElement(new Entry<K, V>(key, value));
            myInsertPos.setLeft(new AVLNode<Entry<K, V>>(null, myInsertPos, null, null));
            myInsertPos.setRight(new AVLNode<Entry<K, V>>(null, myInsertPos, null, null));
            size++;
            checkTreeBalance(myInsertPos);
        }
        
        return myInsertPos;
    }

    /**
     * Removes an item from the AVL tree
     * @param key the key that should be removed
     * @return the removed item or null if there was nothing to remove.
     */
    public Entry<K,V> remove(K key) {
        BTPosition<Entry<K,V>> removePos = treeSearch(key, root());
        Entry<K,V> toRet = removePos.element();
        
        if (isExternal(removePos)) {
            return null;    //nothing to remove, key not found
        }
        
        //if the left child or the right child is an external node,
        // we can remove the node
        AVLNode<Entry<K,V>> left = (AVLNode<Entry<K,V>>)removePos.left();
        AVLNode<Entry<K,V>> right = (AVLNode<Entry<K,V>>)removePos.right();
        AVLNode<Entry<K,V>> actionNode;
        
        if (isExternal(left)) {
            actionNode = left;
        } else if (isExternal(right)) {
            actionNode = right;
        } else {
            //this is an internal node (no external children)
            //we need to find the inorder successor to swap the node and then perform
            // the removal the inorder successor will always be the left-most node
            // with an external child on the right of this node
            actionNode = right;
            while (!hasLeafChildren(actionNode)) {
                actionNode = (AVLNode<Entry<K,V>>)actionNode.left();
            }
            
            //once we have found the inorder successor, swap the entries from the
            //removal node with this
            removePos.setElement(actionNode.element());
        }
        
        //proceed with removal, replace my parent with my sibling
        AVLNode<Entry<K,V>> sibling = checkPosition(sibling(actionNode));
        AVLNode<Entry<K,V>> parent = checkPosition(parent(actionNode));
    
        //if my parent is the root of the tree then my tree looks like this:
        //    p                     v
        //  /  \    the result =>  / \
        // v    w
        if (isRoot(parent)) {
            root = checkPosition(sibling);
            // fix tree structure here
        } else {
            /* not an external node, my tree looks something like this:
             *      /          /
             *     p    =>    v
             *    / \        / \
             *   v   w
             *  / \
             *               
             */
            AVLNode<Entry<K,V>> grandParent = checkPosition(parent.parent());
            if (isLeftChild(parent)) {
                grandParent.setLeft(sibling);
                sibling.setParent(grandParent);
            } else {
                grandParent.setRight(sibling);
                sibling.setParent(grandParent);
            }
            
            actionNode = sibling;
        }
        
        //now recalculate the heights of the tree and restructure if necessary
        checkTreeBalance(actionNode);
        
        return toRet;
    }
    
    /**
     * Return true if the passed node has any leaf children - used for removal.
     * @param node the node that should be removed
     * @return true if the node has any leaf children.
     */
    private boolean hasLeafChildren(BTPosition<Entry<K,V>> node) {
        BTPosition<Entry<K,V>> left = node.left();
        BTPosition<Entry<K,V>> right = node.right();
        return (isExternal(left) || isExternal(right));
    }
    
    /**
     * Checks the height-balance of the AVL tree. This function will call the restructure
     * function if the height balance has be violated.
     * @param Position the current node to consider in the tree.
     */
    private void checkTreeBalance(AVLNode<Entry<K, V>> Position) {
        // if this is the root, then update the height and be done with it.
        if (isRoot(Position)) {
            AVLNode<Entry<K, V>> left = (AVLNode<Entry<K, V>>) Position.left();
            AVLNode<Entry<K, V>> right = (AVLNode<Entry<K, V>>) Position
                    .right();
            Position.setHeight(1 + Math.max(left.getHeight(), right.getHeight()));
            return;
        }
        //****************** 10 marks ********************************
        //TODO: COMPLETE CODE HERE
        
        if (isExternal(Position)) {
            Position.setHeight(0);
            return;
        }
        
        // Update height of current node
        AVLNode<Entry<K, V>> left = checkPosition(Position.left());
        AVLNode<Entry<K, V>> right = checkPosition(Position.right());
        Position.setHeight(1 + Math.max(left.getHeight(), right.getHeight()));
        
        // Get sibling
        AVLNode<Entry<K, V>> sibling = checkPosition(sibling(Position));
        
        if (!isBalanced(Position, sibling)) {
            AVLNode<Entry<K, V>> zPos = checkPosition(parent(Position));
            AVLNode<Entry<K, V>> yPos = tallerChild(zPos);
            AVLNode<Entry<K, V>> xPos = tallerChild(yPos);
            
            BTPosition<Entry<K, V>> b = rebalance(xPos);
            
            // Update heights after rebalancing
            AVLNode<Entry<K, V>> bNode = checkPosition(b);
            AVLNode<Entry<K, V>> leftChild = checkPosition(bNode.left());
            AVLNode<Entry<K, V>> rightChild = checkPosition(bNode.right());
            
            setHeight(leftChild);
            setHeight(rightChild);
            setHeight(bNode);
            
            // Continue checking balance up the tree
            if (!isRoot(bNode)) {
                checkTreeBalance(checkPosition(parent(bNode)));
            }
        } else {
            checkTreeBalance(checkPosition(parent(Position)));
        }
    }
    
    /**
     * Set the height of a node in the tree based on the heights of its children
     * @param node the node that the height should be modified for.
     */
    private void setHeight(AVLNode<Entry<K,V>> node) {
        AVLNode<Entry<K,V>> left = checkPosition(node.left());
        AVLNode<Entry<K,V>> right = checkPosition(node.right());
        node.setHeight(1 + Math.max(left.getHeight(), right.getHeight()));
    }
    
    /**
     * Obtain the taller of the children passed for a node. This is the child
     * with the greater height. This will
     * be used as part of the rebalancing function. If the nodes are of equal
     * height then base you decision on the parent. This is so that single-rotations
     * will be chosen over double-rotations.
     * @param node the node with two children.
     * @return the taller of the to children of the passed node.
     */
    private AVLNode<Entry<K,V>> tallerChild(AVLNode<Entry<K,V>> node) {
        AVLNode<Entry<K,V>> left = (AVLNode<Entry<K,V>>)node.left();
        AVLNode<Entry<K,V>> right = (AVLNode<Entry<K,V>>)node.right();
        
        if (left.getHeight() > right.getHeight()) {
            return left;
        } else if (right.getHeight() > left.getHeight()) {
            return right;
        } else {
            //the node have equal height if node was a left child return left
            // if node was a right child return right, if node is the root
            // return left
            if (isRoot(node)) {
                return left;
            }
            
            if (isLeftChild(node)) {
                return left;
            } else {
                return right;
            }
        }
    }

    /**
     * Performs a trinode restructuring.  Assumes the nodes are in one
     * of following configurations:
     *
     * <pre>
     *          z=c       z=c        z=a         z=a
     *         /  \      /  \       /  \        /  \
     *       y=b  t4   y=a  t4    t1  y=c     t1  y=b
     *      /  \      /  \           /  \         /  \
     *    x=a  t3    t1 x=b        x=b  t4       t2 x=c
     *   /  \          /  \       /  \             /  \
     *  t1  t2        t2  t3     t2  t3           t3  t4
     * </pre>
     * 
     * In all cases the result of the restructures is as follows:
     * 
     * <pre>
     *            b
     *          /   \
     *         a      c
     *        / \    / \
     *       t1 t2  t3 t4
     * </pre> 
     * @return the new root of the restructured subtree
     */
    private BTPosition<Entry<K,V>> rebalance(AVLNode<Entry<K,V>> x) {
        //the rebalance is implemented as per the diagrams above, the nodes must be obtained
        // and then the subtrees must be stored
        //BTPosition<Entry<K,V>> t1, t2, t3, t4, a, b, c;
        //AVLNode<Entry<K,V>> y = checkPosition(parent(x));
        //AVLNode<Entry<K,V>> z = checkPosition(parent(y));
        
      //****************** 15 marks ********************************
        
        //TODO: COMPLETE CODE HERE
    	// Get the parent and grandparent of x
        AVLNode<Entry<K, V>> y = checkPosition(parent(x));
        AVLNode<Entry<K, V>> z = checkPosition(parent(y));
        
        // Determine which nodes are a, b, c and their subtrees t1-t4
        AVLNode<Entry<K, V>> a, b, c;
        AVLNode<Entry<K, V>> t1, t2, t3, t4;
        
        if (isLeftChild(y) && isLeftChild(x)) {
            // Left-left case
            a = x;
            b = y;
            c = z;
            t1 = checkPosition(a.left());
            t2 = checkPosition(a.right());
            t3 = checkPosition(b.right());
            t4 = checkPosition(c.right());
        } else if (isLeftChild(y) && !isLeftChild(x)) {
            // Left-right case
            a = y;
            b = x;
            c = z;
            t1 = checkPosition(a.left());
            t2 = checkPosition(b.left());
            t3 = checkPosition(b.right());
            t4 = checkPosition(c.right());
        } else if (!isLeftChild(y) && isLeftChild(x)) {
            // Right-left case
            a = z;
            b = x;
            c = y;
            t1 = checkPosition(a.left());
            t2 = checkPosition(b.left());
            t3 = checkPosition(b.right());
            t4 = checkPosition(c.right());
        } else {
            // Right-right case
            a = z;
            b = y;
            c = x;
            t1 = checkPosition(a.left());
            t2 = checkPosition(b.left());
            t3 = checkPosition(c.left());
            t4 = checkPosition(c.right());
        }
        
        // Get parent of z (might be null if z is root)
        AVLNode<Entry<K, V>> p = checkPosition(parent(z));
        
        // Make b the new root of the subtree
        if (p == null) {
            root = b;
            b.setParent(null);
        } else {
            if (isLeftChild(z)) {
                p.setLeft(b);
            } else {
                p.setRight(b);
            }
            b.setParent(p);
        }
        
        // Set a as left child of b
        b.setLeft(a);
        a.setParent(b);
        
        // Set c as right child of b
        b.setRight(c);
        c.setParent(b);
        
        // Reattach the subtrees
        a.setLeft(t1);
        t1.setParent(a);
        a.setRight(t2);
        t2.setParent(a);
        
        c.setLeft(t3);
        t3.setParent(c);
        c.setRight(t4);
        t4.setParent(c);
        
        // Return the new root of the subtree
        return b;
    }
    
    /**
     * Check to see if two nodes are balanced, nodes will be balanced if they return a number
     * between -1 and 1.
     * The nodes should be siblings.
     * @param node1 the first node
     * @param node2 the second node
     * @return true if the nodes are balanced, false if they are not balanced
     */
    private boolean isBalanced(AVLNode<Entry<K,V>> node1, AVLNode<Entry<K,V>> node2) {
        int bf = node1.getHeight() - node2.getHeight();
        return ((bf >= -1) && (bf <= 1));
    }
    
    /**
     * Search for an item in the tree. This function is recursive. If the key that
     * you are currently looking for is less then the key in the current node then
     * call this function on the left child of the node. The the key is greater than
     * the key in this node then call this function on the right child of the node.
     * If the key is equal then call return the node.
     * @param key The key we are looking for
     * @param node the current node we are considering
     * @return A node. This will either be the node, or the position where the node should
     * exist in the tree.
     * //****************** 5 marks ********************************
     */
    public BTPosition<Entry<K,V>> treeSearch(K key, BTPosition<Entry<K,V>> node) {
       //TODO: COMPLETE CODE HERE
    	
    	// Handle null node or external node with null element
        if (node == null || isExternal(node) || node.element() == null) {
            return node;
        }
        
        Entry<K, V> entry = node.element();
        int cmp = key.compareTo(entry.getKey());
        
        if (cmp < 0) {
            return treeSearch(key, node.left());
        } else if (cmp > 0) {
            return treeSearch(key, node.right());
        } else {
            return node;
        }
    }
    
    /**
     * The AVLTree version of checkPosition, this will cast it to an AVLTree node
     */
    protected AVLNode<Entry<K,V>> checkPosition(BTPosition<Entry<K,V>> p) {
        if (!(p instanceof AVLNode<?>)) {
            throw new PositionException("Invalid Position");
        }
        
        return (AVLNode<Entry<K,V>>)p;
    }
    
    /****** Traversal Functions for Display, nothing to see here **********/
    
    /**
     * Return a string that returns a number of tabs that are specified
     * by the parameter level.
     * @param level the number of tabs to include in a returned string
     * @return a string that contains the number of tabs equal to level
     */
    public String addLevelTabs(int level) {
        String ret = "";
        for (int i = 0; i < level-1; i++) {
            ret += "    ";
        }
        if (level > 0)
            ret += "|---";
        return ret;
    }
    
    /**
     * Perform a preorder traversal of the passed tree
     */
    public String doTraversal(String currentString, BTPosition<Entry<K,V>> root, int level) {
        BTPosition<Entry<K,V>> left = root.left();
        BTPosition<Entry<K,V>> right = root.right();
        currentString += addLevelTabs(level) + root.toString() + "\n";
        String leftChildString = "";
        String rightChildString = "";
        if (left != null) {
            leftChildString = doTraversal(leftChildString, left, level+1);
        }
        if (right != null) {
            rightChildString = doTraversal(rightChildString, right, level+1);
        }
        currentString += leftChildString + rightChildString;
        return currentString;
    }
}
