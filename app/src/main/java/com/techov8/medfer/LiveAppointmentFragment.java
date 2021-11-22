package com.techov8.medfer;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Date;

public class LiveAppointmentFragment extends Fragment {


    public LiveAppointmentFragment() {
        // Required empty public constructor
    }

    public static  AppointmentDetailsAdapter appointmentDetailsAdapter;
    public static  ConstraintLayout constraintLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_live_appointment, container, false);
        constraintLayout=view.findViewById(R.id.no_appointments_layout_live);
        final Dialog loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        final RecyclerView  recyclerView=view.findViewById(R.id.live_appointment_recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Calendar c=Calendar.getInstance();
        Date d=c.getTime();
        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("APPOINTMENT_DATA").collection("Live").whereLessThan("appointment_full_date",d).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if(documentSnapshot.getString("time_status").equals("Pending")){
                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USERS_DATA").document("APPOINTMENT_DATA").collection("Live").document(documentSnapshot.getId())
                                        .update("time_status","Expired").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }
                        loadingDialog.dismiss();
                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
        DBqueries.checkAppointments(getContext(), loadingDialog,"Live");
        appointmentDetailsAdapter = new AppointmentDetailsAdapter(DBqueries.appointmentDetailsModelList);
        recyclerView.setAdapter(appointmentDetailsAdapter);
        appointmentDetailsAdapter.notifyDataSetChanged();

        return view;
    }


}