package com.headblocks.rationdistribution.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.headblocks.rationdistribution.DataConnectivity;
import com.headblocks.rationdistribution.R;
import com.headblocks.rationdistribution.api.ApiClient;
import com.headblocks.rationdistribution.api.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImagePreviewAndVerifyActivity extends AppCompatActivity {

    ImageView imageView;
    Button sendButton, cancelButton;
    ProgressDialog progressDialog;

    private static ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview_and_verify);

        apiInterface= ApiClient.getApiClient().create(ApiInterface.class);

        imageView       = findViewById(R.id.imagePreview);
        sendButton      = findViewById(R.id.sendButton);
        cancelButton    = findViewById(R.id.cancelButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        final String imagePath = getIntent().getStringExtra("imgPath");
        if (imagePath != null){
            getImagePath(imagePath);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataConnectivity.haveNetworkConnection(getApplicationContext())){
                    sendImageToServer(imagePath);
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImagePreviewAndVerifyActivity.this, CaptureImageActivity.class);
                startActivity(intent);
            }
        });
    }



    private void sendImageToServer(String imagePath){
        progressDialog.show();
        if (!imagePath.equals("")){
            File file = new File(imagePath);
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("unknown_face", file.getName(), fileReqBody);
            Call<ResponseBody> call = apiInterface.sendFaceImage(part);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("response", String.valueOf(response));
                    if (response.code() == 200){
                        progressDialog.dismiss();
                        try {
                            String responseMsg = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseMsg);
                            boolean error = jsonObject.getBoolean("error");
                            if (error){
                                Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_LONG).show();
                            } else {
                                boolean identifyStatus = jsonObject.getBoolean("identify_status");
                                if (identifyStatus){
                                    showSuccessDialog();
                                } else {
                                    showFailedDialog();
                                }
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getImagePath(String path) {
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        if (myBitmap.getHeight() > myBitmap.getWidth()){
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        imageView.setImageBitmap(myBitmap);
    }

    private void showSuccessDialog(){
        final Dialog dialog = new Dialog(ImagePreviewAndVerifyActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.final_dialog_view);

        LinearLayout linearLayout = dialog.findViewById(R.id.successOrFailedLayout);
        linearLayout.setBackgroundColor(Color.parseColor("#00c853"));

        TextView successText = dialog.findViewById(R.id.successorFailedTextView);
        successText.setText("Success");

        TextView textView = dialog.findViewById(R.id.dialogMessage);
        textView.setText("Face successfully matched with database.");

        Button button = dialog.findViewById(R.id.btnDialogOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImagePreviewAndVerifyActivity.this, CaptureImageActivity.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void showFailedDialog(){
        final Dialog dialog = new Dialog(ImagePreviewAndVerifyActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.final_dialog_view);

        LinearLayout linearLayout = dialog.findViewById(R.id.successOrFailedLayout);
        linearLayout.setBackgroundColor(Color.parseColor("#d50000"));

        TextView successText = dialog.findViewById(R.id.successorFailedTextView);
        successText.setTextColor(Color.parseColor("#eeeeee"));
        successText.setText("Failed");

        TextView textView = dialog.findViewById(R.id.dialogMessage);
        textView.setText("Face did not match with database.");

        Button button = dialog.findViewById(R.id.btnDialogOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImagePreviewAndVerifyActivity.this, CaptureImageActivity.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {

    }
}
