package uk.ac.warwick.cs126.structures;

/*
 * An interface for a generic stack
 */
public interface IStack<E> {
    
    // Add an element to the (top of) stacj.
    public void push(E element);
   
    // Remove and return element from the (top of) queue.
    public E pop();
    
    public boolean isEmpty();
    
    //flips the order of the stack 
    public boolean flip();

    //clears the stack
    public void clear();
    
}
