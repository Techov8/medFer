package com.techov8.medfer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class GridCategoryAdapter extends BaseAdapter {

    List<GridCategoryModel> gridCategoryModelList;

    boolean isGrid;
    public GridCategoryAdapter(List<GridCategoryModel> gridCategoryModelList,boolean isGrid) {
        this.gridCategoryModelList = gridCategoryModelList;
        this.isGrid=isGrid;
    }

    @Override
    public int getCount() {
        return gridCategoryModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
       final View view;

        if(convertView == null){
            ImageView categoryImage;
            TextView name;

            if(isGrid) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout, null);
                categoryImage=view.findViewById(R.id.grid_image);
                name=view.findViewById(R.id.grid_image_name);
            }else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_hospital_layout_item, null);
                categoryImage=view.findViewById(R.id.hospital_item_logo);
                name=view.findViewById(R.id.hospital_item_name);
            }

            Glide.with(parent.getContext()).load(gridCategoryModelList.get(position).getCategoryImage()).apply(new RequestOptions().placeholder(R.drawable.stethoscope)).into(categoryImage);
            String nameId=gridCategoryModelList.get(position).getCategoryTitle();
            name.setText(nameId);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isGrid) {
                            Intent productDetailsIntent = new Intent(parent.getContext(), ItemListActivity.class);
                            productDetailsIntent.putExtra("CATEGORY_ID", gridCategoryModelList.get(position).getCategoryTitle());
                            parent.getContext().startActivity(productDetailsIntent);
                        }else{
                            Intent productDetailsIntent2 = new Intent(parent.getContext(), ItemDetailsActivity.class);
                            productDetailsIntent2.putExtra("ITEM_ID",gridCategoryModelList.get(position).getId());
                            productDetailsIntent2.putExtra("ITEM_TITLE","HOSPITAL");
                            parent.getContext().startActivity(productDetailsIntent2);
                        }
                    }
                });


        }else{
            view = convertView;
        }
        return view;
    }
}
