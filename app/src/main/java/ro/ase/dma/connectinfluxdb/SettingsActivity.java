package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swTemperature.isChecked()){

                }
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
}