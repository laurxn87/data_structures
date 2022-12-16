package uk.ac.warwick.cs126.structures;

public class KeyValuePairLinkedList<K extends Comparable<K>, V, E> {

    protected ListElement<KeyValuePair<K,V>> head;
    protected int size;
    
    public KeyValuePairLinkedList() {
        head = null;
        size = 0;
    }
    
    public void add(K key, V value) {

        this.add(new KeyValuePair<K,V>(key,value));
    }

    public void add(KeyValuePair<K,V> kvp) {
        ListElement<KeyValuePair<K,V>> new_element = 
                new ListElement<>(kvp);
        new_element.setNext(head);
        head = new_element;
        size++;
    }
    
    public int size() {
        return size;
    }
    
    public ListElement<KeyValuePair<K,V>> getHead() {
        return head;
    }
    
    public KeyValuePair<K,V> get(K key) {
        ListElement<KeyValuePair<K,V>> temp = head;
        
        while(temp != null) {
            if(temp.getValue().getKey().equals(key)) {
                return temp.getValue();
            }
            
            temp = temp.getNext();
        }
        
        return null;
    }

    public int indexOf(E element) {
        ListElement<KeyValuePair<K,V>> ptr = head;
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

    /*public  KeyValuePair<K,V> set(int index, K key, V value) {
        ListElement<KeyValuePair<K,V>> ptr = head;
        for (int i=0;i<index;i++) {
            ptr = ptr.getNext();
        }
        KeyValuePair<K,V> ret = ptr.getNext().getValue();
        ListElement<KeyValuePair<K,V>> newlink = new ListElement<>();
        newlink.setNext(ptr.getNext().getNext());
        ptr.setNext(newlink);
        return ret;
    }



    public boolean contains(KeyValuePair<K,V> element) {
        return indexOf(element) != -1;
    }

    

    public void clear() {
        head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }
*/

public boolean isEmpty(){
    if(head == null){
        return true;
    }
    return false;
}

    public  KeyValuePair<K,V> remove(K key) {
        if (isEmpty()) {
            return null;
        }
        ListElement<KeyValuePair<K,V>> ptr = head;
        if(ptr.getNext() == null){
            KeyValuePair<K,V> removed = head.getValue();
            head = null;
            size-- ;
            return removed;
        }
        while (ptr.getNext().getNext() != null) {
            if (ptr.getNext().getValue().getKey().equals(key)){
                KeyValuePair<K,V> removed = ptr.getNext().getValue();
                ptr.setNext(ptr.getNext().getNext());
                size--;
                return removed;
            }
            ptr = ptr.getNext();
        }
        if (ptr.getNext().getValue().getKey().equals(key)) {
            KeyValuePair<K,V> removed = ptr.getNext().getValue();
            ptr.setNext(null);
            size--;
            return removed;
        }
        return null;
    }



}
