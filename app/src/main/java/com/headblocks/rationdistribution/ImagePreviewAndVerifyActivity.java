package com.headblocks.rationdistribution;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImagePreviewAndVerifyActivity extends AppCompatActivity {

    ImageView imageView;
    Button sendButton, cancelButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview_and_verify);

        imageView       = findViewById(R.id.imagePreview);
        sendButton      = findViewById(R.id.sendButton);
        cancelButton    = findViewById(R.id.cancelButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        String imagePath = getIntent().getStringExtra("imgPath");
        if (imagePath != null){
            getImagePath(imagePath);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFailedDialog();
                sendImageToServer();
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



    private void sendImageToServer(){
        progressDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void getImagePath(String path) {
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        if (myBitmap.getHeight() > myBitmap.getWidth()){
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
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
