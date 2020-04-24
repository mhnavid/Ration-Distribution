package com.headblocks.rationdistribution.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.headblocks.rationdistribution.R;

public class NameEntryActivity extends AppCompatActivity {

    TextInputEditText nameEditText;
    TextInputLayout enterNameEditText;
    Button proceedButton, nameSkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_entry);

        nameEditText = findViewById(R.id.nameEditText);
        proceedButton = findViewById(R.id.proceedButton);
        enterNameEditText = findViewById(R.id.enterNameTextField);
        nameSkipButton = findViewById(R.id.nameSkipButton);

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEditText.getText().toString().equals("")){
                    enterNameEditText.setError("Please enter name.");
                } else {
                    Intent intent = new Intent(NameEntryActivity.this, CaptureImageActivity.class);
                    intent.putExtra("name", nameEditText.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });

        nameSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NameEntryActivity.this, CaptureImageActivity.class);
                intent.putExtra("name", "");
                startActivity(intent);
            }
        });
    }
}
