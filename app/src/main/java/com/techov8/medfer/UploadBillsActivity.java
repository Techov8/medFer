package com.techov8.medfer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadBillsActivity extends AppCompatActivity {

    private Button saveBtn,goHomeBtn;
    private ImageView removeBtn,detailsBtn,closeBtn;
    private ImageView billImage,billDetailsImage;
    private Dialog loadingDialog;
    private Uri imageUri;
    private boolean updatePhoto = false;
    private ConstraintLayout confirmationLayout,billZoomLayout;
    private Toolbar toolbar;
    public static boolean isUpload=false;
    private AppBarLayout appBarLayout;
    private TextView noteText,uploadText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bills);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
      Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#05A5B1"));
        loadingDialog = new Dialog(UploadBillsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        removeBtn =findViewById(R.id.delete_image);
        saveBtn=findViewById(R.id.upload_btn);
        goHomeBtn=findViewById(R.id.go_back_btn);
        billImage=findViewById(R.id.bill_image);
        detailsBtn=findViewById(R.id.view_detail);
        closeBtn=findViewById(R.id.close_btn);
        billDetailsImage=findViewById(R.id.bill_details_image);
        appBarLayout=findViewById(R.id.appBarLayout);
        confirmationLayout=findViewById(R.id.confirmation_layout);
        billZoomLayout=findViewById(R.id.bill_zoom_layout);
        noteText=findViewById(R.id.note_text);
        uploadText=findViewById(R.id.up_text);

        detailsBtn.setVisibility(View.GONE);
        removeBtn.setVisibility(View.GONE);

        noteText.setVisibility(View.VISIBLE);
        noteText.setVisibility(View.VISIBLE);
        billImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);
                    }else{
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

                    }
                }else{
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);
                    }else{
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

                    }
                }else{
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(updatePhoto) {
                    loadingDialog.show();
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    updatePhoto(user);
                }else{
                    Toast.makeText(UploadBillsActivity.this, "please upload the bill First!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailsImage();
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == UploadBillsActivity.this.RESULT_OK){
                if(data != null){
                    imageUri = data.getData();
                    updatePhoto = true;
                    detailsBtn.setVisibility(View.VISIBLE);
                    removeBtn.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imageUri).into(billImage);
                    Glide.with(this).load(imageUri).into(billDetailsImage);
                }else{
                    Toast.makeText(this, "Image not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updatePhoto(final FirebaseUser user) {
        if (updatePhoto) {
           final String  image_id = UUID.randomUUID().toString().substring(0,10);
           final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Bills/" + image_id + ".jpg");
            if (imageUri != null) {

                Glide.with(this).asBitmap().load(imageUri).into(new ImageViewTarget<Bitmap>(billImage) {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        ByteArrayOutputStream boas = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                        byte[] date = boas.toByteArray();

                        UploadTask uploadTask = storageReference.putBytes(date);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                imageUri = task.getResult();
                                                final String imageData=task.getResult().toString();

                                                Glide.with(UploadBillsActivity.this).load(imageUri).load(billImage);

                                                final Map<String, Object> updateData = new HashMap<>();
                                                updateData.put("user_id", user.getUid());
                                                updateData.put("profile",imageData);
                                                updateData.put("status","Pending");
                                                updateData.put("doc_id",image_id);
                                                updateData.put("time",FieldValue.serverTimestamp());

                                                final FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                                firebaseFirestore.collection("BILLS").document(image_id).set(updateData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){

                                                                    Map<String, Object> billsData = new HashMap<>();
                                                                    billsData.put("bill_image",imageData);
                                                                    billsData.put("bill_status","Pending..");
                                                                    billsData.put("time", FieldValue.serverTimestamp());
                                                                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA")
                                                                            .document("BILLS_DATA").collection("BILLS").document(image_id).set(billsData)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){

                                                                                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).update("is_uploaded",true)
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if(task.isSuccessful()){
                                                                                                            Toast.makeText(UploadBillsActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                                                                                            showConfirmationLayout();
                                                                                                        }else{

                                                                                                            String error = task.getException().getMessage();
                                                                                                            Toast.makeText(UploadBillsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                        loadingDialog.dismiss();

                                                                                    }else{
                                                                                        loadingDialog.dismiss();
                                                                                        String error = task.getException().getMessage();
                                                                                        Toast.makeText(UploadBillsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                }
                                                                            });

                                                                }else{
                                                                    loadingDialog.dismiss();
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(UploadBillsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });

                                            } else {
                                                loadingDialog.dismiss();
                                                String error = task.getException().getMessage();
                                                Toast.makeText(UploadBillsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(UploadBillsActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return;
                    }

                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        billImage.setImageResource(R.drawable.placeholder);
                    }
                });

            }
        }else{
            loadingDialog.dismiss();
        }
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
    private void showConfirmationLayout(){
        removeBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        billImage.setEnabled(false);
        detailsBtn.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout.setVisibility(View.GONE);
        noteText.setVisibility(View.GONE);
        noteText.setVisibility(View.GONE);
        confirmationLayout.setVisibility(View.VISIBLE);
        isUpload=true;
        SharedPreferences prefs = getSharedPreferences("uploadData", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("data_upload","yes");
        editor.apply();
        goHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent=new Intent(UploadBillsActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }
    private void showDetailsImage(){
        removeBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        billImage.setEnabled(false);
        detailsBtn.setVisibility(View.GONE);
        noteText.setVisibility(View.GONE);
        noteText.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.GONE);
        billZoomLayout.setVisibility(View.VISIBLE);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billZoomLayout.setVisibility(View.GONE);
                noteText.setVisibility(View.VISIBLE);
                noteText.setVisibility(View.VISIBLE);
                removeBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                billImage.setEnabled(true);
                detailsBtn.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
            }
        });

    }
}