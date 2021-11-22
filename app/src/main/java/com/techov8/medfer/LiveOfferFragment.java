package com.techov8.medfer;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LiveOfferFragment extends Fragment {


    public LiveOfferFragment() {
        // Required empty public constructor
    }

  private Dialog loadingDialog;
    public static UserBillAdapter userBillAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_live_offer, container, false);



        RecyclerView recyclerView=view.findViewById(R.id.offer_recycler_view);

        /////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        /////loading dialog


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);



        if (DBqueries.liveOfferModelList.size() == 0) {
            loadingDialog.show();
            DBqueries.LoadLiveOffers(loadingDialog);
        }

        userBillAdapter=new UserBillAdapter(DBqueries.liveOfferModelList,false,true);
        recyclerView.setAdapter(userBillAdapter);
        userBillAdapter.notifyDataSetChanged();




        return view;
    }
}