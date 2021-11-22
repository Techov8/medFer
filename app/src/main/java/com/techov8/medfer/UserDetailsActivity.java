package com.techov8.medfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {
    private EditText upiNo,upiName,ifscCode,accountNo,holderName,confirmUpi,confirmAccountNo;
    private Switch upiOrBankingSwitch;
    private LinearLayout upiSelectionLayout,bankingSelectionLayout;
    private Boolean isUpi=true;
    private String upiType;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#05A5B1"));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details");
        upiNo=findViewById(R.id.upi_no);
        upiName=findViewById(R.id.upi_name);
        ifscCode=findViewById(R.id.ifsc_code);
        accountNo=findViewById(R.id.account_no);
        holderName=findViewById(R.id.holder_name);
        upiSelectionLayout=findViewById(R.id.upi_section_layout);
        bankingSelectionLayout=findViewById(R.id.banking_selection);
        upiOrBankingSwitch =findViewById(R.id.upi_selection);
        RadioGroup upiSelectionRadioGroup = findViewById(R.id.upi_selection_group);
        Button dataSaveBtn = findViewById(R.id.data_save_btn);
        confirmAccountNo=findViewById(R.id.confirm_account_no);
        confirmUpi=findViewById(R.id.confirm_upi_no);
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        upiOrBankingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(upiOrBankingSwitch.isChecked()){
                    isUpi=false;
                    upiSelectionLayout.setVisibility(View.GONE);
                    bankingSelectionLayout.setVisibility(View.VISIBLE);
                }else{
                    isUpi=true;
                    upiSelectionLayout.setVisibility(View.VISIBLE);
                    bankingSelectionLayout.setVisibility(View.GONE);
                }
            }
        });
        upiSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioButton4:
                        upiType="PAYTM";
                        break;
                    case R.id.radioButton5:
                        upiType="PHONE PE";
                        break;
                    case R.id.radioButton6:
                        upiType="GOOGLE PAY";
                        break;
                    case R.id.radioButton7:
                        upiType="AMAZON PAY";
                        break;
                    default:
                        upiType="";
                        break;
                }
            }
        });
        dataSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                                if(isUpi){
                                    if(!upiType.equals("")){
                                        if(!TextUtils.isEmpty(upiNo.getText().toString()) && confirmUpi.getText().toString().equals(upiNo.getText().toString())){
                                            if(!TextUtils.isEmpty(upiName.getText().toString())){
                                                saveCardDetails();

                                            }else{
                                                upiName.requestFocus();
                                            }
                                        }else{
                                            upiNo.requestFocus();
                                            Toast.makeText(UserDetailsActivity.this, "UPI no doesn't matches.", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(UserDetailsActivity.this, "Please select any UPI Id!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    if(!TextUtils.isEmpty(ifscCode.getText().toString())){
                                        if(!TextUtils.isEmpty(accountNo.getText().toString()) && confirmAccountNo.getText().toString().equals(accountNo.getText().toString())){
                                            if(!TextUtils.isEmpty(holderName.getText().toString())){

                                                saveCardDetails();
                                            }else{
                                                holderName.requestFocus();
                                            }
                                        }else{
                                            accountNo.requestFocus();
                                        }
                                    }else{
                                        ifscCode.requestFocus();
                                    }
                                }

            }
        });

    }

    private void saveCardDetails(){
        loadingDialog.show();
        FirebaseFirestore.getInstance().collection("CARDS").document("card_no").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                             final long cardNo;
                            cardNo=task.getResult().getLong("card_no");
                            Map<String,Object> cardData=new HashMap<>();
                            cardData.put("card_no",cardNo+1);
                            cardData.put("time", FieldValue.serverTimestamp());
                            cardData.put("is_expired",false);
                            cardData.put("name",DBqueries.fullName);
                            cardData.put("contact_no",DBqueries.phoneNo);
                            cardData.put("payment_option",isUpi);
                            cardData.put("email",DBqueries.email);
                            if(isUpi){
                                cardData.put("upi",upiType);
                                cardData.put("upi_no",upiNo.getText().toString());
                                cardData.put("upi_name",upiName.getText().toString());
                            }else {
                                cardData.put("ifsc_code", ifscCode.getText().toString());
                                cardData.put("accountNo",accountNo.getText().toString() );
                                cardData.put("holder_name",holderName.getText().toString());
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("CARD_DATA").set(cardData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                Map<String,Object> user_data=new HashMap<>();
                                                user_data.put("is_registered",true);
                                                user_data.put("card_no",cardNo+1);
                                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).update(user_data);
                                              FirebaseFirestore.getInstance().collection("CARDS").document("card_no").update("card_no",cardNo+1);
                                                SharedPreferences prefs2 =getSharedPreferences("cardData", 0);
                                                SharedPreferences.Editor editor2 = prefs2.edit();
                                                editor2.putString("data_card","Yes");
                                                editor2.apply();
                                                HomeFragment.isFromUserDetails=true;
                                                Toast.makeText(UserDetailsActivity.this, "You are registered Successfully!", Toast.LENGTH_SHORT).show();

                                                Intent homeIntent=new Intent(UserDetailsActivity.this,MainActivity.class);
                                                startActivity(homeIntent);
                                            }else{
                                                String error=task.getException().getMessage();
                                                Toast.makeText(UserDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            String error=task.getException().getMessage();
                            Toast.makeText(UserDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
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
}