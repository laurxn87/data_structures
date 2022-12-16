package uk.ac.warwick.cs126.structures;

/**
 * An interface for a generic map, from keys of type K to values of type V.
 */
public interface IMap<K,V> {
    
    // Adds a mapping from key to value to the map
    public void add(K key, V value);
    
    // Finds the respective value that is mapped to from key
    public V get(K key);

    public boolean remove(K key);

    public boolean find(K key) ;

    public int size();

    public KeyValuePair[] getAllKeyValues();

    public boolean isEmpty();




    
}
