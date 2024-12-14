package com.example.skripsi.Data;

public class Order {

    private int nomorUrut;
    private String name;
    private String order;
    private int estimatedTime;

    public Order() {
    }

    public Order(int nomorUrut, String name, String order, int estimatedTime) {
        this.nomorUrut = nomorUrut;
        this.name = name;
        this.order = order;
        this.estimatedTime = estimatedTime;
    }

    public int getNomorUrut() {
        return nomorUrut;
    }

    public void setNomorUrut(int nomorUrut) {
        this.nomorUrut = nomorUrut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
