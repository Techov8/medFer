package com.techov8.medfer;


import java.util.List;

public class HomePageModel {

    private int type;
    private String backgroundColor;

    public static final  int BANNER_SLIDER=1;
    public static final int STRIP_AD_BANNER=0;
    public static final int GRID_PRODUCT_VIEW = 2;
    public static final int HOSPITAL_VIEW=3;

  //banner slider
    private List<SliderModel> sliderModelList;
    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }
    public int getType() {
        return type;
    }
    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    //banner slider
   //strip
    private String resource;
    private int index;
    public HomePageModel(int type, String resource, String backgroundColor,int index) {
        this.type = type;
        this.resource = resource;
        this.backgroundColor = backgroundColor;
        this.index=index;
    }

    public int getIndex() {
        return index;
    }

    public String getResource() {
        return resource;
    }
    public String getBackgroundColor() {
        return backgroundColor;
    }
    //strip

////grid product
    private List<GridCategoryModel> gridCategoryModelList;
    private long itemNo;
    private String heading1,heading2;
    public HomePageModel(int type,String backgroundColor, List<GridCategoryModel> gridCategoryModelList,long itemNo,String heading1,String heading2) {
        this.type = type;
        this.gridCategoryModelList = gridCategoryModelList;
        this.backgroundColor=backgroundColor;
        this.itemNo=itemNo;
        this.heading1=heading1;
        this.heading2=heading2;
    }

    public String getHeading1() {
        return heading1;
    }

    public String getHeading2() {
        return heading2;
    }

    public List<GridCategoryModel> getGridCategoryModelList() {
        return gridCategoryModelList;
    }

    public long getItemNo() {
        return itemNo;
    }
    ////grid product

}
