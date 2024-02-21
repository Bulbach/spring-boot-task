package ru.clevertec.ecl.cache;

import java.util.Collection;

public interface AbstractCache<K,V>{
    V get(K k);
    void put(K k, V v);
    Collection<V> getAllValues();
    void evict();
    void delete(K key);
    boolean containsKey(K id);
}
