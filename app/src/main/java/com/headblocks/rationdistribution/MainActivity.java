package com.headblocks.rationdistribution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputEditText usernameText, passwordText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameText = findViewById(R.id.usernameEditText);
        passwordText = findViewById(R.id.passwordEditText);
        loginButton  = findViewById(R.id.loginButton);

        usernameText.setText("Test");
        passwordText.setText("Demo");

        if (!getSavedName().equals("")){
            Intent intent = new Intent(MainActivity.this, CaptureImageActivity.class);
            startActivity(intent);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameText.getText().toString().equals("Test") &&
                        passwordText.getText().toString().equals("Demo")){
                    saveName(usernameText.getText().toString().trim());
                    Intent intent = new Intent(MainActivity.this, LocationSelectionActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please check input field.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void saveName(String name){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NameOfShared", name);
        editor.apply();
    }

    String getSavedName(){
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return mSharedPreference.getString("NameOfShared", "");
    }
}
