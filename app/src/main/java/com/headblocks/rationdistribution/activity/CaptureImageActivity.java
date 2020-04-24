package com.headblocks.rationdistribution.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.headblocks.rationdistribution.R;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

public class CaptureImageActivity extends AppCompatActivity {

    CameraView cameraView;
    ImageButton captureImageButton, cameraFacingButton;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        cameraView          = findViewById(R.id.camera);
        captureImageButton  = findViewById(R.id.btnCaptureImage);
        cameraFacingButton  = findViewById(R.id.cameraFacingButton);
        cameraView.setLifecycleOwner(this);

        name = getIntent().getStringExtra("name");

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                byte[] data = result.getData();
                Bitmap bmp = byteArrayToBitmap(data);
                Matrix matrix = new Matrix();
                matrix.setRotate(result.getRotation());
                Bitmap bOutput = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                File path = saveBitmap(bOutput);
                Intent intent = new Intent(CaptureImageActivity.this, ImagePreviewAndVerifyActivity.class);
                intent.putExtra("imgPath", path.toString());
                intent.putExtra("name", name);
                startActivity(intent);
            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        cameraFacingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.getFacing() == Facing.BACK){
                    cameraView.setFacing(Facing.FRONT);
                } else {
                    cameraView.setFacing(Facing.BACK);
                }
            }
        });

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePictureSnapshot();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.open();
    }

    public Bitmap byteArrayToBitmap(byte[] byteArray)
    {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        return BitmapFactory.decodeStream(arrayInputStream);
    }

    public File saveBitmap(Bitmap bmp) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("rationDistributionImage", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File mypath = new File(directory, "tempImage.jpg");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mypath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        return  mypath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NameOfShared", "");
        editor.apply();

        Intent intent = new Intent(CaptureImageActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}
