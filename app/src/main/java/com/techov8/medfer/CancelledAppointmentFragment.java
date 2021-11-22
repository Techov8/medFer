package com.techov8.medfer;

import android.app.Dialog;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CancelledAppointmentFragment extends Fragment {


    public CancelledAppointmentFragment() {
        // Required empty public constructor
    }
    public static  AppointmentDetailsAdapter appointmentDetailsAdapter;
    public static ConstraintLayout constraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_cancelled_appointment, container, false);
        constraintLayout=view.findViewById(R.id.no_appointments_layout_cancelled);

        Dialog loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        RecyclerView recyclerView=view.findViewById(R.id.cancell_recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        DBqueries.checkAppointments(getContext(), loadingDialog,"Cancelled");
        appointmentDetailsAdapter = new AppointmentDetailsAdapter(DBqueries.appointmentDetailsModelList);
        recyclerView.setAdapter(appointmentDetailsAdapter);

        return view;
    }


}