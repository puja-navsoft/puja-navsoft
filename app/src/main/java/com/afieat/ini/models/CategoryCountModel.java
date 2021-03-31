package com.afieat.ini.models;

public class CategoryCountModel {

    private String categoryName;
    private int headerPos;
    private int categoryCount;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getHeaderPos() {
        return headerPos;
    }

    public void setHeaderPos(int headerPos) {
        this.headerPos = headerPos;
    }

    public int getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(int categoryCount) {
        this.categoryCount = categoryCount;
    }
}
