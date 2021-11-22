package com.techov8.medfer;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


public class UserBillsFragment extends Fragment {

    public  UserBillsFragment() {
        // Required empty public constructor
    }
    private Dialog loadingDialog;
    public static UserBillAdapter userBillAdapter;
    private ImageButton closeBtn;
    public static ConstraintLayout constraintLayout;
    public static RecyclerView billImageRecyclerView;
    public static ImageView billImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_bills, container, false);
        ConstraintLayout linearLayout=view.findViewById(R.id.no_bills_to_show_layout);
        closeBtn=view.findViewById(R.id.image_close_btn);
        billImage=view.findViewById(R.id.big_bill_image);

        constraintLayout=view.findViewById(R.id.show_image_layout);
        /////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        /////loading dialog


        billImageRecyclerView=view.findViewById(R.id.user_bills_recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        billImageRecyclerView.setLayoutManager(linearLayoutManager);


        if(UploadBillsActivity.isUpload){
            DBqueries.userBillModelList.clear();
            UploadBillsActivity.isUpload=false;
        }
        SharedPreferences prefs = getActivity().getSharedPreferences("uploadData",
                0);
        String string = prefs.getString("data_upload",
                "");

        if(string.equals("yes")) {
            linearLayout.setVisibility(View.GONE);
            if (DBqueries.userBillModelList.size() == 0) {
                loadingDialog.show();
                DBqueries.loadUserBill(getContext(), loadingDialog);
            }
        }else{
            linearLayout.setVisibility(View.VISIBLE);
        }
        userBillAdapter=new UserBillAdapter(DBqueries.userBillModelList,false,false);
        billImageRecyclerView.setAdapter(userBillAdapter);
        userBillAdapter.notifyDataSetChanged();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.toolbar.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.GONE);
                billImageRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }


}