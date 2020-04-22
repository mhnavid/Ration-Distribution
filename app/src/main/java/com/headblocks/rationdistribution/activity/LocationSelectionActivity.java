package com.headblocks.rationdistribution.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.headblocks.rationdistribution.R;

import java.util.ArrayList;
import java.util.List;

public class LocationSelectionActivity extends AppCompatActivity {

    Spinner districtSelectionSpinner, upazilaSelectionSpinner, wardSelectionSpinner;
    ArrayAdapter<String> districtAdapter, upazilaAdapter, wardAdapter;

    List<String> districtList, upazilaList, wardList = null;

    Button locationSelectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);

        districtSelectionSpinner = findViewById(R.id.districtSelectionSpinner);
        upazilaSelectionSpinner  = findViewById(R.id.upazilaSelectionSpinner);
        wardSelectionSpinner     = findViewById(R.id.wardSelectionSpinner);
        locationSelectionButton  = findViewById(R.id.locationSelectionButton);

        getDistrictList();

        districtSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getUpazilaList((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getUpazilaList((String) parent.getItemAtPosition(0));
            }
        });

        upazilaSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getWardList((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getWardList((String) parent.getItemAtPosition(0));
            }
        });

        wardSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationSelectionActivity.this, CaptureImageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getDistrictList(){
        districtList = new ArrayList<>();
        districtList.add("Dhaka");
        districtAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, districtList);
        districtSelectionSpinner.setAdapter(districtAdapter);
    }

    private void getUpazilaList(String district){
        Log.d("input", district);
        upazilaList = new ArrayList<>();
        if (district.equals("Dhaka")){
            upazilaList.add("Dhaka South City Corporation");
            upazilaList.add("Dhaka North City Corporation");
        }
        upazilaAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, upazilaList);
        upazilaSelectionSpinner.setAdapter(upazilaAdapter);
        upazilaAdapter.notifyDataSetChanged();
    }

    private void getWardList(String upazila){
        wardList = new ArrayList<>();
        if (upazila.equals("Dhaka South City Corporation")){
            wardList.add("Ward 1");
            wardList.add("Ward 2");
            wardList.add("Ward 3");
            wardList.add("Ward 4");
        }
        if (upazila.equals("Dhaka North City Corporation")){
            wardList.add("Ward 5");
            wardList.add("Ward 6");
            wardList.add("Ward 7");
            wardList.add("Ward 8");
        }
        wardAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.support_simple_spinner_dropdown_item, wardList);
        wardSelectionSpinner.setAdapter(wardAdapter);
        wardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

    }
}
