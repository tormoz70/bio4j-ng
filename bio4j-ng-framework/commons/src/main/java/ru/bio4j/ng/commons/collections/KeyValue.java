package ru.bio4j.ng.commons.collections;

public class KeyValue<K, V> implements Pair<K, V> {

    private K key;
    private V value;

    public KeyValue(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public K getKey() {
        return key;
    }

    @Override
    public K getLeft() {
        return key;
    }

    @Override
    public V getRight() {
        return value;
    }

    @Override
    public String toString() {
        return "KeyValue{" +
            "key=" + key +
            ", value=" + value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyValue)) return false;

        KeyValue keyValue = (KeyValue) o;

        if (key != null ? !key.equals(keyValue.key) : keyValue.key != null) return false;
        if (value != null ? !value.equals(keyValue.value) : keyValue.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
