package com.techov8.medfer;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MyAccountFragment extends Fragment {


    public MyAccountFragment() {
    }
    private TextView nameBtn,emailBtn;
    private EditText name,mobile,email,address;
    private String gd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_account, container, false);

        nameBtn = view.findViewById(R.id.user_name);
        emailBtn=view.findViewById(R.id.user_email);
        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email_id);
        mobile=view.findViewById(R.id.mobile_no);
        Button updateBtn = view.findViewById(R.id.update_btn);
        address=view.findViewById(R.id.address);
        RadioGroup gdRdGroup = view.findViewById(R.id.gd_rd_btn);
        email.setEnabled(false);
        gdRdGroup.check(R.id.radioButton6);
        gd="Male";
        gdRdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioButton7:
                        gd="Female";
                        break;
                    case R.id.radioButton8:
                        gd="Others";
                        break;
                    default:
                        gd="Male";
                        break;

                }
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(name.getText().toString())){
                    Map<String,Object> data=new HashMap<>();

                    if(!TextUtils.isEmpty(name.getText().toString())){
                        data.put("name",name.getText().toString());
                    }

                    if(!TextUtils.isEmpty(mobile.getText().toString())){
                        data.put("phone_no",mobile.getText().toString());
                    }
                    if(!TextUtils.isEmpty(address.getText().toString())){
                        data.put("address",address.getText().toString());
                    }
                    if(!gd.equals("Male")){
                        data.put("gender",gd);
                    }
                   FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).update(data)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       DBqueries.fullName = name.getText().toString();
                                       DBqueries.address=address.getText().toString();
                                       DBqueries.email=email.getText().toString();
                                       Toast.makeText(getContext(), "Profile is Updated!", Toast.LENGTH_SHORT).show();
                                   }else{
                                       String error=task.getException().getMessage();
                                       Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                }else{
                    name.requestFocus();
                }
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        nameBtn.setText(DBqueries.fullName);
        emailBtn.setText(DBqueries.email);
        email.setText(DBqueries.email);
        name.setText(DBqueries.fullName);
        address.setText(DBqueries.address);
        mobile.setText(DBqueries.phoneNo);

    }

}