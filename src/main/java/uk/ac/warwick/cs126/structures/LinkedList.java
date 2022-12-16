package uk.ac.warwick.cs126.structures;

public class LinkedList<E> implements IList<E> {
    
    ListElement<E> head;
    
    public LinkedList() {
        this.head = null;
    }

    public E get(int index) {
        ListElement<E> ptr = head;
        for (int i=size()-1;i>index;i--) {
            ptr = ptr.getNext();
        }
        return ptr.getValue();
    }

    public int indexOf(E element) {
        ListElement<E> ptr = head;
        int i=size()-1;
        while (ptr.getNext() != null) {
            if (element.equals(ptr.getValue())) {
                return i;
            }
            i--;
            ptr = ptr.getNext();
        }
        return -1;
    }

    public E set(int index, E element) {
        ListElement<E> ptr = head;
        for (int i=0;i<index;i++) {
            ptr = ptr.getNext();
        }
        E ret = ptr.getNext().getValue();
        ListElement<E> newlink = new ListElement<>(element);
        newlink.setNext(ptr.getNext().getNext());
        ptr.setNext(newlink);
        return ret;
    }

    public boolean add(E element) {
        ListElement<E> temp = new ListElement<>(element);
        
        // if the list is not empty, point the new link to head
        if (head != null) {
            temp.setNext(head);
        }
        // update the head
        head = temp;
        
        return true;
    }

    public void clear() {
        head = null;
    }

    public boolean contains(E element) {
        return indexOf(element) != -1;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public boolean remove(E element) {
        if (isEmpty()) return false;
        ListElement<E> ptr = head;
        while (ptr.getNext().getNext() != null) {
            if (element.equals(ptr.getNext().getValue())) {
                ptr.setNext(ptr.getNext().getNext());
                return true;
            }
            ptr = ptr.getNext();
        }
        if (element.equals(ptr.getNext().getValue())) {
            ptr.setNext(null);
            return true;
        }
        return false;
    }

    public int size() {
        if (isEmpty()) return 0;
        ListElement<E> ptr = head;
        int i=1;
        while (ptr.getNext() != null) {
            i++;
            ptr = ptr.getNext();
        }
        return i;
    }
    
    
    public String toString() {
        String ret = "";
        ListElement<E> ptr = head;
        while (ptr.getNext() != null) {
            ret += ptr.getValue()+", ";
            ptr = ptr.getNext();
        }
        ret += ptr.getValue();
        return ret;
    }

}