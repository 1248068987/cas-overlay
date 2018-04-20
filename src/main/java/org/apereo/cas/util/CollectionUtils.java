//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apereo.cas.util;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CollectionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionUtils.class);

    private CollectionUtils() {
    }

    public static Set<Object> toCollection(Object obj) {
        Set<Object> c = new LinkedHashSet();
        if (obj == null) {
            LOGGER.debug("Converting null obj to empty collection");
        } else if (obj instanceof Collection) {
            c.addAll((Collection)obj);
            LOGGER.trace("Converting multi-valued attribute [{}]", obj);
        } else if (obj instanceof Map) {
            Set<Entry> set = ((Map)obj).entrySet();
            c.addAll((Collection)set.stream().map((e) -> {
                return Pair.of(e.getKey(), e.getValue());
            }).collect(Collectors.toSet()));
        } else if (obj.getClass().isArray()) {
            c.addAll((Collection)Arrays.stream((Object[])((Object[])obj)).collect(Collectors.toSet()));
            LOGGER.trace("Converting array attribute [{}]", obj);
        } else {
            c.add(obj);
            LOGGER.trace("Converting attribute [{}]", obj);
        }

        return c;
    }

    public static <K, V> Map<K, Collection<V>> wrap(Multimap<K, V> source) {
        if (source != null && !source.isEmpty()) {
            Map inner = source.asMap();
            Map map = new HashMap();
            inner.forEach((k, v) -> {
                map.put(k, wrap(v));
            });
            return map;
        } else {
            return new HashMap(0);
        }
    }

    public static <K, V> Map<K, V> wrap(Map<K, V> source) {
        return source != null && !source.isEmpty() ? new HashMap(source) : new HashMap(0);
    }

    public static <K, V> Map<K, V> wrap(String key, Object value) {
        Map map = new HashMap();
        if (StringUtils.isNotBlank(key) && value != null) {
            map.put(key, value);
        }

        return map;
    }

    public static <K, V> Map<K, V> wrap(String key, Object value, String key2, Object value2) {
        Map m = wrap(key, value);
        m.put(key2, value2);
        return m;
    }

    public static <K, V> Map<K, V> wrap(String key, Object value, String key2, Object value2, String key3, Object value3) {
        Map m = wrap(key, value, key2, value2);
        m.put(key3, value3);
        return m;
    }

    public static <K, V> Map<K, V> wrap(String key, Object value, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
        Map m = wrap(key, value, key2, value2, key3, value3);
        m.put(key4, value4);
        return m;
    }

    public static <K, V> Map<K, V> wrap(String key, Object value, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5) {
        Map m = wrap(key, value, key2, value2, key3, value3, key4, value4);
        m.put(key5, value5);
        return m;
    }

    public static <T> List<T> wrap(T source) {
        List<T> list = new ArrayList();
        if (source != null) {
            if (source instanceof Collection) {
                Iterator it = ((Collection)source).iterator();

                while(it.hasNext()) {
                    list.add((T)it.next());
                }
            } else if (source.getClass().isArray()) {
                List elements = (List)Arrays.stream((Object[])((Object[])source)).collect(Collectors.toList());
                list.addAll(elements);
            } else {
                list.add(source);
            }
        }

        return list;
    }

    public static <T> List<T> wrap(List<T> source) {
        List<T> list = new ArrayList();
        if (source != null && !source.isEmpty()) {
            list.addAll(source);
        }

        return list;
    }

    public static <T> Set<T> wrap(Set<T> source) {
        Set<T> list = new LinkedHashSet();
        if (source != null && !source.isEmpty()) {
            list.addAll(source);
        }

        return list;
    }

    public static <T> Set<T> wrapSet(T source) {
        Set<T> list = new LinkedHashSet();
        if (source != null) {
            list.add(source);
        }

        return list;
    }

    public static <T> Set<T> wrapSet(T... source) {
        Set<T> list = new LinkedHashSet();
        addToCollection(list, source);
        return list;
    }

    public static <T> List<T> wrapList(T... source) {
        List<T> list = new ArrayList();
        addToCollection(list, source);
        return list;
    }

    private static <T> void addToCollection(Collection<T> list, T[] source) {
        if (source != null) {
            Arrays.stream(source).forEach((s) -> {
                Collection col = toCollection(s);
                list.addAll(col);
            });
        }

    }
}
