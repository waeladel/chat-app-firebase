package com.trackaty.chat.models;

public class Profile  {

    private String key;
    private String value;
    private SocialObj social ;
    private int order;
    private int section;

    public Profile() {

    }


    public Profile(String key, String value, int order, int section) {
        this.key = key;
        this.value = value;
        this.order = order;
        this.section = section;
    }

    public Profile(String key, SocialObj social, int order, int section) {
        this.key = key;
        this.social = social;
        this.order = order;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public SocialObj getSocial() {
        return social;
    }

    public void setSocial(SocialObj social) {
        this.social = social;
    }


}
