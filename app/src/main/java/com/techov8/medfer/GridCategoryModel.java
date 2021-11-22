package com.techov8.medfer;
public class GridCategoryModel {


    private String categoryImage;
    private String categoryTitle;
    private String id;


    public GridCategoryModel(String categoryImage, String categoryTitle,String id) {
        this.categoryImage = categoryImage;
        this.categoryTitle = categoryTitle;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

}
