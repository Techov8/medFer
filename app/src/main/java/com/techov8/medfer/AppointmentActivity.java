package com.techov8.medfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class AppointmentActivity extends AppCompatActivity {

    private CircleImageView itemLogo;
    private TextView itemName;
    private TextView itemAddress;
    private TextView itemCategory;
    private TextView itemTiming, itemSitting;
    private Dialog loadingDialog;
    private FirebaseFirestore firebaseFirestore;
    private LinearLayout appointmentDateLayout,morningApLayout,afternoonApLayout,eveningApLayout;
    private EditText patientName,age,phoneNo,note;
    private TextView doctorFee,serviceFee,discount,gstFee, itemTotalFee,termsBtn;

    private String logo,name,category,fee;
    private String title,ID;
    private String time,date,appointmentType,genderType;

    private ConstraintLayout successfullyLayout;
    private TextView appointmentIdText,appointDateText;
    private LinearLayout allLayout;
    private TextView notScheduled;
    private String  TAG ="AppointmentActivity";
    private ProgressBar progressBar;
    private String midString ="aQOZiC81331234197942", txnAmountString="", orderIdString="", txnTokenString="";
    private Integer ActivityRequestCode = 2;
    private Button doneGoHome, paymentBtn ;
    private Toolbar toolbar;
    private Boolean isTimeSelected=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        itemLogo=findViewById(R.id.item_logo);
        itemName =findViewById(R.id.item_name);
        itemAddress =findViewById(R.id.item_address);
        itemCategory =findViewById(R.id.item_category);
        itemTiming =findViewById(R.id.item_timing);
        RadioGroup appointmentTypeRg = findViewById(R.id.appointment_type_radio_group);
        RadioGroup genderRg = findViewById(R.id.gender_radio_group);
        appointmentDateLayout=findViewById(R.id.appointment_date_layout);
        morningApLayout=findViewById(R.id.morning_appointment_layout);
        afternoonApLayout=findViewById(R.id.aftrenoon_appointment_layout);
        eveningApLayout=findViewById(R.id.evening_appointment_layout);
        patientName=findViewById(R.id.patient_name);
        age=findViewById(R.id.age);
        phoneNo=findViewById(R.id.phone_no);
        doctorFee=findViewById(R.id.doctor_fee);
        serviceFee=findViewById(R.id.service_fee);
        discount=findViewById(R.id.discount);
        gstFee=findViewById(R.id.gst_fee);
        itemTotalFee =findViewById(R.id.total_fee);
        termsBtn=findViewById(R.id.terms_btn);
        note=findViewById(R.id.note_text);
        itemSitting =findViewById(R.id.item_sitting);
        successfullyLayout=findViewById(R.id.successfully_layout);
        appointmentIdText=findViewById(R.id.appointment_id_text);
        appointDateText=findViewById(R.id.appointebt_date_txt);
        allLayout=findViewById(R.id.all_layout);
        notScheduled=findViewById(R.id.not_scheduled_yet);
        progressBar = findViewById(R.id.progressBar4);
        doneGoHome=findViewById(R.id.done_go_home);
        
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#05A5B1"));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title=getIntent().getStringExtra("ITEM_TITLE");
        getSupportActionBar().setTitle(title);
        paymentBtn = findViewById(R.id.payment_btn);
        firebaseFirestore=FirebaseFirestore.getInstance();

        loadingDialog = new Dialog(AppointmentActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ID=getIntent().getStringExtra("ITEM_ID");

        firebaseFirestore.collection("CATEGORY").document("DOCTOR").collection("ITEM_LIST").document(ID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                           name=task.getResult().getString("title")+" ("+task.getResult().getString("details")+")";
                           itemName.setText(name);
                           itemTiming.setText(task.getResult().getString("timing"));
                           category=task.getResult().getString("sub_category");
                           itemCategory.setText(category);
                           itemAddress.setText(task.getResult().getString("address"));
                           logo=task.getResult().getString("icon");
                            Glide.with(AppointmentActivity.this).load(logo).apply(new RequestOptions().placeholder(R.drawable.stethoscope)).into(itemLogo);
                            itemSitting.setText(task.getResult().getString("hospital"));
                            //fee calculation
                            String df=task.getResult().getString("fee");
                            doctorFee.setText("₹  "+df);
                            String sf=task.getResult().getString("service_fee");
                            serviceFee.setText("+ ₹  "+sf);
                            String gf=task.getResult().getString("gst_fee");
                            gstFee.setText("+ ₹  "+gf);
                            String dsf;
                            SharedPreferences prefs = getSharedPreferences("cardData",
                                    0);
                            String string = prefs.getString("data_card",
                                    "");

                            if (string.equals("Yes")) {
                                dsf=task.getResult().getString("our_discount");

                            }else{
                                dsf="0";
                            }
                            discount.setText("- ₹  "+dsf);

                            int x=Integer.parseInt(df);
                            int y=Integer.parseInt(sf);
                            int z=Integer.parseInt(gf);
                            int p=Integer.parseInt(dsf);

                            int Tf=x+y+z-p;

                            fee= Integer.toString(Tf);
                            itemTotalFee.setText("₹  "+fee);

                            ///fee calculation
                            termsBtn.setText(task.getResult().getString("terms"));
                            Calendar c=Calendar.getInstance();
                                Date d=c.getTime();
                                checkAvailableDates(d);
                                checkDateButton(0);

                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(AppointmentActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                    });

        appointmentTypeRg.check(R.id.radioButton);
        appointmentType="Normal";
        appointmentTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButton2) {
                    appointmentType = "Follow Up";
                } else {
                    appointmentType = "Normal";
                }
            }
        });
        genderRg.check(R.id.radioButton3);
        genderType="Male";
        genderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioButton4:
                        genderType="Female";
                        break;
                    case R.id.radioButton5:
                        genderType="Other";
                        break;
                    default:
                        genderType="Male";
                        break;
                }
            }
        });

