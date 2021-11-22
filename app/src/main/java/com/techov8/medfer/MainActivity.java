package com.techov8.medfer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FrameLayout frameLayout;
    private  NavigationView navigationView;

    private  int currentFragment=-1 ;
    private static final int HOME_FRAGMENT = 0;
    private static final int APPOINTMENT_FRAGMENT = 1;
    private static final int LIVE_OFFERS=2;
    private static final int BILLS_FRAGMENT=3;
    private static final int HEALTH_FACTS=4;
    private static final int WALLET=5;
    private static final int ACCOUNT_FRAGMENT=6;

    private ImageView actionBarLogo;
    private TextView actionBarText;
    private Window window;
    public static  Toolbar toolbar;
    private FirebaseUser currentUser;
    public static  DrawerLayout drawer;
    private AppBarLayout.LayoutParams params;
    private TextView fullName;
    private  Button searchBarBtn;
    String TAG ="main";

    public static boolean noConnection=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
       drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBarLogo = findViewById(R.id.action_bar_logo);
        searchBarBtn=findViewById(R.id.home_search_bar);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        actionBarText = findViewById(R.id.action_bar_text);
        params = (AppBarLayout.LayoutParams)toolbar.getLayoutParams();

        fullName =  navigationView.getHeaderView(0).findViewById(R.id.main_full_name);

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        frameLayout = findViewById(R.id.main_framelayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setFragment(new HomeFragment(), HOME_FRAGMENT);

        searchBarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBqueries.email = task.getResult().getString("email");
                            DBqueries.address = task.getResult().getString("address");
                            DBqueries.phoneNo=task.getResult().getString("phone_no");
                            DBqueries.fullName = task.getResult().getString("full_name");

                            fullName.setText(DBqueries.fullName);

                        } else {
                            String error = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        /////refer try
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            Log.e(TAG, " my referlink "+deepLink.toString());
                            //   "http://www.blueappsoftware.com/myrefer.php?custid=cust123-prod456"
                            String referlink = deepLink.toString();
                            try {

                                referlink = referlink.substring(referlink.lastIndexOf("=")+1);
                                Log.e(TAG, " substring "+referlink); //cust123-prod456

                                String custid = referlink.substring(0, referlink.indexOf("-"));
                                String prodid = referlink.substring(referlink.indexOf("-")+1);

                                Log.e(TAG, " custid "+custid +"----prpdiid "+ prodid);
                                Toast.makeText(MainActivity.this, referlink, Toast.LENGTH_SHORT).show();

                                // shareprefernce (prodid, custid);
                                //sharepreference  (refercustid, custid)



                            }catch (Exception e){
                                Log.e(TAG, " error "+e.toString());
                            }


                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });
///refer try

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(noConnection) {
            int id = item.getItemId();
           if (id == R.id.main_notification_icon) {
                Intent notificationIntent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
                return true;
            } else if (id == R.id.bill_upload_icon) {
               SharedPreferences prefs = getSharedPreferences("cardData",
                       0);
               String string = prefs.getString("data_card",
                       "");
                                    Intent uploadBillsIntent;
                                    if (string.equals("Yes")) {
                                        uploadBillsIntent = new Intent(MainActivity.this, UploadBillsActivity.class);
                                    } else {
                                        uploadBillsIntent = new Intent(MainActivity.this, CardRegistration.class);
                                    }
                                    startActivity(uploadBillsIntent);

                return true;
            }
        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentFragment == HOME_FRAGMENT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem notifyItem = menu.findItem(R.id.main_notification_icon);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            notifyIcon.setImageResource(R.drawable.notification_bell);
            TextView notifyCount = notifyItem.getActionView().findViewById(R.id.badge_count);
             DBqueries.checkNotifications(false,notifyCount);
            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                
                public void onClick(View view) {
                    Intent notificationIntent = new Intent(MainActivity.this,NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });
        }
        return true;
    }


    MenuItem menuItem;
    @SuppressWarnings("StatementWithEmptyBody")
    public  boolean onNavigationItemSelected(MenuItem item){
        final DrawerLayout drawer= findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuItem = item;

            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);

                    int id = menuItem.getItemId();
                    if (id == R.id.nav_my_home) {
                        actionBarLogo.setVisibility(View.VISIBLE);
                        actionBarText.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                        setFragment(new HomeFragment(), HOME_FRAGMENT);
                        navigationView.getMenu().getItem(0).setChecked(true);
                    } else if (id == R.id.nav_my_card) {
                        goToFragment("Your Appointments", new MyAppointmentFragment(), APPOINTMENT_FRAGMENT);
                    } else if (id == R.id.nav_live_offers) {
                        goToFragment("Live Offers", new LiveOfferFragment(), LIVE_OFFERS);

                    }else if(id==R.id.nav_my_bills){
                        goToFragment("My Prescriptions",new UserBillsFragment(),BILLS_FRAGMENT);
                    }
                    else if(id==R.id.nav_health_shop){
                        goToFragment("Health Facts",new HealthFactFragment(),HEALTH_FACTS);
                    }
                    else if(id==R.id.nav_my_wallet){
                        goToFragment("Wallet",new MyWalletFragment(),WALLET);
                    }
                    else if (id == R.id.nav_my_account) {
                        goToFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                    } else if (id == R.id.nav_privacy_policy) {
                        Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("https://www.likedeal.in/pages/termscondition.aspx"));
                        startActivity(viewIntent);
                    } else if (id == R.id.nav_my_faqs) {
                        Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("https://www.likedeal.in/pages/faq.aspx"));
                        startActivity(viewIntent);
                    } else if (id == R.id.nav_sign_out) {

                        SharedPreferences prefs3 = getSharedPreferences("uploadData", 0);
                        SharedPreferences.Editor editor3 = prefs3.edit();
                        editor3.putString("data_upload", "No");
                        editor3.apply();

                        //for card registration
                        SharedPreferences prefs2 =getSharedPreferences("cardData", 0);
                        SharedPreferences.Editor editor2 = prefs2.edit();
                        editor2.putString("data_card", "No");
                        editor2.apply();

                        SharedPreferences prefs = getSharedPreferences("LoginData", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("data", "no");
                        editor.apply();
                        //
                        FirebaseAuth.getInstance().signOut();
                        DBqueries.clearData();
                        Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                        startActivity(registerIntent);
                        finish();
                    }
                    drawer.removeDrawerListener(this);
                }
            });

            return true;


    }

    private void goToFragment(String title, Fragment fragment, int fragmentNo) {
        actionBarLogo.setVisibility(View.GONE);
        actionBarText.setVisibility(View.GONE);
       getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment,fragmentNo);

    }

    private void setFragment(Fragment fragment, int fragmentNo) {
        if(fragmentNo != currentFragment) {
        window.setStatusBarColor(Color.parseColor("#05A5B1"));
            toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
         if(fragmentNo==HOME_FRAGMENT){
             searchBarBtn.setVisibility(View.VISIBLE);
         }else{
             searchBarBtn.setVisibility(View.GONE);
         }
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if(currentFragment == HOME_FRAGMENT){
                currentFragment = -1;
                super.onBackPressed();
            }else{

                    actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);

            }
        }
    }

}