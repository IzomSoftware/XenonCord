package net.md_5.bungee.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.Locale;

public class ConcurrentCaseInsensitiveMap<V> implements ConcurrentMap<String, V>
{
    private final ConcurrentHashMap<String, V> delegate;

    public ConcurrentCaseInsensitiveMap()
    {
        this.delegate = new ConcurrentHashMap<>();
    }

    public ConcurrentCaseInsensitiveMap(int initialCapacity)
    {
        this.delegate = new ConcurrentHashMap<>(initialCapacity);
    }

    public ConcurrentCaseInsensitiveMap(Map<? extends String, ? extends V> map)
    {
        this.delegate = new ConcurrentHashMap<>(map.size());
        for (Map.Entry<? extends String, ? extends V> entry : map.entrySet())
        {
            delegate.put(normalize(entry.getKey()), entry.getValue());
        }
    }

    private static String normalize(String key)
    {
        return key == null ? null : key.toLowerCase(Locale.ROOT);
    }

    @Override
    public V put(String key, V value)
    {
        return delegate.put(normalize(key), value);
    }

    @Override
    public V get(Object key)
    {
        return key instanceof String ? delegate.get(normalize((String) key)) : null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue)
    {
        return key instanceof String 
            ? delegate.getOrDefault(normalize((String) key), defaultValue) 
            : defaultValue;
    }

    @Override
    public V putIfAbsent(String key, V value)
    {
        return delegate.putIfAbsent(normalize(key), value);
    }

    @Override
    public boolean replace(String key, V oldValue, V newValue)
    {
        return delegate.replace(normalize(key), oldValue, newValue);
    }

    @Override
    public V replace(String key, V value)
    {
        return delegate.replace(normalize(key), value);
    }

    @Override
    public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction)
    {
        return delegate.computeIfAbsent(normalize(key), mappingFunction);
    }

    @Override
    public V computeIfPresent(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction)
    {
        return delegate.computeIfPresent(normalize(key), remappingFunction);
    }

    @Override
    public V compute(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction)
    {
        return delegate.compute(normalize(key), remappingFunction);
    }

    @Override
    public V merge(String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        return delegate.merge(normalize(key), value, remappingFunction);
    }

    @Override
    public V remove(Object key)
    {
        return key instanceof String ? delegate.remove(normalize((String) key)) : null;
    }

    @Override
    public boolean remove(Object key, Object value)
    {
        return key instanceof String && delegate.remove(normalize((String) key), value);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return key instanceof String && delegate.containsKey(normalize((String) key));
    }

    @Override
    public boolean containsValue(Object value)
    {
        return delegate.containsValue(value);
    }

    @Override
    public int size()
    {
        return delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m)
    {
        for (Map.Entry<? extends String, ? extends V> entry : m.entrySet())
        {
            delegate.put(normalize(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public void clear()
    {
        delegate.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values()
    {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet()
    {
        return delegate.entrySet();
    }
}