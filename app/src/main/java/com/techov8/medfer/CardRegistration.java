package com.techov8.medfer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardRegistration extends AppCompatActivity {


    private Button addCartBtn1,addCartBtn2,payBtn;
    private ImageView firstTick,secondTick;

    private String  TAG ="CardRegistration";
    private ProgressBar progressBar;
    private String amount="349";
    private String midString ="aQOZiC81331234197942", txnAmountString="", orderIdString="", txnTokenString="";
    private Integer ActivityRequestCode = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_registration);
        addCartBtn1=findViewById(R.id.add_to_cart_button1);
        addCartBtn2=findViewById(R.id.add_to_cart_button2);
        payBtn=findViewById(R.id.pay_btn);
        firstTick=findViewById(R.id.first_tick);
        secondTick=findViewById(R.id.second_tick);
        progressBar = findViewById(R.id.progressBar3);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#05A5B1"));
       Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upgrade");


        addCartBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstTick.setVisibility(View.VISIBLE);
                secondTick.setVisibility(View.GONE);
                addCartBtn1.setText("ADDED");
                addCartBtn2.setText("ADD TO CART");
                payBtn.setText("UPGRADE NOW (₹ 235 )");
                amount="235";

            }
        });
        addCartBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondTick.setVisibility(View.VISIBLE);
                firstTick.setVisibility(View.GONE);
                addCartBtn2.setText("ADDED");
                addCartBtn1.setText("ADD TO CART");
                payBtn.setText("UPGRADE NOW (₹ 412 )");
                amount="412";

            }
        });
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String date = df.format(c.getTime());
        Random rand = new Random();
        int min =1000, max= 9999;
// nextInt as provided by Random is exclusive of the top value so you need to add 1
        int randomNum = rand.nextInt((max - min) + 1) + min;
        orderIdString =  date+String.valueOf(randomNum);

       payBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Toast.makeText(CardRegistration.this, "Service is not available for now!", Toast.LENGTH_SHORT).show();
               /////
//               txnAmountString = amount;
//               String errors = "";
//               if(orderIdString.equalsIgnoreCase("")){
//                   errors ="Enter valid Order ID here\n";
//                   Toast.makeText(CardRegistration.this, errors, Toast.LENGTH_SHORT).show();
//
//               }else
//               if(txnAmountString.equalsIgnoreCase("")){
//                   errors ="Enter valid Amount here\n";
//                   Toast.makeText(CardRegistration.this, errors, Toast.LENGTH_SHORT).show();
//
//               }else{
//
//                   getToken();
//               }
               ///////
           }
       });


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

               Intent detailIntent= new Intent(CardRegistration.this,UserDetailsActivity.class);
                startActivity(detailIntent);
                finish();
            }
            Log.e(TAG, " data "+  data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - "+data.getStringExtra("response"));

            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }else{
            Log.e(TAG, " payment failed");
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
}