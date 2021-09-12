package com.tech.amanah.taxiservices.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.tech.amanah.R;
import com.tech.amanah.databinding.ActivityRideDetailBinding;
import com.tech.amanah.fragments.RaiseIssueBottomSheet;

public class RideDetailActivity extends AppCompatActivity {

    ActivityRideDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_detail);

        binding.GoToDriver.setOnClickListener(v ->

                {
                    // startActivity(new Intent(this, DriverDetailActivity.class));
                }
                );

        binding.btnRaiseIssue.setOnClickListener(v -> {
                    RaiseIssueBottomSheet bottomSheetFragment= new RaiseIssueBottomSheet();
                    Bundle bundle = new Bundle();
                    bundle.putString("link","Hello Friends");
                    bottomSheetFragment.setArguments(bundle);
                    bottomSheetFragment.show(getSupportFragmentManager(),"ModalBottomSheet");
                });

    }
}