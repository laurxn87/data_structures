package uk.ac.warwick.cs126.structures;

public class MyStack<E> implements IStack<E> {

    private DoublyLinkedList<E> stack = new DoublyLinkedList<E>();

    public MyStack(){
        stack = this.stack;
    }

    public void push(E val) {
        //  TODO: implement pushing
        stack.addToHead(val);
    }

    public E pop() {
        //  TODO: implement popping
        if(stack.isEmpty()){
            return null;
        }
        else{
            return stack.removeFromHead();
        }
    }

    public boolean isEmpty() {
        //  TODO: check whether list is empty
        if(stack.isEmpty())
            return true;
        else
            return false;
    }

    public boolean flip() {
        DoublyLinkedList<E> flippedStack = new DoublyLinkedList<E>();
        if(stack.getHead()!=null){
        while(stack.getHead().getNext()!=null){
            flippedStack.addToHead(stack.removeFromHead());
        }
        flippedStack.addToHead(stack.removeFromHead());
        stack = flippedStack; 
        return true;
        }
        return false;
    }

    public void clear(){
        DoublyLinkedList<E> clearStack = new DoublyLinkedList<E>();
        stack = clearStack;
    }
}
