package com.afieat.ini.models;

/**
 * Created by amartya on 19/04/16 with love.
 */
public class Cuisine {
    private String id;
    private String name;

    public Cuisine() {
    }

    public Cuisine(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
