package com.afieat.ini.models;

public class CategoryList {
    private String id;
    private String cat_type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_type() {
        return cat_type;
    }

    public void setCat_type(String cat_type) {
        this.cat_type = cat_type;
    }

    @Override
    public String toString() {
        return "CategoryList{" +
                "id='" + id + '\'' +
                ", cat_type='" + cat_type + '\'' +
                '}';
    }
}
