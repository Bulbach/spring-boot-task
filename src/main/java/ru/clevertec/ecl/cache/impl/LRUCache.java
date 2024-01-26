package ru.clevertec.ecl.cache.impl;


import ru.clevertec.ecl.cache.AbstractCache;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class LRUCache<K, V> implements AbstractCache<K, V> {

    /**
     * Переменная, которая определяет максимальный размер кэша.
     */
    private final int capacity;
    /**
     * Используется для хранения ключей и значений элементов кэша.
     */
    private final Map<K, V> cache;
    /**
     * Для сохранения порядка использования
     */
    private final Map<K, Integer> accessOrder;
    private final Deque<K> accessQueue;

    public  LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.accessOrder = new HashMap<>();
        this.accessQueue = new LinkedList<>();
    }

    public V get(K key) {
        return Optional.ofNullable(cache.get(key))
                .map(value -> {
                updateAccessOrder(key);
                    return value;
                })
                .orElse(null);
    }

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            updateAccessOrder(key);
        } else {
            if (cache.size() >= capacity) {
                evict();
            }
            cache.put(key, value);
            updateAccessOrder(key);
        }
    }

    public Collection<V> getAllValues() {
        return cache.values();
    }

    private void updateAccessOrder(K key) {
        accessOrder.put(key, accessOrder.size() + 1);
        accessQueue.remove(key);
        accessQueue.addLast(key);
    }

    public void delete(K key) {
        cache.remove(key);
        accessOrder.remove(key);
        accessQueue.remove(key);
    }

    public void evict() {
        K leastRecentlyUsed = accessQueue.pollFirst();
        if (leastRecentlyUsed != null) {
            cache.remove(leastRecentlyUsed);
            accessOrder.remove(leastRecentlyUsed);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }
}