package com.techov8.medfer;

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

public class UserBillAdapter extends RecyclerView.Adapter<UserBillAdapter.ViewHolder> {

    List<UserBillModel> userBillModelList;

    private boolean isFromFact;
    private boolean isFromOffer;
    public UserBillAdapter(List<UserBillModel> userBillModelList,boolean isFromFact,boolean isFromOffer) {
        this.userBillModelList = userBillModelList;
        this.isFromFact=isFromFact;
        this.isFromOffer=isFromOffer;

    }

    @NonNull
    @Override
    public UserBillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isFromFact) {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.health_fact_item, parent, false);
        }
        else if (isFromOffer) {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.live_offer_item, parent, false);
        }else{
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_bill_item_layout, parent, false);
         }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserBillAdapter.ViewHolder holder, int position) {

        String resource=userBillModelList.get(position).getBillImage();
        String status=userBillModelList.get(position).getBillStatus();
        String discount=userBillModelList.get(position).getDiscountPercentage();
        String money=userBillModelList.get(position).getTotalAmount();
        String name=userBillModelList.get(position).getItemName();
        String subName=userBillModelList.get(position).getItemSubName();
        String detailStatus=userBillModelList.get(position).getStatus();
        holder.setData(resource,status,discount,money,name,subName,detailStatus);

    }

    @Override
    public int getItemCount() {
        return userBillModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView billImage;
        private TextView billStatus,totalAmount,cashBackMoney;
        private TextView itemName,itemSubName,detailStatus;
        public  Button viewBillBtn;
        private ImageView isUseFul,noUseful;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            if(isFromFact) {
                billImage = itemView.findViewById(R.id.fact_image);
                billStatus = itemView.findViewById(R.id.fact_headline);
                itemSubName = itemView.findViewById(R.id.fact_body);
                isUseFul = itemView.findViewById(R.id.yes_ful);
                noUseful = itemView.findViewById(R.id.no_useful);

            }else if(isFromOffer){
                billImage=itemView.findViewById(R.id.offer_image);
            }else {
                billImage = itemView.findViewById(R.id.user_bill_image);
                billStatus = itemView.findViewById(R.id.bill_status);
                cashBackMoney = itemView.findViewById(R.id.cashback_amount);
                totalAmount = itemView.findViewById(R.id.total_amount);
                itemName = itemView.findViewById(R.id.item_name);
                itemSubName = itemView.findViewById(R.id.item_sub_name);
                detailStatus = itemView.findViewById(R.id.detail_status);
                viewBillBtn = itemView.findViewById(R.id.view_bill_btn);
            }

        }
        private void setData(final String image, String status, String discount, String money, String name, String subName, String dStatus){
            if(!isFromOffer){
                itemSubName.setText(subName);
                billStatus.setText(status);
            }
            Glide.with(itemView.getContext()).load(image).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(billImage);


            if(!isFromFact && !isFromOffer) {
                if (status.equals("Verified")) {
                    itemName.setText(name);
                    detailStatus.setText(dStatus);
                    totalAmount.setText(money);
                    cashBackMoney.setText("You Saved Rs." + money + "/- on this Bill.");
                } else if (status.equals("Rejected")) {
                    itemName.setText(name);
                    detailStatus.setText(dStatus);
                    totalAmount.setText(money);
                    cashBackMoney.setText("Sorry you did not get any cashback!");
                } else {
                    itemName.setText("-");
                    detailStatus.setText("-");
                    totalAmount.setText("-");
                    cashBackMoney.setText("-");
                }

                viewBillBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBqueries.showLayout(image, itemView.getContext());
                    }
                });
            }
        }
    }
}
