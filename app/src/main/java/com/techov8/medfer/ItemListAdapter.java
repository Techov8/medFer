package com.techov8.medfer;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private  boolean fromSearch;
    List<ItemListModel> itemListModelList;

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public void setItemListModelList(List<ItemListModel> itemListModelList) {
        this.itemListModelList = itemListModelList;
    }

    public ItemListAdapter(List<ItemListModel> itemListModelList) {
        this.itemListModelList = itemListModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String resource = itemListModelList.get(position).getItemIcon();
        String title = itemListModelList.get(position).getItemTitle();
        String address = itemListModelList.get(position).getItemAddress();
        String discount = itemListModelList.get(position).getItemDiscount();
        String Id=itemListModelList.get(position).getItemID();
        String homeTitleId=itemListModelList.get(position).getHomeTitleID();
        String experience=itemListModelList.get(position).getExperience();
        String timing=itemListModelList.get(position).getTiming();
        String location=itemListModelList.get(position).getLocation();
        String details=itemListModelList.get(position).getDetails();
        String sitting=itemListModelList.get(position).getSittingWhere();
        String sub=itemListModelList.get(position).getSubCategory();

        holder.setData(resource,title,address,discount,Id,homeTitleId,experience,timing,location,details,sitting,sub);
    }

    @Override
    public int getItemCount() {
        return itemListModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView itemIcon;
        private TextView itemTitle,itemAddress,itemDiscount,experience,timing,category,location,itemDetails;
        private Button itemDetailsButton;
        private ImageView viewLocationBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon=itemView.findViewById(R.id.item_circle_icon);
            itemTitle=itemView.findViewById(R.id.item_list_title);
            itemAddress=itemView.findViewById(R.id.item_list_address);
            itemDiscount=itemView.findViewById(R.id.item_price_details);
            experience=itemView.findViewById(R.id.experience);
            timing=itemView.findViewById(R.id.timing);
            category=itemView.findViewById(R.id.item_list_category);
            location=itemView.findViewById(R.id.item_location);
            itemDetailsButton=itemView.findViewById(R.id.item_list_details_btn);
            itemDetails=itemView.findViewById(R.id.item_details);
            viewLocationBtn=itemView.findViewById(R.id.view_location_btn);
        }

        private void setData(String icon, final String title, String address, String discount, final String Id, final String homeTitleId
        , String workingSince, String time, final String place,String details,String sitting,String sub){
            Glide.with(itemView.getContext()).load(icon).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(itemIcon);
            itemTitle.setText(title);
            itemAddress.setText(sitting);
           itemDetails.setText(details);
           category.setText(sub);
            timing.setText("Timing: "+time);
            location.setText(address);

                viewLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(place));
                        itemView.getContext().startActivity(intent);
                    }
                });


                itemDetailsButton.setText(R.string.book_appointment);
                itemDiscount.setText("₹ "+discount);
                experience.setText(String.format("%s+ year Exp", workingSince));
                itemDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent itemDetailsIntent=new Intent(itemView.getContext(),AppointmentActivity.class);
                        itemDetailsIntent.putExtra("ITEM_ID",Id);
                        itemDetailsIntent.putExtra("ITEM_TITLE",homeTitleId);
                        itemView.getContext().startActivity(itemDetailsIntent);
                    }
                });





        }
    }
}
