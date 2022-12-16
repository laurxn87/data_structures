package uk.ac.warwick.cs126.structures;
import java.lang.Math;
import java.util.concurrent.RecursiveTask;

//CHANGE TO TREES INSTEAD OF LINKED LISTS?? 


// This line allows us to cast our object to type (E) without any warnings.
// For further detais, please see: http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/SuppressWarnings.html
@SuppressWarnings("unchecked") 
public class HashMap<K extends Comparable<K>,V> implements IMap<K,V> {

    protected KeyValuePairLinkedList[] table;
    private int size;
    public HashMap() {
        /* for very simple hashing, primes reduce collisions */
        this(4999);
        size=0;
    }
    
    public HashMap(int size) {
        table = new KeyValuePairLinkedList[size];
        initTable();
    }

    public boolean find(K key) {
        //returns the number of comparisons required to find element using Linear Search.
       //go to the bin the same key
       int location = Math.floorMod(hash(key),table.length);
       ListElement<KeyValuePair<K,V>> ptr = table[location].head;
       while(ptr!= null){
           if(ptr.getValue().getKey().equals(key)){
              // System.out.println(ptr.getValue().getValue());
               return true;
           }else{
               ptr=ptr.getNext();
           }
       }
       //System.out.println("cant find it");
       return false;
    }
    
    protected void initTable() {
        for(int i = 0; i < table.length; i++) {
            table[i] = new KeyValuePairLinkedList<>();
        }
    }
    
    protected int hash(K key) {
        int code = key.hashCode();
        return code;    
    }
    

    public void add(K key, V value) {
        int hash_code = hash(key);
        int location = Math.floorMod(hash_code,table.length);
        
        //System.out.println("Adding " + value + " under key " + key + " at location " + location);
        
        table[location].add(key,value);
        size++;
    }

    public V get(K key) {
        int hash_code = hash(key);
        int location = Math.floorMod(hash_code,table.length);
        ListElement<KeyValuePair<K,V>> ptr = table[location].head;
        return (V) table[location].get(key).getValue();
    }

    public boolean remove(K key){
        int hash_code = hash(key);
        int location = Math.floorMod(hash_code,table.length);
        if(table[location].remove(key)!=null){
            size--;
            return true;
        }return false;
    }

    public int size(){
        return size;
    }

    public KeyValuePair<K,V>[] getAllKeyValues(){
    KeyValuePair<K,V>[] keyvals = new KeyValuePair[size];
    int i=0; 
     for(int j=0; j<table.length;j++){
        ListElement<KeyValuePair<K,V>> ptr = table[j].head;
        while(ptr!= null && i<size){
            keyvals[i] = ptr.getValue();
            //System.out.println(keyvals[i].getValue());
            i++;
            ptr = ptr.getNext();
        }
        }
    return keyvals;
    }

    public boolean isEmpty(){
        if(size ==0){
            return true;
        }
        return false;
    }

    }



