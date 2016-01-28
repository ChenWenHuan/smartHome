package com.smarthome.client2.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author xingang.sun.com
 *
 * @param <K> key type
 * @param <V> value type
 */
public class SoftReferenceMap<K, V> {
    private final Map<K, SoftReference<V>> map = new HashMap<K, SoftReference<V>>();

    public V put(K key, V value) {
        SoftReference<V> r = map.put(Utils.requireNonNull(key), new SoftReference<V>(Utils.requireNonNull(value)));
        return r.get();
    }

    public V get(K key) {
        SoftReference<V> r = map.get(Utils.requireNonNull(key));
        return r == null ? null : r.get();
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        SoftReference<V> r = map.remove(Utils.requireNonNull(key));
        return r == null ? null : r.get();
    }

    public void clear() {
        map.clear();
    }
}
