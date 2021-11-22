package com.techov8.medfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailsActivity extends AppCompatActivity {

    public static ItemListAdapter itemListAdapter;
    private ImageView discountDetailsImage,itemIcon;
    private TextView itemTitle,itemAddress,itemRatings,itemTiming,services;
    private TextView visitThePlace;
    private TabLayout viewpagerIndicator;
    private ViewPager itemImagesViewPager;
    private FirebaseFirestore firebaseFirestore;
    private Dialog loadingDialog;
    private TextView callNow,location;
    private String number,locationUrl;
    private Button availNowBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String ID=getIntent().getStringExtra("ITEM_ID");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#05A5B1"));
        final String title=getIntent().getStringExtra("ITEM_TITLE");
        loadingDialog = new Dialog(ItemDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        firebaseFirestore=FirebaseFirestore.getInstance();
        itemImagesViewPager = findViewById(R.id.item_images_viewpager);
        viewpagerIndicator = findViewById(R.id.viewpager_indicator);
        callNow=findViewById(R.id.call_now);
        location=findViewById(R.id.location);
        availNowBtn=findViewById(R.id.avail_now_btn);
        final RecyclerView itemListRecyclerView = findViewById(R.id.hospital_doctor_recycler_view);
        itemIcon=findViewById(R.id.item_icon);
        itemTitle=findViewById(R.id.item_name);
        itemAddress=findViewById(R.id.item_sitting);
        itemRatings=findViewById(R.id.ratings);
        itemTiming=findViewById(R.id.timing);
        discountDetailsImage =findViewById(R.id.discount_details_image);
        visitThePlace=findViewById(R.id.visit_the_place);
        services=findViewById(R.id.services);

        final List<String> itemImages = new ArrayList<>();

        firebaseFirestore.collection("CATEGORY").document(title).collection("ITEM_LIST").document(ID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            for (long x = 1; x < (long) task.getResult().get("no_of_item_images") + 1; x++) {
                                itemImages.add(task.getResult().get("item_image_" + x).toString());
                            }

                            ItemImagesAdapter productImagesAdapter = new ItemImagesAdapter(itemImages);
                            itemImagesViewPager.setAdapter(productImagesAdapter);
                            Glide.with(ItemDetailsActivity.this).load(task.getResult().getString("icon")).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(itemIcon);
                         final  String  itemName=task.getResult().getString("title");
                            itemTitle.setText(itemName);
                            itemAddress.setText(task.getResult().getString("address"));
                            itemTiming.setText(task.getResult().getString("timing"));
                            itemRatings.setText(task.getResult().getString("ratings"));
                            Glide.with(ItemDetailsActivity.this).load(task.getResult().getString("discount_details")).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(discountDetailsImage);
                            number=task.getResult().getString("contact_no");
                            locationUrl=task.getResult().getString("location");
                            services.setText(task.getResult().getString("services"));

                            visitThePlace.setText("Visit "+itemName);
                            getSupportActionBar().setTitle(itemName);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ItemDetailsActivity.this);
                            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            itemListRecyclerView.setLayoutManager(linearLayoutManager);


                            DBqueries.itemListModelList.clear();
                            DBqueries.loadItemList(ItemDetailsActivity.this,itemName,loadingDialog,true);

                          itemListAdapter = new ItemListAdapter(DBqueries.itemListModelList);
                            itemListRecyclerView.setAdapter(itemListAdapter);


                        }else{
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ItemDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        viewpagerIndicator.setupWithViewPager(itemImagesViewPager, true);

        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               callPhoneNumber();
            }
        });

    location.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(locationUrl));
            startActivity(intent);
        }
    });
    availNowBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences prefs = getSharedPreferences("cardData",
                    0);
            String string = prefs.getString("data_card",
                    "");
            Intent uploadBillsIntent;
            if (string.equals("Yes")) {
                uploadBillsIntent = new Intent(ItemDetailsActivity.this, UploadBillsActivity.class);
            } else {
                uploadBillsIntent = new Intent(ItemDetailsActivity.this, CardRegistration.class);
            }
            startActivity(uploadBillsIntent);
        }
    });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
            else
            {
               // Log.e(TAG, "Permission not Granted");
            }
        }
    }

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(ItemDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}