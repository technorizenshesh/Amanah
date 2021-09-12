package com.tech.amanah.taxiservices.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tech.amanah.R;
import com.tech.amanah.adapters.RideHistoryAdapter;
import com.tech.amanah.databinding.ActivityRideHistoryBinding;

public class RideHistoryActivity extends AppCompatActivity {

    ActivityRideHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_history);

        binding.ivBack.setOnClickListener(v ->
                {
                    finish();
                }
                );

        binding.rvRideHistory.setHasFixedSize(true);
        binding.rvRideHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRideHistory.setAdapter(new RideHistoryAdapter(this));

    }
}