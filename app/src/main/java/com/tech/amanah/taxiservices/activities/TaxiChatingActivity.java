package com.tech.amanah.taxiservices.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.databinding.ActivityTaxiChatingBinding;
import com.tech.amanah.taxiservices.Interfaces.BottomReachedInterface;
import com.tech.amanah.taxiservices.adapters.AdapterChating;
import com.tech.amanah.taxiservices.models.ModelChating;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxiChatingActivity extends AppCompatActivity implements BottomReachedInterface {

    ActivityTaxiChatingBinding binding;
    String senderId = "",receiverId = "",receiverName = "",requestId="";
    Timer timer = new Timer();
    boolean isBottom;
    Context mContext = TaxiChatingActivity.this;
    ModelChating modelAllchats;
    String timezoneID = TimeZone.getDefault().getID();
    AdapterChating adapterChating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_taxi_chating);

//      senderId = getIntent().getStringExtra("sender_id");
//      receiverId = getIntent().getStringExtra("receiver_id");
//      receiverName = getIntent().getStringExtra("name");
//      requestId = getIntent().getStringExtra("request_id");

        init();

    }

    private void init() {

//      AdapterChating adapterChating = new AdapterChating(mContext,null);
//      binding.rvChating.setAdapter(adapterChating);
//
//      getAllMessages();

        binding.tvName.setText(receiverName);

        binding.ivSendMsg.setOnClickListener(v -> {
            if(!binding.etSendMsg.getText().toString().trim().isEmpty()) {
                Animation anim = new AlphaAnimation(0.0f,1.0f);
                anim.setDuration(50);
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                binding.ivSendMsg.startAnimation(anim);
                sendMessageApi(binding.etSendMsg.getText().toString().trim());
                binding.etSendMsg.setText("");
            } else {
                Toast.makeText(mContext, getString(R.string.please_write_a_message), Toast.LENGTH_SHORT).show();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
            if(timer != null) timer.cancel();
        });

    }

    private void sendMessageApi(String msg) {

//        TaximobilityInterface api = ApiClient.getClient().create(TaximobilityInterface.class);
//
//        HashMap<String,String> param = new HashMap<>();
//        param.put("sender_id",senderId);
//        param.put("receiver_id",receiverId);
//        param.put("request_id",requestId);
//        param.put("chat_message",msg);
//
//        Call<ResponseBody> call = api.insertChatBookingCall(param);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Toast.makeText(TaxiChatingActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                getAllMessages();
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//
//        });

    }

    private void getAllMessages() {

//        TaximobilityInterface api = ApiClient.getClient().create(TaximobilityInterface.class);
//
//        HashMap<String,String> param = new HashMap<>();
//        param.put("sender_id",senderId);
//        param.put("receiver_id",receiverId);
//        param.put("request_id",requestId);
//
//        Call<ResponseBody> call = api.getChatBookingCall(param);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                // ProjectUtil.pauseProgressDialog();
//
//                try {
//
//                    String stringResponse = response.body().string();
//
//                    JSONObject jsonObject = new JSONObject(stringResponse);
//
//                    Log.e("stsdfdsfdsfs","stringResponse = " + stringResponse);
//                    Log.e("stsdfdsfdsfs","response = " + response);
//
//                    if (jsonObject.getString("status").equals("1")) {
//
//                        modelAllchats = new Gson().fromJson(stringResponse, ModelChating.class);
//
//                        try {
//                            binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);
//                        } catch (Exception e) {}
//
//                        adapterChating = new AdapterChating(mContext, modelAllchats.getResult());
//                        binding.rvChating.setLayoutManager(new LinearLayoutManager(mContext));
//                        binding.rvChating.setAdapter(adapterChating);
//
//                        binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // ProjectUtil.pauseProgressDialog();
//            }
//
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();

//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//                if(isLastVisible()) {
//                    getAllMessages2();
//                }
//
//            }
//        }, 0, 5000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer != null) timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timer != null) timer.cancel();
    }

    private void getAllMessages2() {

//        TaximobilityInterface api = ApiClient.getClient().create(TaximobilityInterface.class);
//
//        HashMap<String,String> param = new HashMap<>();
//        param.put("sender_id",senderId);
//        param.put("receiver_id",receiverId);
//        param.put("request_id",requestId);
//
//        Call<ResponseBody> call = api.getChatBookingCall(param);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                // ProjectUtil.pauseProgressDialog();
//
//                try {
//
//                    String stringResponse = response.body().string();
//
//                    JSONObject jsonObject = new JSONObject(stringResponse);
//
//                    Log.e("stringResponse","stringResponse = " + stringResponse);
//                    Log.e("stringResponse","response = " + response);
//
//                    if (jsonObject.getString("status").equals("1")) {
//
//                        modelAllchats = new Gson().fromJson(stringResponse, ModelChating.class);
//
//                        try {
//                            binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);
//                        } catch (Exception e) {
//
//                        }
//
//                        adapterChating = new AdapterChating(mContext, modelAllchats.getResult());
//                        binding.rvChating.setLayoutManager(new LinearLayoutManager(mContext));
//                        binding.rvChating.setAdapter(adapterChating);
//
//                        binding.rvChating.scrollToPosition(modelAllchats.getResult().size() - 1);
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // ProjectUtil.pauseProgressDialog();
//            }
//
//        });

    }

    private boolean isLastVisible() {

//        if (modelAllchats != null) {
//            LinearLayoutManager layoutManager = ((LinearLayoutManager) binding.rvChating.getLayoutManager());
//            int pos = layoutManager.findLastCompletelyVisibleItemPosition();
//            int numItems = binding.rvChating.getAdapter().getItemCount();
//            return (pos >= numItems - 1);
//        }
//
          return false;

    }

    @Override
    public void isBottomReached(boolean isBottomreached) {
        isBottom = isBottomreached;
    }

}