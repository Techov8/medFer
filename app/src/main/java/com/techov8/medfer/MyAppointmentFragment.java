package com.techov8.medfer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

public class MyAppointmentFragment extends Fragment {

    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private LiveAppointmentFragment liveAppointmentFragment;
    private PastAppointmentFragment pastAppointmentFragment;
    private CancelledAppointmentFragment cancelledAppointmentFragment;

    public MyAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_appointment, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        frameLayout = view.findViewById(R.id.frame_layout);
        liveAppointmentFragment=new LiveAppointmentFragment();
        pastAppointmentFragment=new PastAppointmentFragment();
        cancelledAppointmentFragment=new CancelledAppointmentFragment();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    setFragment(liveAppointmentFragment);
                }
                if(tab.getPosition() == 1){
                    setFragment(pastAppointmentFragment);
                }
                if(tab.getPosition() == 2){
                    setFragment(cancelledAppointmentFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).select();
        setFragment(liveAppointmentFragment);

        return view;
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}