package implementations;

import utilities.BSTreeADT;
import utilities.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E> {
    private BSTreeNode<E> root;
    private int size;

    public BSTree() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public BSTreeNode<E> getRoot() throws NullPointerException {
        if (root == null) throw new NullPointerException("The tree is empty.");
        return root;
    }

    @Override
    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(BSTreeNode<E> node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateHeight(node.getLeft()), calculateHeight(node.getRight()));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean contains(E entry) throws NullPointerException {
        if (entry == null) throw new NullPointerException("Entry cannot be null.");
        return search(entry) != null;
    }

    @Override
    public BSTreeNode<E> search(E entry) throws NullPointerException {
        if (entry == null) throw new NullPointerException("Entry cannot be null.");
        return searchNode(root, entry);
    }

    private BSTreeNode<E> searchNode(BSTreeNode<E> node, E entry) {
        if (node == null) return null;
        int cmp = entry.compareTo(node.getElement());
        if (cmp < 0) return searchNode(node.getLeft(), entry);
        else if (cmp > 0) return searchNode(node.getRight(), entry);
        else return node;
    }

    @Override
    public boolean add(E newEntry) throws NullPointerException {
        if (newEntry == null) throw new NullPointerException("New entry cannot be null.");
        if (root == null) {
            root = new BSTreeNode<>(newEntry);
            size++;
            return true;
        }
        return addNode(root, newEntry);
    }

    private boolean addNode(BSTreeNode<E> node, E newEntry) {
        int cmp = newEntry.compareTo(node.getElement());
        if (cmp < 0) {
            if (node.getLeft() == null) {
                node.setLeft(new BSTreeNode<>(newEntry));
                size++;
                return true;
            }
            return addNode(node.getLeft(), newEntry);
        } else if (cmp > 0) {
            if (node.getRight() == null) {
                node.setRight(new BSTreeNode<>(newEntry));
                size++;
                return true;
            }
            return addNode(node.getRight(), newEntry);
        }
        return false;
    }

    @Override
    public BSTreeNode<E> removeMin() {
        if (root == null) return null;
        BSTreeNode<E> parent = null;
        BSTreeNode<E> current = root;
        while (current.getLeft() != null) {
            parent = current;
            current = current.getLeft();
        }
        if (parent == null) root = root.getRight();
        else parent.setLeft(current.getRight());
        size--;
        return current;
    }

    @Override
    public BSTreeNode<E> removeMax() {
        if (root == null) return null;
        BSTreeNode<E> parent = null;
        BSTreeNode<E> current = root;
        while (current.getRight() != null) {
            parent = current;
            current = current.getRight();
        }
        if (parent == null) root = root.getLeft();
        else parent.setRight(current.getLeft());
        size--;
        return current;
    }

    @Override
    public Iterator<E> inorderIterator() {
        return new BSTreeIterator(root, TraversalOrder.INORDER);
    }

    @Override
    public Iterator<E> preorderIterator() {
        return new BSTreeIterator(root, TraversalOrder.PREORDER);
    }

    @Override
    public Iterator<E> postorderIterator() {
        return new BSTreeIterator(root, TraversalOrder.POSTORDER);
    }

    private enum TraversalOrder {
        INORDER, PREORDER, POSTORDER
    }
    
    
    

	// Inner class for iterator
    private class BSTreeIterator implements Iterator<E> {
        private Stack<BSTreeNode<E>> stack;
        private TraversalOrder traversalOrder;

        public BSTreeIterator(BSTreeNode<E> root, TraversalOrder traversalOrder) {
            this.stack = new Stack<>();
            this.traversalOrder = traversalOrder;

            if (root != null) {
                switch (traversalOrder) {
                    case INORDER:
                        pushLeftNodes(root);
                        break;
                    case PREORDER:
                        stack.push(root);
                        break;
                    case POSTORDER:
                        pushPostOrderNodes(root);
                        break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public E next() throws NoSuchElementException {
            if (!hasNext()) throw new NoSuchElementException("No more elements in the tree.");

            BSTreeNode<E> node = stack.pop();

            if (traversalOrder == TraversalOrder.INORDER) {
                if (node.getRight() != null) {
                    pushLeftNodes(node.getRight());
                }
            } else if (traversalOrder == TraversalOrder.PREORDER) {
                if (node.getRight() != null) {
                    stack.push(node.getRight());
                }
                if (node.getLeft() != null) {
                    stack.push(node.getLeft());
                }
            } 

            return node.getElement();
        }

        private void pushLeftNodes(BSTreeNode<E> node) {
            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }
        }

        private void pushPostOrderNodes(BSTreeNode<E> node) {
            Stack<BSTreeNode<E>> tempStack = new Stack<>();
            tempStack.push(node);

            while (!tempStack.isEmpty()) {
                BSTreeNode<E> current = tempStack.pop();
                stack.push(current);

                if (current.getLeft() != null) {
                    tempStack.push(current.getLeft());
                }
                if (current.getRight() != null) {
                    tempStack.push(current.getRight());
                }
            }
        }
    }
}




