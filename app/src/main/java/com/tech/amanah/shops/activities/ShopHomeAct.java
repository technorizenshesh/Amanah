package com.tech.amanah.shops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.Compress;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.LoginActivity;
import com.tech.amanah.databinding.ActivityShopHomeBinding;
import com.tech.amanah.databinding.AddItemsDialogBinding;
import com.tech.amanah.shops.adapters.AdapterShopItems;
import com.tech.amanah.shops.models.ModelShopItems;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopHomeAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    Context mContext = ShopHomeAct.this;
    ActivityShopHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    Dialog mDialog;
    AddItemsDialogBinding dialogBinding;
    File mFile;
    private final int GALLERY=0,CAMERA=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        Log.e("shopiIDIDID", "Shop Id = " + modelLogin.getResult().getId());
        itit();
    }

    private void itit() {

        getShopItemsApiCall();

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        binding.ivAddItems.setOnClickListener(v -> {
            openAddItemDialog();
        });

        binding.ivMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnChangePass.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnOrders.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext,ShopOrdersAct.class));
        });

        binding.childNavDrawer.btnMyProducts.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

    }

    private void openAddItemDialog() {
        mFile = null;
        mDialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.setCancelable(true);

        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.add_items_dialog, null, false);
        mDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivItemImg.setOnClickListener(v -> {
            if (checkPermissions()) {
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etItemName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etItemPrice.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etDescription.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (mFile == null) {
                Toast.makeText(mContext, getString(R.string.please_add_item_img), Toast.LENGTH_SHORT).show();
            } else {
                addItemApiCall(mDialog,
                        dialogBinding.etItemName.getText().toString().trim(),
                        dialogBinding.etItemPrice.getText().toString().trim(),
                        dialogBinding.etDescription.getText().toString().trim()
                );
            }
        });

        mDialog.show();

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, GALLERY);
                                break;
                            case 1:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, CAMERA);
                                break;
                        }
                    }
                });

        pictureDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
                        String path = getRealPathFromURI(getImageUri(mContext,bitmap));
                        mFile = new File(path);
                        dialogBinding.ivItemImg.setImageURI(Uri.parse(path));
    //                        Compress.get(mContext)
//                                .setQuality(40)
//                                .execute(new Compress.onSuccessListener() {
//                            @Override
//                            public void response(boolean status, String message, File file) {
//                                mFile = file;
//                                dialogBinding.ivItemImg.setImageURI(Uri.parse(file.getPath()));
//                            }
//                        }).CompressedImage(path);
                    } catch (Exception e) {
                        Log.e("hjagksads", "image = " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                String path = getRealPathFromURI(getImageUri(mContext, thumbnail));
                mFile = new File(path);
                dialogBinding.ivItemImg.setImageURI(Uri.parse(path));
                //                Compress.get(mContext).setQuality(100)
//                        .execute(new Compress.onSuccessListener() {
//                            @Override
//                            public void response(boolean status, String message, File file) {
//                                mFile = file;
//                            }
//                        }).CompressedImage(path);
            }
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void getShopItemsApiCall() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllShopItems(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("getShopItemsApiCall", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        ModelShopItems modelShopItems = new Gson().fromJson(responseString, ModelShopItems.class);

                        AdapterShopItems adapterShopItems = new AdapterShopItems(mContext, modelShopItems.getResult());
                        binding.rvMyProducts.setAdapter(adapterShopItems);

                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }
        });

    }

    private void addItemApiCall(Dialog dialog, String name, String price, String description) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        MultipartBody.Part itemImgfilePart;

        itemImgfilePart = MultipartBody.Part.createFormData("item_image", mFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), mFile));

        RequestBody shop_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody item_name = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody item_price = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody item_description = RequestBody.create(MediaType.parse("text/plain"), description);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addShopsItems(shop_id, item_name, item_price, item_description, itemImgfilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("addItemApiCall", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        mFile = null;
                        getShopItemsApiCall();
                        dialog.dismiss();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });

    }

    private void logoutAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, LoginActivity.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ProjectUtil.clearNortifications(mContext);
                        startActivity(loginscreen);
                        finishAffinity();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                PERMISSION_ID
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}