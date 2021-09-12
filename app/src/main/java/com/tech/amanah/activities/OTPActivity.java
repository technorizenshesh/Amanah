package com.tech.amanah.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.tech.amanah.R;
import com.tech.amanah.databinding.ActivityOTPBinding;


public class OTPActivity extends AppCompatActivity {

    ActivityOTPBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p);

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnSignup.setOnClickListener(v ->

                {
                    startActivity(new Intent(this,LoginActivity.class));
                }
                );

    }
}