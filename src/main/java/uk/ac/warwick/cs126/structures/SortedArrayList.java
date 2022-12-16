package uk.ac.warwick.cs126.structures;

//import javafx.scene.control.SortEvent;

public class SortedArrayList<E extends Comparable<E>> extends MyArrayList<E> {

    public SortedArrayList() {
        super();

    }

    // INCOMPLETE.
    public boolean addSorted(E element) {
        if (element != null) {
            if (size() == 0) {
                add(element);
                return true;
            } else {
                int index = 0;
                // find the index where the element should be added
                for (int i = 0; i < size(); i++) {
                    if (get(i).compareTo(element) <= 0) {
                        index++;
                    }
                }
                //System.out.println("index: " + index);
                // make the icrementors
                E prevElement = get(size() - 1);
                int prevIndex = size() - 1;
                // add element to the end to make space for it to fit
                add(element);
                // shuffle the elements up by one
                for (int i = size() - 1; i > index; i--) {
                    if (prevIndex > 0) {
                        set(i, prevElement);
                        prevIndex--;
                        prevElement = get(prevIndex);
                    }
                }
                if (index == 0) {
                    set(index + 1, get(index));
                }
                // set the last element
                set(index, element);

                return true;
            }
        } else {
            return false;
        }

    }

    // INCOMPLETE.
    public int findLinear(E element) {
        // returns the number of comparisons required to find element using Linear
        // Search.
        int comparisons = 0;
        int index = 0;
        while (index < size()) {
            if (get(index).compareTo(element) == 0) {
                comparisons++;
                break;
            } else {
                comparisons++;
                index++;
            }
        }
        return comparisons;
    }

    // INCOMPLETE.
    public E findBinary(int start, int end, E element) {
        // returns the number of comparisons required to find element using Binary
        // Search.
        // int comparisons = 0;
        while (start <= end) {
            int mid = (start + end) / 2;
            if(get(mid)!=null){
                if (get(mid).compareTo(element) == 0) {
                    // comparisons++;
                    return element;
                } else if (get(mid).compareTo(element) < 0) {
                    // comparisons++;
                    start = mid + 1;
                } else {
                    // comparisons++;
                    end = mid - 1;
                }
            }else{
                return null;
            }
        }
        // return comparisons;
        return null;
    }

    public SortedArrayList<E> mergeSort(SortedArrayList<E> A, SortedArrayList<E> B, int n){
        if(A.size()<=1){
            return A;
        }else{
        copyArrayList(A,0,n,B);
        split(B,0,n,A);
        return B;
        }
        

    }
    private void split(SortedArrayList<E> B, int start, int end, SortedArrayList<E> A){
        if(end-start<=1){
            return;
        }
        //midpoint
        int mid = (start+end)/2;
        //System.out.println(mid);
        //recursively sort both arrays from array a to array b
        //sort the left array
        split(A,start,mid,B);
        //sort the right array
        split(A,mid,end,B);
        //merge the arrays into one array
        merge(A,start,mid,end,B);
        
    }
    private void merge(SortedArrayList<E> A,int start, int mid, int end, SortedArrayList<E> B){
        //System.out.println("merge");

        int i = start;
        //System.out.println(i);
        int j = mid;
        //System.out.println(j);
    
        int k = start;
        //while there are elements in the left or right arraylist
        for(k=start;k<end;k++){
            if(i<mid&&(j>=end || A.get(i).compareTo(A.get(j))<=0)){
                B.set(k,A.get(i));
                i++;
            }else{
                B.set(k,A.get(j));
                j++;
            }
        }

    }
    
    
    private void copyArrayList(SortedArrayList<E> A, int start, int end, SortedArrayList<E> B){
        
        for(int i=start;i<end;i++){
            B.add(A.get(i));
        }
    }

}
