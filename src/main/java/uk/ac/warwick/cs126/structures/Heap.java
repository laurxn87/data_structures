package uk.ac.warwick.cs126.structures;

import uk.ac.warwick.cs126.structures.KeyValuePair;
import uk.ac.warwick.cs126.structures.MyArrayList;

import java.util.Comparator;
@SuppressWarnings("unchecked")

//arraylist based implementation of a heap - still a little iffy on some bits- should i make it all one thing like an array or more like my sorting one
public class Heap<E>{
    private final Comparator<E> c;
    private Object[] heap;
    private int size;
    private int maxsize;


    public Heap(int maxsize, Comparator<E> comparator)
    {
        this.maxsize = maxsize;
        this.size = 0;
        heap = new Object[this.maxsize + 1];
        this.c = comparator;
        //heap[0] = Integer.MIN_VALUE;
    }

    // Function to return the positionition of
    // the parent for the node currently
    // at position
    private int parent(int position)
    {
        return (position -1) / 2;
    }

    // Function to return the positionition of the
    // left child for the node currently at position
    private int leftChild(int position)
    {
        return (2 * position) + 1;
    }

    // Function to return the positionition of
    // the right child for the node currently
    // at position
    private int rightChild(int position)
    {
        return (2 * position) + 2;
    }

    // Function that returns true if the passed
    // node is a leaf node
private boolean hasLeft(int position){
        return leftChild(position) < size;
}
private boolean hasRight(int position){
        return rightChild(position) < size;
}

    public int size() {
    return size;
    }


    // Function to swap two nodes of the heap
    private void swap(int index1, int index2)
    {
        Object temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
    }

    // Function to insert a node into the heap
    public void insert(E element) {
        if (size >= maxsize) {
            return;
        } else {
            heap[size] = element;
            upheap(size());
            size++;
            //System.out.println(size);

        }
    }
    public void upheap(int pos){
        int parent = parent(pos);
        if(Comparator.nullsLast(c).compare((E) heap[pos],(E) heap[parent])<0){
            swap(pos,parent);
            upheap(parent);
        }
    }
    public void downheap(int pos){
        if(hasLeft(pos)) {
            int left = leftChild(pos);
            //assuming the left child is smaller
            int smaller = left;
            if (hasRight(pos)) {
                int right = rightChild(pos);
                if (Comparator.nullsLast(c).compare((E) heap[left], (E) heap[right]) > 0) {
                    smaller = right;
                }
            }

            //CHECK FOR THE RECURSIVE CASE
            if (Comparator.nullsLast(c).compare((E)heap[smaller], (E)heap[pos]) < 0) {
                swap(pos,smaller);
                downheap(smaller);
            }
        }
    }


    public Object peek(){
    return heap[0];
    }


    // Function to remove and return the minimum
    // element from the heap
    public Object remove()
    {
        Object removed = heap[0];
        swap(0,size()-1);
        heap[size()-1] = null;
        downheap(0);
        size--;
        return removed;
    }
    public E[] topK(MyArrayList<E> array, E[] topK, int k,Comparator<E> comparator) {
        if (k < 0) {
            throw new IllegalArgumentException();
        } else if (k == 0) {
            System.out.println("k cannot be zero");
            return null;
        } else {
            Heap<E> top = new Heap<E>(k, comparator);
            int counter = 0;
            //build the heap
            for (int i = 0; i < array.size(); i++) {
                //if there is less than k items in the heap just add it
                if (top.size() < k) {
                    //System.out.println(array[i]);
                    top.insert(array.get(i));
                    counter++;
                }//if there is more than k items then only add if the item is larger than the minimum then remove the old minimum
                else if (comparator.compare(array.get(i), (E) top.peek()) > 0) {
                   // System.out.println("fuck");
                    top.remove(); //remove the last node
                    top.insert(array.get(i));
                }
            }
            //System.out.println("shit");
            for (int i = 0; i <counter; i++) {
                topK[i] = (E) top.remove();
                //System.out.println(topK[i]);
               // System.out.println(heap[i]);
            }
            return topK;
        }
    }
}
