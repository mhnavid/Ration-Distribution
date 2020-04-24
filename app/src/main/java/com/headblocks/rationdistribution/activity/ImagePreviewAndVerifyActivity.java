package com.headblocks.rationdistribution.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import retrofit2.http.POST;

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

        final String imagePath  = getIntent().getStringExtra("imgPath");
        final String name       = getIntent().getStringExtra("name");

        if (imagePath != null){
            getImagePath(imagePath);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataConnectivity.haveNetworkConnection(getApplicationContext())){
                    sendImageToServer(imagePath, name);
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImagePreviewAndVerifyActivity.this, CaptureImageActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }



    private void sendImageToServer(String imagePath, final String name){
        progressDialog.show();
        if (!imagePath.equals("")){
            File file = new File(imagePath);
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("unknown_face", file.getName(), fileReqBody);
            Call<ResponseBody> call = apiInterface.sendFaceImage(part, name);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("response", String.valueOf(name));
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
                                String name = jsonObject.getString("user_name");
                                String userId = jsonObject.getString("user_id");
                                if (identifyStatus){
                                    String lastUpdatedDate = jsonObject.getString("last_updated_date");
                                    showFinalDialog("Matched with database.", name, lastUpdatedDate, userId);
                                } else {
                                    showFinalDialog("First visit.", name, "", userId);
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
                    Log.d("response", String.valueOf(t.getMessage()));
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

    private void showFinalDialog(String msg, String name, String date, final String userId){
        final Dialog dialog = new Dialog(ImagePreviewAndVerifyActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.final_dialog_view);

        TextView identificationMsg = dialog.findViewById(R.id.identificationMsg);
        identificationMsg.setText(msg);

        TextView nameView = dialog.findViewById(R.id.nameView);
        nameView.setText(name);

        TextView lastGivenDate = dialog.findViewById(R.id.lastGivenDate);
        lastGivenDate.setText(date);

        Button declineButton = dialog.findViewById(R.id.declineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataConnectivity.haveNetworkConnection(ImagePreviewAndVerifyActivity.this)) {
                    sendStatusToServer(userId, "no");
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button approveButton = dialog.findViewById(R.id.approveButton);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataConnectivity.haveNetworkConnection(ImagePreviewAndVerifyActivity.this)) {
                    sendStatusToServer(userId, "yes");
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    private void sendStatusToServer(String userId, String status) {
        progressDialog.show();

        Call<ResponseBody> call = apiInterface.sendStatus(userId, status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200){
                    progressDialog.dismiss();
                    try {
                        String responseMsg = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseMsg);
                        boolean error = jsonObject.getBoolean("error");
                        if (error){
                            Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(ImagePreviewAndVerifyActivity.this, NameEntryActivity.class);
                            startActivity(intent);
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

    @Override
    public void onBackPressed() {

    }
}
