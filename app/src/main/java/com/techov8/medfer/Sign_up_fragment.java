package com.techov8.medfer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Sign_up_fragment extends Fragment {

    public Sign_up_fragment() {
        // Required empty public constructor
    }

    private EditText email, fullName, password, confirmPassword;
    private Button signUpBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private TextView alreadyHaveAnAccount,signUpTermBtn;
    private FrameLayout parentFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_fragment, container, false);

        alreadyHaveAnAccount = view.findViewById(R.id.tv_already_have_an_account);
        parentFrameLayout = requireActivity().findViewById(R.id.register_framelayout);

        email = view.findViewById(R.id.sign_up_email);
        fullName = view.findViewById(R.id.sign_up_full_name);
        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_confirm_password);
        signUpBtn = view.findViewById(R.id.sign_up_btn);
        progressBar = view.findViewById(R.id.sign_up_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        signUpTermBtn=view.findViewById(R.id.sign_up_term_btn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new Sign_in_fragment());
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signUpTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("https://www.likedeal.in/pages/termscondition.aspx"));
                startActivity(viewIntent);
            }
        });
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkInputs();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });
    }


    private void setFragment(Sign_in_fragment sign_in_Fragment) {

        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_fron_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), sign_in_Fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(fullName.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 7) {
                    if (!TextUtils.isEmpty(confirmPassword.getText())) {

                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(Color.rgb(255, 255, 255));

                    } else {
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
                    }
                } else {
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(Color.argb(50, 255, 255, 255));

        }
    }

    private void checkEmailAndPassword() {
        String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        if (email.getText().toString().matches(email_pattern)) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                progressBar.setVisibility(View.VISIBLE);

            signUpBtn.setEnabled(false);
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                final FirebaseUser user = task.getResult().getUser();
                                String uid = user.getUid();
                                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                final DocumentReference docRef = db.collection("USERS").document(uid);

                                                   Map<String, Object> data = new HashMap<>();
                                                   data.put("full_name", fullName.getText().toString());
                                                   data.put("email", email.getText().toString());
                                                   data.put("phone_no", "");
                                                   data.put("profile", "");
                                                   data.put("address", "");
                                                   data.put("gender", "");
                                                   data.put("is_registered", false);
                                                   data.put("is_uploaded", false);
                                                   data.put("card_no", "");
                                                   docRef.set(data);

                                                   Map<String, Object> nDATA = new HashMap<>();
                                                   nDATA.put("list_size", 0);
                                                   docRef.collection("USERS_DATA").document("MY_NOTIFICATION").set(nDATA);

                                                   //for uploading bills
                                                   SharedPreferences prefs = getActivity().getSharedPreferences("uploadData", 0);
                                                   SharedPreferences.Editor editor = prefs.edit();
                                                   editor.putString("data_upload", "No");
                                                   editor.apply();

                                                   //for card registration
                                                   SharedPreferences prefs2 = getActivity().getSharedPreferences("cardData", 0);
                                                   SharedPreferences.Editor editor2 = prefs2.edit();
                                                   editor2.putString("data_card", "No");
                                                   editor2.apply();


                                                   SharedPreferences prefs3 = getActivity().getSharedPreferences("LoginData", 0);
                                                   SharedPreferences.Editor editor3 = prefs3.edit();
                                                   editor3.putString("data", "Yes");
                                                   editor3.apply();

                                                   mainIntent();

                                               } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                signUpBtn.setEnabled(true);
                                signUpBtn.setTextColor(Color.rgb(255,255,255));

                            }
                        }
                    });
        }else{
                confirmPassword.setError("Password doesn't matched");
            }
        }else{
            email.setError("Invalid Email");
        }

    }
    private void mainIntent(){

            Intent ii = new Intent(getActivity(), MainActivity.class);
            startActivity(ii);
            getActivity().finish();
    }
}


