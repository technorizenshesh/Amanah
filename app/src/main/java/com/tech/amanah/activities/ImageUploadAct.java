package com.tech.amanah.activities;

import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tech.amanah.R;
import com.tech.amanah.Utils.RealPathUtil;
import com.tech.amanah.databinding.ActivityImageUploadBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUploadAct extends AppCompatActivity {

    Context mContext = ImageUploadAct.this;
    ActivityImageUploadBinding binding;
    private String filePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_upload);
        itit();
    }

    private void itit() {

        binding.btUplod.setOnClickListener(v -> {
            if (checkPermissions(mContext)) {
                showPictureDialog();
            } else {
                requestPermissions(mContext);
            }
        });

    }

    public void showPictureDialog() {
        androidx.appcompat.app.AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null)
            startActivityForResult(cameraIntent, 0);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    try {

                        if (data != null) {

                            Bundle extras = data.getExtras();
                            Bitmap bitmapNew = (Bitmap) extras.get("data");
                            Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());

                            Glide.with(mContext).load(imageBitmap).into(binding.ivImg);
                            Uri tempUri = getImageUri(mContext, imageBitmap);

                            String image = RealPathUtil.getRealPath(mContext, tempUri);
                            filePaths = image;

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();

                        Bitmap bitmapNew = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                        Bitmap bitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                        Glide.with(mContext).load(selectedImage).into(binding.ivImg);
                        Uri tempUri = getImageUri(mContext, bitmap);
                        String image = RealPathUtil.getRealPath(mContext, tempUri);
                        filePaths = image;

                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    public static void requestPermissions(Context mContext) {
        ActivityCompat.requestPermissions(
                ((Activity) mContext), new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 101);
    }

    public static boolean checkPermissions(Context mContext) {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }

}