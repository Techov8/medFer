package com.techov8.medfer;

import android.app.Dialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;
public class DBqueries {
    public static FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
  public static String email,fullName,address,phoneNo;

    public static List<HomePageModel> lists =new ArrayList<>();
    public static List<ItemListModel> itemListModelList=new ArrayList<>();
    public static List<UserBillModel> userBillModelList=new ArrayList<>();
    public static List<UserBillModel> HealthFactModeList=new ArrayList<>();
    public static List<UserBillModel> liveOfferModelList=new ArrayList<>();
    public static List<NotificationModel> notificationModelList = new ArrayList<>();
    private static ListenerRegistration registration;
    public static List<AppointmentDetailsModel> appointmentDetailsModelList=new ArrayList<>();



    public  static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context){
        lists.clear();
        firebaseFirestore.collection("HOME")
                .orderBy("Index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                      if(((long)documentSnapshot.get("Index"))==1){
                            List<SliderModel> sliderModelList = new ArrayList<>();
                            long no_of_banners= (long)documentSnapshot.get("no_of_banners");
                            for(long x =1;x < no_of_banners+1;x++){
                                sliderModelList.add(new SliderModel(documentSnapshot.get("banner_"+x).toString(),
                                        documentSnapshot.get("banner_"+x+"_background").toString()));
                            }
                            lists.add(new HomePageModel(1,sliderModelList));

                        }else
                            if(((long)documentSnapshot.get("Index"))==2) {
                                SharedPreferences prefs = context.getSharedPreferences("cardData",
                                        0);
                                String string = prefs.getString("data_card",
                                        "");

                                if (!string.equals("Yes")) {
                                    lists.add(new HomePageModel(0, documentSnapshot.get("strip_ad_banner").toString(), documentSnapshot.get("background").toString(),2));
                                }
                            }

                        else if(((long)documentSnapshot.get("Index"))==3) {
                            List<GridCategoryModel> gridLayoutScrollModelList = new ArrayList<>();
                            long no_of_icons= (long)documentSnapshot.get("no_of_icons");
                            for(long x =1;x < no_of_icons+1;x++) {
                                gridLayoutScrollModelList.add(new GridCategoryModel(documentSnapshot.get("Icon_"+x+"_image").toString(),
                                        documentSnapshot.get("Icon_"+x+"_title").toString(),
                                        documentSnapshot.getString("id")));
                            }

                            lists.add(new HomePageModel(2,documentSnapshot.get("layout_background").toString(),gridLayoutScrollModelList,no_of_icons,"",""));

                        }

                        else if(((long)documentSnapshot.get("Index"))==4) {
                            List<GridCategoryModel> gridLayoutScrollModelList = new ArrayList<>();
                            long no_of_icons= (long)documentSnapshot.get("no_of_icons");
                            for(long x =1;x < no_of_icons+1;x++) {
                                gridLayoutScrollModelList.add(new GridCategoryModel(documentSnapshot.get("Icon_"+x+"_image").toString(),
                                        documentSnapshot.get("Icon_"+x+"_title").toString(),
                                        documentSnapshot.getString("id_"+x)));
                            }

                            lists.add(new HomePageModel(3,documentSnapshot.get("layout_background").toString(),gridLayoutScrollModelList,no_of_icons,documentSnapshot.get("heading1").toString()
                            ,documentSnapshot.get("heading2").toString()));

                        }else
                            if(((long)documentSnapshot.get("Index"))==22) {
                                    lists.add(new HomePageModel(0, documentSnapshot.get("strip_ad_banner").toString(), documentSnapshot.get("background").toString(),22));
                            }
                            else if(((long)documentSnapshot.get("Index"))==44) {
                                List<GridCategoryModel> gridLayoutScrollModelList = new ArrayList<>();
                                long no_of_icons= (long)documentSnapshot.get("no_of_icons");
                                for(long x =1;x < no_of_icons+1;x++) {
                                    gridLayoutScrollModelList.add(new GridCategoryModel(documentSnapshot.get("Icon_"+x+"_image").toString(),
                                            documentSnapshot.get("Icon_"+x+"_title").toString(),
                                            documentSnapshot.getString("id_"+x)));
                                }

                                lists.add(new HomePageModel(3,documentSnapshot.get("layout_background").toString(),gridLayoutScrollModelList,no_of_icons,documentSnapshot.get("heading1").toString()
                                        ,documentSnapshot.get("heading2").toString()));

                            }
                            else
                            if(((long)documentSnapshot.get("Index"))==222) {
                                lists.add(new HomePageModel(0, documentSnapshot.get("strip_ad_banner").toString(), documentSnapshot.get("background").toString(),22));
                            }
                            else if(((long)documentSnapshot.get("Index"))==444) {
                                List<GridCategoryModel> gridLayoutScrollModelList = new ArrayList<>();
                                long no_of_icons= (long)documentSnapshot.get("no_of_icons");
                                for(long x =1;x < no_of_icons+1;x++) {
                                    gridLayoutScrollModelList.add(new GridCategoryModel(documentSnapshot.get("Icon_"+x+"_image").toString(),
                                            documentSnapshot.get("Icon_"+x+"_title").toString(),
                                            documentSnapshot.getString("id_"+x)));
                                }

                                lists.add(new HomePageModel(3,documentSnapshot.get("layout_background").toString(),gridLayoutScrollModelList,no_of_icons,documentSnapshot.get("heading1").toString()
                                        ,documentSnapshot.get("heading2").toString()));

                            }


                    }
                    HomePageAdapter homePageAdapter = new HomePageAdapter(lists);
                    homePageRecyclerView.setAdapter(homePageAdapter);
                    homePageAdapter.notifyDataSetChanged();

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void loadItemList(final Context context, final String categoryID, final Dialog loadingDialog, final boolean isHospital){
        itemListModelList.clear();
        String c;
         if(isHospital){
            c="hospital";
         }else{
             c="category";
         }
        firebaseFirestore.collection("CATEGORY").document("DOCTOR").collection("ITEM_LIST").whereEqualTo(c,categoryID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                itemListModelList.add(new ItemListModel(
                                        documentSnapshot.getString("icon"),
                                        documentSnapshot.getString("title"),
                                        documentSnapshot.getString("address"),
                                        documentSnapshot.getString("fee"),
                                        documentSnapshot.getString("item_id"),
                                        categoryID,
                                        documentSnapshot.getString("experience"),
                                        documentSnapshot.getString("timing"),
                                        documentSnapshot.getString("location"),
                                        documentSnapshot.getString("details"),
                                        documentSnapshot.getString("hospital"),
                                        documentSnapshot.getString("sub_category")));

                            }
                            if(isHospital){
                               ItemDetailsActivity.itemListAdapter.notifyDataSetChanged();
                            }else{
                                ItemListActivity.itemListAdapter.notifyDataSetChanged();
                            }
                                loadingDialog.dismiss();
                        }else{
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadUserBill(final Context context,final Dialog loadingDialog){
        userBillModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("BILLS_DATA")
                .collection("BILLS").orderBy("time",Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                userBillModelList.add(new UserBillModel(documentSnapshot.getString("bill_image"),
                                        documentSnapshot.getString("bill_status"),
                                        documentSnapshot.getString("cashback"),
                                        documentSnapshot.getString("total_amount"),
                                        documentSnapshot.getString("name"),
                                        documentSnapshot.getString("sub_name"),
                                        documentSnapshot.getString("detailed_status")));

                            }
                            UserBillsFragment.userBillAdapter.notifyDataSetChanged();

                            loadingDialog.dismiss();
                        }else{
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public static void checkNotifications(final boolean remove,@Nullable final TextView notifyCount) {

        if(remove){
            registration.remove();
        }else{
            registration =  firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("MY_NOTIFICATION")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                            if(documentSnapshot != null && documentSnapshot.exists()){
                                notificationModelList.clear();
                                int unRead = 0;
                                for(long x = 0 ; x < (long)documentSnapshot.get("list_size"); x++){
                                    notificationModelList.add(0,new NotificationModel(documentSnapshot.get("Image_"+x).toString()
                                            ,documentSnapshot.get("Body_"+x).toString(),documentSnapshot.getBoolean("Readed_"+x)));

                                    if(!documentSnapshot.getBoolean("Readed_"+x)){
                                        unRead++;
                                        if(notifyCount != null) {
                                            if (unRead > 0) {
                                                notifyCount.setVisibility(View.VISIBLE);
                                                if (unRead < 99) {
                                                    notifyCount.setText(String.valueOf(unRead));
                                                } else {
                                                    notifyCount.setText("99");
                                                }
                                            }else{
                                                notifyCount.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }
                                if(NotificationActivity.notificationAdapter != null){
                                    NotificationActivity.notificationAdapter.notifyDataSetChanged();
                                }

                            }
                        }
                    });

        }


    }

    public static void checkAppointments(final Context context, final Dialog dialog, final String type){
        appointmentDetailsModelList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("APPOINTMENT_DATA").collection("Live").whereEqualTo("appointment_status",type).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                appointmentDetailsModelList.add(new AppointmentDetailsModel(documentSnapshot.getString("logo"),
                                        documentSnapshot.getString("name"),
                                        documentSnapshot.getString("category"),
                                        documentSnapshot.getString("time"),
                                        documentSnapshot.getString("time_status"),
                                        documentSnapshot.getString("paid_status"),
                                        documentSnapshot.getString("date"),
                                        documentSnapshot.getString("booking_status"),
                                        documentSnapshot.getString("booking_id"),
                                        documentSnapshot.getString("fee")));
                            }
                            if(type.equals("Live")){
                                LiveAppointmentFragment.appointmentDetailsAdapter.notifyDataSetChanged();
                                if(appointmentDetailsModelList.size()==0){
                                    LiveAppointmentFragment.constraintLayout.setVisibility(View.VISIBLE);
                                }else{
                                    LiveAppointmentFragment.constraintLayout.setVisibility(View.GONE);
                                }
                            }
                            if(type.equals("Cancelled")){
                                CancelledAppointmentFragment.appointmentDetailsAdapter.notifyDataSetChanged();
                                if(appointmentDetailsModelList.size()==0){
                                    CancelledAppointmentFragment.constraintLayout.setVisibility(View.VISIBLE);
                                }else{
                                    CancelledAppointmentFragment.constraintLayout.setVisibility(View.GONE);
                                }
                            }
                            if(type.equals("Past")){
                                PastAppointmentFragment.appointmentDetailsAdapter.notifyDataSetChanged();
                                if(appointmentDetailsModelList.size()==0){
                                    PastAppointmentFragment.constraintLayout.setVisibility(View.VISIBLE);
                                }else{
                                    PastAppointmentFragment.constraintLayout.setVisibility(View.GONE);
                                }
                            }
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });


    }

    public static void showLayout(String image,Context context){
        UserBillsFragment.billImageRecyclerView.setVisibility(View.GONE);
        UserBillsFragment.constraintLayout.setVisibility(View.VISIBLE);
        Glide.with(context).load(image).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(UserBillsFragment.billImage);
        MainActivity.toolbar.setVisibility(View.GONE);

    }

    public static void LoadHealthFact(final Dialog dialog){

        FirebaseFirestore.getInstance().collection("HEALTH_FACTS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {


                            HealthFactModeList.add(new UserBillModel(documentSnapshot.getString("image"),
                                    documentSnapshot.getString("headline"),
                                    "",
                                    "",
                                    "",
                                    documentSnapshot.getString("body"),
                                    ""
                            ));

                        }
                        HealthFactFragment.userBillAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
    }

    public static void LoadLiveOffers(final Dialog dialog){

        FirebaseFirestore.getInstance().collection("LIVE_OFFERS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {


                            liveOfferModelList.add(new UserBillModel(documentSnapshot.getString("image"),
                                    documentSnapshot.getString("item_id"),
                                    "",
                                    "",
                                    "",
                                    documentSnapshot.getString("name"),
                                    ""
                            ));

                        }
                        LiveOfferFragment.userBillAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
    }

    public static void clearData(){
        lists.clear();
        itemListModelList.clear();
        userBillModelList.clear();

    }
}
