package com.techov8.medfer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentDetailsAdapter extends RecyclerView.Adapter<AppointmentDetailsAdapter.ViewHolder> {

    List<AppointmentDetailsModel> appointmentDetailsModelList;

    public AppointmentDetailsAdapter(List<AppointmentDetailsModel> appointmentDetailsModelList) {
        this.appointmentDetailsModelList = appointmentDetailsModelList;
    }

    @NonNull
    @Override
    public AppointmentDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentDetailsAdapter.ViewHolder holder, int position) {
        String logo=appointmentDetailsModelList.get(position).getLogo();
        String name=appointmentDetailsModelList.get(position).getName();
        String category=appointmentDetailsModelList.get(position).getCategory();
        String time=appointmentDetailsModelList.get(position).getTime();
        String timeStatus=appointmentDetailsModelList.get(position).getTimeStatus();
        String paidStatus=appointmentDetailsModelList.get(position).getPaidStatus();
        String date=appointmentDetailsModelList.get(position).getDate();
        String bookingStatus=appointmentDetailsModelList.get(position).getBookingStatus();
        String bookingId=appointmentDetailsModelList.get(position).getBookingId();
        String amount=appointmentDetailsModelList.get(position).getAmount();

        holder.setData(logo,name,category,time,timeStatus,paidStatus,date,bookingStatus,bookingId,amount);
    }

    @Override
    public int getItemCount() {
        return appointmentDetailsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView docName,docCategory,apTime,apStatus,paidStatus,apDate,bookingStatus,bookingId,amountFee;
        private ImageView docLogo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            docName=itemView.findViewById(R.id.doc_name);
            docCategory=itemView.findViewById(R.id.doc_category);
            apTime=itemView.findViewById(R.id.doc_time);
            apStatus=itemView.findViewById(R.id.time_status);
            paidStatus=itemView.findViewById(R.id.paid_status);
            apDate=itemView.findViewById(R.id.doc_date);
            bookingStatus=itemView.findViewById(R.id.booking_status);
            bookingId=itemView.findViewById(R.id.booking_id);
            amountFee=itemView.findViewById(R.id.amount);
            docLogo=itemView.findViewById(R.id.doc_logo);

        }
        private void setData(String logo,String name,String category,String time,String timeStatus,String pStatus,String date,String booking,String id,String amount){
            docName.setText(name);
            docCategory.setText(category);
            apTime.setText(time);
            bookingId.setText(id);
            amountFee.setText("Amount: Rs."+amount+"/-");
            apDate.setText(date);
            Glide.with(itemView.getContext()).load(logo).apply(new RequestOptions().placeholder(R.drawable.stethoscope)).into(docLogo);
            if(booking.equals("Booked")){
                bookingStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.deepGreen));
            }else{
                bookingStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            }
            bookingStatus.setText(booking);
            paidStatus.setText(pStatus);
            if(timeStatus.equals("Live") || timeStatus.equals("Visited")){
                apStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.deepGreen));
            }else{
                apStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            }
            apStatus.setText(timeStatus);



        }
    }
}
