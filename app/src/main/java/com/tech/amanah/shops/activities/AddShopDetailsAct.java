package com.tech.amanah.shops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.Compress;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.RealPathUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.LoginActivity;
import com.tech.amanah.activities.PinLocationActivity;
import com.tech.amanah.activities.SelectService;
import com.tech.amanah.databinding.ActivityAddShopDetailsBinding;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.Tools;
import com.tech.amanah.utility.onDateSetListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddShopDetailsAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 102;
    private final int GALLERY = 0, CAMERA = 1;
    Context mContext = AddShopDetailsAct.this;
    ActivityAddShopDetailsBinding binding;
    File idCardFile, businessFile, frontFile;
    int imageCapturedCode;
    private LatLng latLng;
    ArrayList<String> typeIds = new ArrayList<>();
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String str_image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop_details);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        typeIds.add("1");
        typeIds.add("2");
        typeIds.add("3");
        typeIds.add("4");
        typeIds.add("5");
        typeIds.add("6");
        typeIds.add("7");
        typeIds.add("8");

        itit();

    }

    private void setImageFromCameraGallery(File file) {
        if (imageCapturedCode == 0) {
            businessFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    binding.ivbusinessLicenseImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
            Log.e("filefilefile", "After file Size = " + file.length() / 1024);
        } else if (imageCapturedCode == 1) {
            idCardFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    binding.ividCardImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 2) {
            frontFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    binding.ivShopFrontImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        }
    }

    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, GALLERY);
                                break;
                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null)
                                    startActivityForResult(cameraIntent, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

//    private void showPictureDialog() {
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
//        pictureDialog.setTitle("Select Action");
//        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                startActivityForResult(galleryIntent, GALLERY);
//                                break;
//                            case 1:
//                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(intent, CAMERA);
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//    }

    private void itit() {

        binding.address.setOnClickListener(v -> {
            startActivityForResult(new Intent(mContext, PinLocationActivity.class), 222);
//           List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
//           Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                  .build(this);
//           startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.ivbusinessLicenseImg.setOnClickListener(v -> {
            if (checkPermissions()) {
                imageCapturedCode = 0;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        binding.ividCardImg.setOnClickListener(v -> {
            if (checkPermissions()) {
                imageCapturedCode = 1;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        binding.ivShopFrontImg.setOnClickListener(v -> {
            if (checkPermissions()) {
                imageCapturedCode = 2;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        binding.etCloseTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String AM_PM;
                    if (selectedHour >= 0 && selectedHour < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                    }
                    binding.etCloseTime.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                }
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        binding.etOpenTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String AM_PM;
                    if (selectedHour >= 0 && selectedHour < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                    }
                    binding.etOpenTime.setText(selectedHour + ":" + selectedMinute + " " + AM_PM);
                }
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        binding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etShopName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etOpenTime.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etCloseTime.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.address.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.landAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etDescription.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.all_fields_man), Toast.LENGTH_SHORT).show();
            } else if (frontFile == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_shop_front_copy), Toast.LENGTH_SHORT).show();
            } else if (businessFile == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_busi_license_copy), Toast.LENGTH_SHORT).show();
            } else if (idCardFile == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_id_card_copy), Toast.LENGTH_SHORT).show();
            } else {
                addShopApiCall();
            }
        });

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void addShopApiCall() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part idCardfilePart;
        MultipartBody.Part businessfilePart;
        MultipartBody.Part frontfilePart;

        idCardfilePart = MultipartBody.Part.createFormData("id_card_image", idCardFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), idCardFile));
        businessfilePart = MultipartBody.Part.createFormData("business_license_image", businessFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), businessFile));
        frontfilePart = MultipartBody.Part.createFormData("shop_front_image", frontFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), frontFile));

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody shop_name = RequestBody.create(MediaType.parse("text/plain"), binding.etShopName.getText().toString().trim());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), binding.etDescription.getText().toString().trim());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), binding.address.getText().toString().trim() +
                " " + binding.landAddress.getText().toString());
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.latitude));
        RequestBody lon = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.longitude));
        RequestBody open_time = RequestBody.create(MediaType.parse("text/plain"), binding.etOpenTime.getText().toString());
        RequestBody close_time = RequestBody.create(MediaType.parse("text/plain"), binding.etCloseTime.getText().toString());
        RequestBody shopTypeId = RequestBody.create(MediaType.parse("text/plain"), typeIds.get(binding.spShopType.getSelectedItemPosition()));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addShops(user_id, shop_name, description, address, shopTypeId
                , lat, lon, open_time, close_time, idCardfilePart, businessfilePart, frontfilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        modelLogin.getResult().setShop_status("1");
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                        startActivity(new Intent(mContext, LoginActivity.class));
                        // startActivity(new Intent(mContext, ShopHomeAct.class));
                        finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

        } else if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = RealPathUtil.getRealPath(mContext, data.getData());
                setImageFromCameraGallery(new File(path));
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                try {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        Bitmap bitmapNew = (Bitmap) extras.get("data");
                        Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());

                        Uri tempUri = getImageUri(mContext, imageBitmap);

                        String image = RealPathUtil.getRealPath(mContext, tempUri);
                        setImageFromCameraGallery(new File(image));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == 222) {
            String add = data.getStringExtra("add");
            Log.e("sfasfdas", "fdasfdas = 222 = " + add);
            Log.e("sfasfdas", "fdasfdas = lat = " + data.getDoubleExtra("lat", 0.0));
            Log.e("sfasfdas", "fdasfdas = lon = " + data.getDoubleExtra("lon", 0.0));
            double lat = data.getDoubleExtra("lat", 0.0);
            double lon = data.getDoubleExtra("lon", 0.0);
            latLng = new LatLng(lat, lon);
            binding.address.setText(add);
        }

    }

    public Bitmap BITMAP_RE_SIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
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

}


