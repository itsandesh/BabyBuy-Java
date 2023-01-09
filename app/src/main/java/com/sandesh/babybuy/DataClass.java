package com.sandesh.babybuy;


public class DataClass {
    private String prdTitle;
    private String prdDesc;
    private String prdPrice;
    private String prdImage;
    private String key;
    private String lat;
    private String lang;
    private String address;
    private  String userId;

    private  boolean Purchased;

    public boolean isPurchased() {
        return Purchased;
    }

    public void setPurchased(boolean purchased) {
        Purchased = purchased;
    }

    public DataClass(String userId, String prdTitle, String prdDesc, String prdPrice, String prdImage, String lat, String lang, String address, boolean Purchased) {
        this.prdTitle = prdTitle;
        this.prdDesc = prdDesc;
        this.prdPrice = prdPrice;
        this.prdImage = prdImage;
        this.lat = lat;
        this.lang = lang;
        this.address = address;
        this.userId =userId;
        this.Purchased = Purchased;
    }
    public DataClass(){
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPrdImage() {
        return prdImage;
    }

    public void setPrdImage(String prdImage) {
        this.prdImage = prdImage;
    }

    public String getPrdTitle() {
        return prdTitle;
    }

    public void setPrdTitle(String prdTitle) {
        this.prdTitle = prdTitle;
    }

    public String getPrdDesc() {
        return prdDesc;
    }

    public void setPrdDesc(String prdDesc) {
        this.prdDesc = prdDesc;
    }

    public String getPrdPrice() {
        return prdPrice;
    }

    public void setPrdPrice(String prdPrice) {
        this.prdPrice = prdPrice;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }

    }

