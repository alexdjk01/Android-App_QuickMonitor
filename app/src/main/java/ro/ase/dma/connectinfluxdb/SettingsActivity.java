package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    private User receivedUserHome;

    Switch swTemperature;
    Switch swPower;
    Switch swPowerFactor;
    Switch swTension;
    Switch swAmperage;

    EditText etTemperatureMin;
    EditText etPowerMin;
    EditText etPowerFactorMin;
    EditText etTensionMin;
    EditText etAmperageMin;
    EditText etTemperatureMax;
    EditText etPowerMax;
    EditText etPowerFactorMax;
    EditText etTensionMax;
    EditText etAmperageMax;

    Button btnSaveSettings;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCES_NAME = "notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        swTemperature = findViewById(R.id.switchTemperature);
        swPower = findViewById(R.id.switchPower);
        swPowerFactor = findViewById(R.id.switchPowerFactor);
        swTension = findViewById(R.id.switchTension);
        swAmperage = findViewById(R.id.switchAmperage);

        etTemperatureMin = findViewById(R.id.etNotifyTempMin);
        etTemperatureMax = findViewById(R.id.etNotifyTempMax);
        etPowerMin = findViewById(R.id.etNotifyPowerMin);
        etPowerMax = findViewById(R.id.etNotifyPowerMax);
        etPowerFactorMin = findViewById(R.id.etNotifyPFMin);
        etPowerFactorMax = findViewById(R.id.etNotifyPFMax);
        etTensionMin = findViewById(R.id.etNotifyTensionMin);
        etTensionMax = findViewById(R.id.etNotifyTensionMax);
        etAmperageMin = findViewById(R.id.etNotifyAmperageMin);
        etAmperageMax = findViewById(R.id.etNotifyAmperageMax);

        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        bottomNavigation = findViewById(R.id.navigationMenuBar);

        Intent receivedIntentLogged = getIntent();
        if(receivedIntentLogged!=null) {
            receivedUserHome = receivedIntentLogged.getParcelableExtra("keyHome");
            if (receivedUserHome != null) {
                Log.e("Profile activity: ", receivedUserHome.toString());
                // data was loaded correctly
            }
        }

        populateEditText();

        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if(swTemperature.isChecked()){
                    editor.putFloat("minTemperature",Float.parseFloat(etTemperatureMin.getText().toString()));
                    editor.putFloat("maxTemperature",Float.parseFloat(etTemperatureMax.getText().toString()));
                }
                if(swPower.isChecked())
                {
                    editor.putFloat("minPower",Float.parseFloat(etPowerMin.getText().toString()));
                    editor.putFloat("maxPower",Float.parseFloat(etPowerMax.getText().toString()));
                }
                if(swPowerFactor.isChecked())
                {
                    editor.putFloat("minPowerFactor",Float.parseFloat(etPowerFactorMin.getText().toString()));
                    editor.putFloat("maxPowerFactor",Float.parseFloat(etPowerFactorMax.getText().toString()));
                }
                if(swTension.isChecked())
                {
                    editor.putFloat("minTension",Float.parseFloat(etTensionMin.getText().toString()));
                    editor.putFloat("maxTension",Float.parseFloat(etTensionMax.getText().toString()));
                }
                if(swAmperage.isChecked())
                {
                    editor.putFloat("minAmperage",Float.parseFloat(etAmperageMin.getText().toString()));
                    editor.putFloat("maxAmperage",Float.parseFloat(etAmperageMax.getText().toString()));
                }

                editor.apply();

                SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);
                Map<String, ?> allEntries = sharedPreferences.getAll();

                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                }

                Intent toHome = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(toHome);
            }
        });

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navHome)
                {
                    Intent toHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(toHomeIntent);
                    Toast.makeText(getApplicationContext(),"HOME PRESSED!",Toast.LENGTH_SHORT).show();
                }
                else if(item.getItemId() == R.id.navSettings)
                {
                    Toast.makeText(getApplicationContext(),"Settings PRESSED!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent toProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    toProfileIntent.putExtra("keyHome",receivedUserHome);
                    toProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(toProfileIntent);
                }
                return true;
            }
        });
    }

    public void populateEditText(){
        //populates the editexts values with the ones that are stored in shared preferences. if none, put 0.0 for each
        try{
            sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            etTemperatureMin.setText(String.valueOf(sharedPreferences.getFloat("minTemperature",0.0f)));
            etTemperatureMax.setText(String.valueOf(sharedPreferences.getFloat("maxTemperature",0.0f)));
            etPowerMin.setText(String.valueOf(sharedPreferences.getFloat("minPower",0.0f)));
            etPowerMax.setText(String.valueOf(sharedPreferences.getFloat("maxPower",0.0f)));
            etPowerFactorMin.setText(String.valueOf(sharedPreferences.getFloat("minPowerFactor",0.0f)));
            etPowerFactorMax.setText(String.valueOf(sharedPreferences.getFloat("maxPowerFactor",0.0f)));
            etTensionMin.setText(String.valueOf(sharedPreferences.getFloat("minTension",0.0f)));
            etTensionMax.setText(String.valueOf(sharedPreferences.getFloat("maxTension",0.0f)));
            etAmperageMin.setText(String.valueOf(sharedPreferences.getFloat("minAmperage",0.0f)));
            etAmperageMax.setText(String.valueOf(sharedPreferences.getFloat("maxAmperage",0.0f)));

            if (!etTemperatureMin.getText().toString().equals("0.0") || !etTemperatureMax.getText().toString().equals("0.0"))
                swTemperature.setChecked(true);
            if (!etPowerMin.getText().toString().equals("0.0") || !etPowerMax.getText().toString().equals("0.0"))
                swPower.setChecked(true);
            if (!etPowerFactorMin.getText().toString().equals("0.0") || !etPowerFactorMax.getText().toString().equals("0.0"))
                swPowerFactor.setChecked(true);
            if (!etTensionMin.getText().toString().equals("0.0") || !etTensionMax.getText().toString().equals("0.0"))
                swTension.setChecked(true);
            if (!etAmperageMin.getText().toString().equals("0.0") || !etAmperageMin.getText().toString().equals("0.0"))
                swAmperage.setChecked(true);
        }
        catch (Error e)
        {
            e.printStackTrace();
        }

    }
}