package com.trackaty.chat.models;

public class SocialObj {

    private String url;
    private Boolean isPublic;

    // object to use when doing atomic update children using
    public SocialObj() {

    }

    public SocialObj(String url, Boolean isPublic) {
        this.url = url;
        this.isPublic = isPublic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

}
