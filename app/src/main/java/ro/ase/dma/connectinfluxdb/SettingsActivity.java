package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigation = findViewById(R.id.navigationMenuBar);

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
                    startActivity(toProfileIntent);
                    Toast.makeText(getApplicationContext(),"Profile PRESSED!",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}