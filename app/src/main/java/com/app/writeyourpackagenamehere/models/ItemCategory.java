package com.app.writeyourpackagenamehere.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemCategory {

    @PrimaryKey(autoGenerate = true)
    private int CategoryId;
    private String CategoryName;
    private String CategoryImageUrl;

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryid) {
        this.CategoryId = categoryid;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryname) {
        this.CategoryName = categoryname;
    }

    public String getCategoryImageUrl() {
        return CategoryImageUrl;

    }

    public void setCategoryImageUrl(String catimageurl) {
        this.CategoryImageUrl = catimageurl;
    }

}
