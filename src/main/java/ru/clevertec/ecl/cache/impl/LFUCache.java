package ru.clevertec.ecl.cache.impl;


import ru.clevertec.ecl.cache.AbstractCache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LFUCache<K, V> implements AbstractCache<K, V> {

    /**
     * Переменная, которая определяет максимальный размер кэша.
     */
    private final int capacity;
    /**
     * Используется для хранения ключей и значений элементов кэша.
     */
    private final Map<K, V> cache;
    /**
     * Используется для отслеживания частоты использования каждого элемента кэша.
     * Ключом является элемент кэша, а значением - его частота использования.
     */
    private final Map<K, Integer> frequency;
    /**
     * Используется для группировки элементов кэша
     * по их частоте использования. Ключом является частота использования,
     * а значением - LinkedHashSet элементов(для сохранения порядка элементов)
     * имеющих данную частоту использования.
     */
    private final Map<Integer, LinkedHashSet<K>> frequencyLists;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.frequency = new HashMap<>();
        this.frequencyLists = new HashMap<>();
        this.frequencyLists.put(1, new LinkedHashSet<>());
    }

    public V get(K key) {
        return Optional.ofNullable(cache.get(key))
                .map(value -> {
                    updateFrequency(key);
                    return value;
                })
                .orElse(null);
    }

    public void put(K key, V value) {
        cache.computeIfPresent(key, (k, v) -> {
            updateFrequency(k);
            return value;
        });
        if (!cache.containsKey(key)) {
            if (cache.size() >= capacity) {
                evict();
            }
            frequency.put(key, 1);
            frequencyLists.computeIfAbsent(1, k2 -> new LinkedHashSet<>()).add(key);
            cache.put(key, value);
        }
    }

    public Collection<V> getAllValues() {
        return cache.values();
    }

    private void updateFrequency(K key) {
        frequency.computeIfPresent(key, (k, v) -> {
            int freq = v + 1;
            frequencyLists.computeIfAbsent(freq, k2 -> new LinkedHashSet<>()).add(key);
            frequencyLists.computeIfPresent(v, (k2, set) -> {
                set.remove(key);
                return set;
            });
            return freq;
        });
    }

    public void delete(K key) {
        cache.remove(key);
        frequency.remove(key);
        frequencyLists.values().forEach(set -> set.remove(key));
    }

    public void evict() {
        if (cache.size() >= capacity) {
            int minFreq = frequencyLists.keySet().stream().min(Integer::compareTo).orElse(0);
            Set<K> keysWithMinFreq = frequencyLists.get(minFreq);
            if (keysWithMinFreq != null && !keysWithMinFreq.isEmpty()) {
                K evictKey = keysWithMinFreq.iterator().next();
                keysWithMinFreq.remove(evictKey);
                cache.remove(evictKey);
                frequency.remove(evictKey);
            }
        }
    }

    public boolean containsKey(K id) {
        return cache.containsKey(id);
    }

    private String defaultValue() {
        return "Value not found";
    }
}

