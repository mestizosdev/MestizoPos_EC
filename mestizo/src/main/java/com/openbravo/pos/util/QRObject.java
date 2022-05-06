package com.openbravo.pos.util;

import java.nio.charset.StandardCharsets;

public class QRObject {

    private int tag;
    private int length;
    private String value;

    public QRObject(int tag, String value) {
        this.tag = tag;
        this.length = setLength(value);
        this.value = value;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLength() {
        return length;
    }

    private static int setLength(String value) {
        return value.getBytes(StandardCharsets.UTF_8).length;
    }
}
