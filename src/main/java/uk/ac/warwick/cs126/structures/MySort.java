
package uk.ac.warwick.cs126.structures;
import java.util.Comparator;

//MAYBE ADD A VERSION THAT TAKES ARRAYLISTS AND RETURNS ARRAYS??
public class MySort<E> {

    public MySort() {
        // do i need to put anything here?

    }

    public E[] mergeSort(E[] A, E[] B, int n, Comparator<? super E> c) {
        //is there a way to stop null arrays
        if(A.length == 0){
         B = A;
        }
        else if(A.length == 1){
            B[0] = A[0];
        }else{
        copyArray(A, 0, n, B);
        split(B, 0, n, A, c);
        }
        return B;
    }

    private void split(E[] B, int start, int end, E[] A, Comparator<? super E> c) {
        if (end - start <= 1) {
            return;
        }
        // midpoint
        int mid = (start + end) / 2;
       // System.out.println(mid);
        // recursively sort both arrays from array a to array b
        // sort the left array
        split(A, start, mid, B, c);
        // sort the right array
        split(A, mid, end, B, c);
        // merge the arrays into one array
        merge(A, start, mid, end, B, c);

    }

    private void merge(E[] A, int start, int mid, int end, E[] B, Comparator<? super E> c) {
        int i = start;
        int j = mid;
        int k = start;
        // while there are elements in the left or right arraylist
        for (k = start; k < end; k++) {
            if (i < mid && (j >= end || c.compare(A[i], A[j]) <= 0)) {
                B[k] = A[i];
                i++;
            }
            else {
                B[k] = A[j];
                j++;
            }
        }

    }

    private void copyArray(E[] A, int start, int end, E[] B) {

        for (int i = start; i < end; i++) {
            B[i] = A[i];
        }
 
   }

   public int find(E[] array, int start, int end, E element,Comparator<? super E> c) {
    // returns the number of comparisons required to find element using Binary
    // Search.
    // int comparisons = 0;
    while (start <= end) {
        int mid = (int) (start + end) / 2;
        if(array[mid]!=null){
            if (c.compare(array[mid],element) == 0) {
                // comparisons++;
                return mid;
            } else if (c.compare(array[mid],element) < 0) {
                // comparisons++;
                start = mid + 1;
            } else {
                // comparisons++;
                end = mid - 1;
            }
        }else{
            return -1;
        }
    }
    // return comparisons;
    return -1;
}


}




