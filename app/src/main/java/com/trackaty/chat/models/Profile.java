package com.trackaty.chat.models;

public class Profile {

    private String key;
    private String value;
    private int section;

    public Profile() {

    }

    public Profile(String key, String value, int section) {
        this.key = key;
        this.value = value;
        this.section = section;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }
}
