package uk.ac.warwick.cs126.structures;

public class DoublyLinkedList<E> {

    private ListElement<E> head;
    private ListElement<E> tail;

    public DoublyLinkedList() {
        head = null;
        tail = null;
    }
    public ListElement<E> getHead(){
        return head;
    }

    public void addToHead(E value) {
        ListElement<E> e = new ListElement<>(value);

        if (!isEmpty()) {
            e.setNext(head);
            head.setPrev(e);
        } else { // empty
            tail = e;
        }

        head = e;
    }

    public void addToTail(E value) {
        ListElement<E> e = new ListElement<>(value);

        if (!isEmpty()) {
            tail.setNext(e);
            e.setPrev(tail);
        } else { // empty
            head = e;
        }

        tail = e;
    }

    public E removeFromHead() {
        if (isEmpty()) {
            return null;
        }

        ListElement<E> e = head;

        head = head.getNext();

        if (isEmpty()) {
            tail = null;
        } else {
            head.setPrev(null); // the first element has no predecessors
        }

        return e.getValue();
    }

    public E removeFromTail() {
        if (isEmpty()) {
            return null;
        }

        ListElement<E> e = tail;

        tail = tail.getPrev();

        if (isEmpty()) {
            head = null;
        } else {
            tail.setNext(null); // the last element has no successors
        }

        return e.getValue();
    }

    public boolean isEmpty() {
        return (head == null) || (tail == null);
    }

}
