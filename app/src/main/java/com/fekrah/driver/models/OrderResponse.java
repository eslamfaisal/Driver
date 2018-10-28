package com.fekrah.driver.models;

import java.io.Serializable;

public class OrderResponse implements Serializable {

    private String driver_name;
    private String Response_key;
    private String cost;
    private String distance;
    private String driver_key;
    private String driver_image;
    private String edtimated_time;
    private int driver_rating_count;
    private float driver_rating;


    public OrderResponse() {
    }

    public OrderResponse(String driver_name, String response_key, String cost, String distance, String driver_key, String driver_image, String edtimated_time, int driver_rating_count, float driver_rating) {
        this.driver_name = driver_name;
        Response_key = response_key;
        this.cost = cost;
        this.distance = distance;
        this.driver_key = driver_key;
        this.driver_image = driver_image;
        this.edtimated_time = edtimated_time;
        this.driver_rating_count = driver_rating_count;
        this.driver_rating = driver_rating;
    }

    public String getEdtimated_time() {
        return edtimated_time;
    }

    public void setEdtimated_time(String edtimated_time) {
        this.edtimated_time = edtimated_time;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getResponse_key() {
        return Response_key;
    }

    public void setResponse_key(String response_key) {
        Response_key = response_key;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDriver_key() {
        return driver_key;
    }

    public void setDriver_key(String driver_key) {
        this.driver_key = driver_key;
    }

    public String getDriver_image() {
        return driver_image;
    }

    public void setDriver_image(String driver_image) {
        this.driver_image = driver_image;
    }

    public int getDriver_rating_count() {
        return driver_rating_count;
    }

    public void setDriver_rating_count(int driver_rating_count) {
        this.driver_rating_count = driver_rating_count;
    }

    public float getDriver_rating() {
        return driver_rating;
    }

    public void setDriver_rating(float driver_rating) {
        this.driver_rating = driver_rating;
    }
}