///// appointment date buttons
        appointmentDateLayout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(0);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(1);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(2);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(3);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(4);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(5);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(6);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(7);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(8);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
        appointmentDateLayout.getChildAt(9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                checkDateButton(9);
                allDateInvisible();
                Button btn = (Button)view;
                checkDate(btn.getText().toString());
                 date=btn.getText().toString();
                loadingDialog.dismiss();

            }
        });
///// appointment date buttons
/////appointments time buttons
        morningApLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(morningApLayout,1);
               Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        morningApLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(morningApLayout,2);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        morningApLayout.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(morningApLayout,3);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        morningApLayout.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(morningApLayout,4);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        morningApLayout.getChildAt(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(morningApLayout,5);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,1);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,2);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,3);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,4);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,5);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        eveningApLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(eveningApLayout,1);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,2);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,3);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,4);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
        afternoonApLayout.getChildAt(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkButton(afternoonApLayout,5);
                Button b = (Button)view;
                time=b.getText().toString();
            }
        });
/////appointments time buttons

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(patientName.getText().toString())){
                    if(!TextUtils.isEmpty(age.getText().toString())){
                        if(!TextUtils.isEmpty(phoneNo.getText().toString())){
                            if(isTimeSelected) {

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                                String date = df.format(c.getTime());
                                Random rand = new Random();
                                int min =1000, max= 9999;
// nextInt as provided by Random is exclusive of the top value so you need to add 1
                                int randomNum = rand.nextInt((max - min) + 1) + min;
                                orderIdString =  date+String.valueOf(randomNum);

                                txnAmountString = fee;
                                String errors = "";
                                if(orderIdString.equalsIgnoreCase("")){
                                    errors ="Error Occured! Please try again later";
                                    Toast.makeText(AppointmentActivity.this, errors, Toast.LENGTH_SHORT).show();

                                }else
                                if(txnAmountString.equalsIgnoreCase("")){
                                    errors ="Error Occured! Please try again later";
                                    Toast.makeText(AppointmentActivity.this, errors, Toast.LENGTH_SHORT).show();

                                }else{

                                    getToken();
                                }

                            }else{
                                Toast.makeText(AppointmentActivity.this, "Please select a time slot!", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            phoneNo.requestFocus();
                        }
                    }else{
                        age.requestFocus();
                    }
                }else{
                    patientName.requestFocus();
                }

            }
        });
    }

    private void checkAvailableDates(Date d){

        firebaseFirestore.collection("CATEGORY").document("DOCTOR").collection("ITEM_LIST").document(ID).collection("Appointments_scheduled").whereGreaterThan("full_date",d).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            int y=0;
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Button btn;
                                View view = appointmentDateLayout.getChildAt(y);
                                view.setVisibility(View.VISIBLE);
                                if (view instanceof Button) {
                                    btn = (Button) view;
                                    btn.setText(documentSnapshot.getString("date"));
                                    if(y==0){
                                        checkDate(btn.getText().toString());
                                        date=btn.getText().toString();
                                    }

                                }

                                y++;
                            }
                            if(y==0){
                                notScheduled.setVisibility(View.VISIBLE);
                                paymentBtn.setEnabled(false);
                            }else{
                                notScheduled.setVisibility(View.GONE);
                                paymentBtn.setEnabled(true);
                            }
                        }
                    }
                });


    }
    private void checkDate(String date) {
        Calendar c=Calendar.getInstance();
        Date d=c.getTime();

        firebaseFirestore.collection("CATEGORY").document("DOCTOR").collection("ITEM_LIST").document(ID).collection(date).whereGreaterThan("time",d).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int morning=1,evening=1,afternoon=1;
                            int y=0;
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                if(documentSnapshot.getString("day_time").equals("morning")){
                                    morningApLayout.getChildAt(morning).setVisibility(View.VISIBLE);
                                    Button btn;
                                    View view = morningApLayout.getChildAt(morning);
                                    if(view instanceof Button){
                                        btn=(Button)view;
                                        btn.setText(documentSnapshot.getString("text"));
                                    }

                                    morning++;
                                }
                                if(documentSnapshot.getString("day_time").equals("afternoon")){
                                    afternoonApLayout.getChildAt(afternoon).setVisibility(View.VISIBLE);
                                    Button btn;
                                    View view = afternoonApLayout.getChildAt(afternoon);
                                    if(view instanceof Button){
                                        btn=(Button)view;
                                        btn.setText(documentSnapshot.getString("text"));
                                    }
                                    afternoon++;
                                }
                                if(documentSnapshot.getString("day_time").equals("evening")){
                                    eveningApLayout.getChildAt(evening).setVisibility(View.VISIBLE);
                                    Button btn;
                                    View view = eveningApLayout.getChildAt(evening);
                                    if(view instanceof Button){
                                        btn=(Button)view;
                                        btn.setText(documentSnapshot.getString("text"));
                                    }
                                    evening++;
                                }

                            }

                            morning=0;
                            evening=0;
                            afternoon=0;

                        }else{
                            String error=task.getException().getMessage();
                            Toast.makeText(AppointmentActivity.this,error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void checkButton(LinearLayout layout,int x){
    isTimeSelected=true;
      Button btn;
      for(int i=1;i<morningApLayout.getChildCount()+1;i++){

              View view = morningApLayout.getChildAt(i);
              if(view instanceof Button){
                  btn=(Button)view;
                  if(layout==morningApLayout && x==i) {
                      btn.setTextColor(getResources().getColor(R.color.white));
                      btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                  }else {
                      btn.setTextColor(getResources().getColor(R.color.black));
                      btn.setBackground(getResources().getDrawable(R.drawable.edittextbackground));
                  }

          }
      }
        for(int i=1;i<afternoonApLayout.getChildCount()+1;i++){

                View view = afternoonApLayout.getChildAt(i);
                if(view instanceof Button){
                    btn=(Button)view;
                    if(layout==afternoonApLayout && x==i) {
                        btn.setTextColor(getResources().getColor(R.color.white));
                        btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                    }else {
                        btn.setTextColor(getResources().getColor(R.color.black));
                        btn.setBackground(getResources().getDrawable(R.drawable.edittextbackground));
                    }

            }
        }
        for(int i=1;i<eveningApLayout.getChildCount()+1;i++){

                View view = eveningApLayout.getChildAt(i);
                if(view instanceof Button){
                    btn=(Button)view;
                    if(layout==eveningApLayout && x==i) {
                        btn.setTextColor(getResources().getColor(R.color.white));
                        btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                    }else {
                        btn.setTextColor(getResources().getColor(R.color.black));
                        btn.setBackground(getResources().getDrawable(R.drawable.edittextbackground));
                    }
                }
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

    private void allDateInvisible(){
        for(int x=1;x<6;x++){
            morningApLayout.getChildAt(x).setVisibility(View.GONE);
            afternoonApLayout.getChildAt(x).setVisibility(View.GONE);
            eveningApLayout.getChildAt(x).setVisibility(View.GONE);
            Button btn,btn2,btn3;
            View view = eveningApLayout.getChildAt(x);
            if(view instanceof Button){
                btn=(Button)view;

                    btn.setTextColor(getResources().getColor(R.color.black));
                    btn.setBackground(getResources().getDrawable(R.drawable.edittextbackground));

            }
            View view2 = afternoonApLayout.getChildAt(x);
            if(view2 instanceof Button){
                btn2=(Button)view2;

                btn2.setTextColor(getResources().getColor(R.color.black));
                btn2.setBackground(getResources().getDrawable(R.drawable.edittextbackground));

            }
            View view3 = afternoonApLayout.getChildAt(x);
            if(view3 instanceof Button){
                btn3=(Button)view3;

                btn3.setTextColor(getResources().getColor(R.color.black));
                btn3.setBackground(getResources().getDrawable(R.drawable.edittextbackground));

            }

        }
    }

    private void checkDateButton(int a){
        isTimeSelected=false;
        for(int x=0;x<10;x++){
                Button btn;
                View view = appointmentDateLayout.getChildAt(x);
                if (view instanceof Button) {
                    btn = (Button) view;
                    if(x!=a){
                    btn.setTextColor(getResources().getColor(R.color.black));
                    btn.setBackgroundTintList(getResources().getColorStateList(R.color.lightWhite));

                }else{
                        btn.setTextColor(getResources().getColor(R.color.white));
                        btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                    }
            }
        }
    }

    private void saveDetails() throws ParseException {

        String full_time1=time.substring(11,16);
        String full_time2=time.substring(17,19);

       String full_day1=date.substring(4,6);
       String full_day2=date.substring(7,10);
        int v= getConvertDate(full_day2);
        String month=String.valueOf(v);
        String year=date.substring(11,15);

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss a.SSSXXX");
        Date full_date = sdf.parse(year+"-"+month+"-"+full_day1+"T"+full_time1+":00"+" "+full_time2+".103+05:30");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
        final Map<String,Object> data=new HashMap<>();

        data.put("type",appointmentType);
        data.put("date",date);
        data.put("time",time);
        data.put("patient_name",patientName.getText().toString());
        data.put("patient_age",age.getText().toString());
        data.put("gender",genderType);
        data.put("note",note.getText().toString());
        data.put("contact",phoneNo.getText().toString());

        data.put("logo",logo);
        data.put("name",name);
        data.put("category",category);
        data.put("time_status","Pending");
        data.put("paid_status","Paid");
        data.put("booking_status","Booked");
        data.put("fee",fee);
        data.put("appointment_status","Live");
        data.put("appointment_full_date", full_date);
        data.put("booking_id",orderIdString);

        firebaseFirestore.collection("APPOINTMENT").document(orderIdString).set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){


                           firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("APPOINTMENT_DATA").collection("Live").document(orderIdString).set(data)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           allLayout.setVisibility(View.GONE);
                                           successfullyLayout.setVisibility(View.VISIBLE);
                                           appointDateText.setText("Appointment on: "+date);
                                           appointmentIdText.setText("Appointment ID: "+orderIdString);
                                           toolbar.setVisibility(View.GONE);
                                           doneGoHome.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                  Intent mainIntent=new Intent(AppointmentActivity.this,MainActivity.class);
                                                  startActivity(mainIntent);
                                                  finish();
                                               }
                                           });
                                       }
                                   }
                               });

                        }

                    }
                });
    }

    private int getConvertDate(String month){
        switch (month){
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return  5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return  8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
            default:
                return 0;

        }

    }
    private  void getToken(){
        Log.e(TAG, " get token start");
        progressBar.setVisibility(View.VISIBLE);
        ServiceWrapper serviceWrapper = new ServiceWrapper(null);
        Call<Token_Res> call = serviceWrapper.getTokenCall("12345", midString, orderIdString, txnAmountString);
        call.enqueue(new Callback<Token_Res>() {
            @Override
            public void onResponse(Call<Token_Res> call, Response<Token_Res> response) {
                Log.e(TAG, " respo "+ response.isSuccessful() );
                progressBar.setVisibility(View.GONE);
                try {

                    if (response.isSuccessful() && response.body()!=null){
                        if (response.body().getBody().getTxnToken()!="") {
                            Log.e(TAG, " transaction token : "+response.body().getBody().getTxnToken());
                            startPaytmPayment(response.body().getBody().getTxnToken());
                        }else {
                            Log.e(TAG, " Token status false");
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, " error in Token Res "+e.toString());
                }
            }

            @Override
            public void onFailure(Call<Token_Res> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, " response error "+t.toString());
            }
        });

    }

    public void startPaytmPayment (String token){

        txnTokenString = token;
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it
        //  String host = "https://securegw.paytm.in/";
        String orderDetails = "MID: " + midString + ", OrderId: " + orderIdString + ", TxnToken: " + txnTokenString
                + ", Amount: " + txnAmountString;
        Log.e(TAG, "order details "+ orderDetails);

        String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderIdString;
        Log.e(TAG, " callback URL "+callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdString, midString, txnTokenString, txnAmountString, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : "+bundle.toString());
            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess "+s.toString());
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth "+s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error "+s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
            }
        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, ActivityRequestCode);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG ," result code "+resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }

                try {
                    saveDetails();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
            Log.e(TAG, " data "+  data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - "+data.getStringExtra("response"));
/*
 data response - {"BANKNAME":"WALLET","BANKTXNID":"1395841115",
 "CHECKSUMHASH":"7jRCFIk6eRmrep+IhnmQrlrL43KSCSXrmMP5pH0hekXaaxjt3MEgd1N9mLtWyu4VwpWexHOILCTAhybOo5EVDmAEV33rg2VAS/p0PXdk\u003d",
 "CURRENCY":"INR","GATEWAYNAME":"WALLET","MID":"EAcR4116","ORDERID":"100620202152",
 "PAYMENTMODE":"PPI","RESPCODE":"01","RESPMSG":"Txn Success","STATUS":"TXN_SUCCESS",
 "TXNAMOUNT":"2.00","TXNDATE":"2020-06-10 16:57:45.0","TXNID":"202006101112128001101683631290118"}
  */
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }else{
            Log.e(TAG, " payment failed");
        }
    }

}