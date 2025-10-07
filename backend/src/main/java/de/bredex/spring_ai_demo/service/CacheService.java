package de.bredex.spring_ai_demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CacheService<K, V> {
    final Map<K, V> cache = new HashMap<>();

    public Optional<V> get(K key) {
        return Optional.ofNullable(this.cache.get(key));
    }

    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }
}
