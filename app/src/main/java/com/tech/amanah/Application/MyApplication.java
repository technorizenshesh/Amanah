package com.tech.amanah.Application;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;

import com.google.android.libraries.places.api.Places;
import com.tech.amanah.R;
import com.tech.amanah.taxiservices.Interfaces.onRefreshSchedule;

public class MyApplication extends Application {

    private onRefreshSchedule schedule;
    private CountDownTimer downTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(getApplicationContext(), getResources().getString(R.string.googlekey_other));
    }

    public MyApplication update(onRefreshSchedule schedule) {
        this.schedule = schedule;
        downTimer = new CountDownTimer(5000, 50000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("Running....");
                if (schedule != null) {
                    schedule.run();
                    System.out.println("schedule Running....");
                }
            }
            @Override
            public void onFinish() {
                downTimer.start();
            }
        };
        downTimer.start();
        return this;
    }

    public static MyApplication get() {
        return new MyApplication();
    }

    public static void showConnectionDialog(Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.please_check_internet))
                .setCancelable(false)
                .setIcon(R.drawable.ic_logo)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public static void showAlert(Context mContext,String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.ic_logo)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

}
