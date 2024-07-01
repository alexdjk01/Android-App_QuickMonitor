package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    EditText etColorTemperatureMin;
    EditText etColorPowerMin;
    EditText etColorPowerFactorMin;
    EditText etColorTensionMin;
    EditText etColorAmperageMin;
    EditText etColorTemperatureMax;
    EditText etColorPowerMax;
    EditText etColorPowerFactorMax;
    EditText etColorTensionMax;
    EditText etColorAmperageMax;

    Button btnSaveSettings;

    SharedPreferences sharedPreferencesNotifications;
    SharedPreferences sharedPreferencesColors;
    String SHARED_PREFERENCES_NAME_NOTIFICATIONS;
    private static final String SHARED_PREFERENCES_NAME_COLORS = "colors";

    Spinner spEngineNumber;
    String engineInFocus = "Engine3";

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

        etColorTemperatureMin = findViewById(R.id.etColorTempMin);
        etColorTemperatureMax = findViewById(R.id.etColorTempMax);
        etColorPowerMin = findViewById(R.id.etColorPowerMin);
        etColorPowerMax = findViewById(R.id.etColorPowerMax);
        etColorPowerFactorMin = findViewById(R.id.etColorPFMin);
        etColorPowerFactorMax = findViewById(R.id.etColorPFMax);
        etColorTensionMin = findViewById(R.id.etColorTensionMin);
        etColorTensionMax = findViewById(R.id.etColorTensionMax);
        etColorAmperageMin = findViewById(R.id.etColorAmperageMin);
        etColorAmperageMax = findViewById(R.id.etColorAmperageMax);

        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        bottomNavigation = findViewById(R.id.navigationMenuBar);

        spEngineNumber = findViewById(R.id.spinnerEngineNumber);
        //we need ArrayAdapted to populate a Spinner with the values in the string array situated in res/strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.engine_number, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spEngineNumber.setAdapter(adapter);

        // get the engine for which we change the emails alerts
        spEngineNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                 engineInFocus = adapterView.getItemAtPosition(pos).toString();
                 populateEditText();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });

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
                if(engineInFocus.equals("Engine1"))
                        SHARED_PREFERENCES_NAME_NOTIFICATIONS = "notifications" + "Engine1";
                else if(engineInFocus.equals("Engine2"))
                        SHARED_PREFERENCES_NAME_NOTIFICATIONS = "notifications" + "Engine2";
                else if(engineInFocus.equals("Engine3"))
                        SHARED_PREFERENCES_NAME_NOTIFICATIONS = "notifications" + "Engine3";
                sharedPreferencesNotifications = getSharedPreferences(SHARED_PREFERENCES_NAME_NOTIFICATIONS, MODE_PRIVATE);
                SharedPreferences.Editor editorNotifications = sharedPreferencesNotifications.edit();
                if(swTemperature.isChecked()){
                    editorNotifications.putFloat("minTemperature",Float.parseFloat(etTemperatureMin.getText().toString()));
                    editorNotifications.putFloat("maxTemperature",Float.parseFloat(etTemperatureMax.getText().toString()));
                    editorNotifications.putBoolean("onTemperature" , true);
                }
                else
                {
                    editorNotifications.putBoolean("onTemperature" , false);
                }
                if(swPower.isChecked())
                {
                    editorNotifications.putFloat("minPower",Float.parseFloat(etPowerMin.getText().toString()));
                    editorNotifications.putFloat("maxPower",Float.parseFloat(etPowerMax.getText().toString()));
                    editorNotifications.putBoolean("onPower" , true);
                }
                else
                {
                    editorNotifications.putBoolean("onPower" , false);
                }
                if(swPowerFactor.isChecked())
                {
                    editorNotifications.putFloat("minPowerFactor",Float.parseFloat(etPowerFactorMin.getText().toString()));
                    editorNotifications.putFloat("maxPowerFactor",Float.parseFloat(etPowerFactorMax.getText().toString()));
                    editorNotifications.putBoolean("onPowerFactor" , true);
                }
                else
                {
                    editorNotifications.putBoolean("onPowerFactor" , false);
                }
                if(swTension.isChecked())
                {
                    editorNotifications.putFloat("minTension",Float.parseFloat(etTensionMin.getText().toString()));
                    editorNotifications.putFloat("maxTension",Float.parseFloat(etTensionMax.getText().toString()));
                    editorNotifications.putBoolean("onTension" , true);
                }
                else
                {
                    editorNotifications.putBoolean("onTension" , false);
                }
                if(swAmperage.isChecked())
                {
                    editorNotifications.putFloat("minAmperage",Float.parseFloat(etAmperageMin.getText().toString()));
                    editorNotifications.putFloat("maxAmperage",Float.parseFloat(etAmperageMax.getText().toString()));
                    editorNotifications.putBoolean("onAmperage" , true);
                }
                else
                {
                    editorNotifications.putBoolean("onAmperage" , false);
                }
                editorNotifications.apply();
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME_NOTIFICATIONS, MODE_PRIVATE);
                Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                }
                sharedPreferencesColors = getSharedPreferences(SHARED_PREFERENCES_NAME_COLORS, MODE_PRIVATE);
                SharedPreferences.Editor editorColors = sharedPreferencesColors.edit();
                editorColors.putFloat("limitMinTemperature" , Float.parseFloat(etColorTemperatureMin.getText().toString()));
                editorColors.putFloat("limitMaxTemperature" , Float.parseFloat(etColorTemperatureMax.getText().toString()));
                editorColors.putFloat("limitMinPower" , Float.parseFloat(etColorPowerMin.getText().toString()));
                editorColors.putFloat("limitMaxPower" , Float.parseFloat(etColorPowerMax.getText().toString()));
                editorColors.putFloat("limitMinPowerFactor" , Float.parseFloat(etColorPowerFactorMin.getText().toString()));
                editorColors.putFloat("limitMaxPowerFactor" , Float.parseFloat(etColorPowerFactorMax.getText().toString()));
                editorColors.putFloat("limitMinTension" , Float.parseFloat(etColorTensionMin.getText().toString()));
                editorColors.putFloat("limitMaxTension" , Float.parseFloat(etColorTensionMax.getText().toString()));
                editorColors.putFloat("limitMinAmperage" , Float.parseFloat(etColorAmperageMin.getText().toString()));
                editorColors.putFloat("limitMaxAmperage" , Float.parseFloat(etColorAmperageMax.getText().toString()));

                editorColors.apply();

                Map<String, ?> allEntries2 = sharedPreferencesColors.getAll();

                for (Map.Entry<String, ?> entry : allEntries2.entrySet()) {
                    Log.d("map values color", entry.getKey() + ": " + entry.getValue().toString());
                }

                Intent toHome = new Intent(getApplicationContext(),HomeActivity.class);
                toHome.putExtra("keyHome",receivedUserHome);
                toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(toHome);
            }
        });

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navHome)
                {
                    Intent toHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    toHomeIntent.putExtra("keyHome",receivedUserHome);
                    toHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(toHomeIntent);
                }
                else if(item.getItemId() == R.id.navSettings)
                {
                   //do nothing
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
            Log.e("ENGINEINFOCUSSSSSSSSS", engineInFocus);
            if(engineInFocus.equals("Engine1"))
                SHARED_PREFERENCES_NAME_NOTIFICATIONS = "notifications" + "Engine1";
            else if(engineInFocus.equals("Engine2"))
                SHARED_PREFERENCES_NAME_NOTIFICATIONS = "notifications" + "Engine2";
            else if(engineInFocus.equals("Engine3"))
                SHARED_PREFERENCES_NAME_NOTIFICATIONS = "notifications" + "Engine3";

            sharedPreferencesNotifications = getSharedPreferences(SHARED_PREFERENCES_NAME_NOTIFICATIONS, MODE_PRIVATE);
            boolean onTemperature = sharedPreferencesNotifications.getBoolean("onTemperature",false);
            boolean onPower = sharedPreferencesNotifications.getBoolean("onPower",false);
            boolean onPowerFactor = sharedPreferencesNotifications.getBoolean("onPowerFactor",false);
            boolean onTension = sharedPreferencesNotifications.getBoolean("onTension",false);
            boolean onAmperage = sharedPreferencesNotifications.getBoolean("onAmperage",false);
            etTemperatureMin.setText(String.valueOf(sharedPreferencesNotifications.getFloat("minTemperature",0.0f)));
            etTemperatureMax.setText(String.valueOf(sharedPreferencesNotifications.getFloat("maxTemperature",0.0f)));
            etPowerMin.setText(String.valueOf(sharedPreferencesNotifications.getFloat("minPower",0.0f)));
            etPowerMax.setText(String.valueOf(sharedPreferencesNotifications.getFloat("maxPower",0.0f)));
            etPowerFactorMin.setText(String.valueOf(sharedPreferencesNotifications.getFloat("minPowerFactor",0.0f)));
            etPowerFactorMax.setText(String.valueOf(sharedPreferencesNotifications.getFloat("maxPowerFactor",0.0f)));
            etTensionMin.setText(String.valueOf(sharedPreferencesNotifications.getFloat("minTension",0.0f)));
            etTensionMax.setText(String.valueOf(sharedPreferencesNotifications.getFloat("maxTension",0.0f)));
            etAmperageMin.setText(String.valueOf(sharedPreferencesNotifications.getFloat("minAmperage",0.0f)));
            etAmperageMax.setText(String.valueOf(sharedPreferencesNotifications.getFloat("maxAmperage",0.0f)));

            sharedPreferencesColors = getSharedPreferences(SHARED_PREFERENCES_NAME_COLORS, MODE_PRIVATE);
            etColorTemperatureMin.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMinTemperature",0.0f)));
            etColorTemperatureMax.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMaxTemperature",31.0f)));
            etColorPowerMin.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMinPower",0.0f)));
            etColorPowerMax.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMaxPower",250.0f)));
            etColorPowerFactorMin.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMinPowerFactor",70.0f)));
            etColorPowerFactorMax.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMaxPowerFactor",100.0f)));
            etColorTensionMin.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMinTension",200.0f)));
            etColorTensionMax.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMaxTension",240.0f)));
            etColorAmperageMin.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMinAmperage",0.1f)));
            etColorAmperageMax.setText(String.valueOf(sharedPreferencesColors.getFloat("limitMaxAmperage",1.0f)));

            swTemperature.setChecked(onTemperature);
            swPower.setChecked(onPower);
            swPowerFactor.setChecked(onPowerFactor);
            swTension.setChecked(onTension);
            swAmperage.setChecked(onAmperage);
        }
        catch (Error e)
        {
            e.printStackTrace();
        }

    }
}