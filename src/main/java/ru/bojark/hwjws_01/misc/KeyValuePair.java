package ru.bojark.hwjws_01.misc;

import org.apache.http.NameValuePair;

public class KeyValuePair implements NameValuePair {
    private final String key;
    private final String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getName() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + " " + value;
    }
}
