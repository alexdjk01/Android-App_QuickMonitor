package ro.ase.dma.connectinfluxdb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {

    private CardView cardEngine;
    private CardView cardProfile;
    private CardView cardSettings;
    private CardView cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        cardEngine = findViewById(R.id.cardEngine);
        cardProfile = findViewById(R.id.cardProfile);
        cardSettings = findViewById(R.id.cardSettings);
        cardLogout = findViewById(R.id.cardLogout);

        Intent receivedIntentLogged = getIntent();
        if(receivedIntentLogged!=null)
        {
            User receivedUserLogged = receivedIntentLogged.getParcelableExtra("keyLogin");
            if(receivedUserLogged!=null)
            {
                Log.e("DASHBOARD: ",receivedUserLogged.toString());
                // data was loaded correctly
            }

            // TO-DO -> dashboard

            cardLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // goes back to the Login Activity and finises this activity.
                    Intent intent = new Intent(getApplicationContext(),StartAppActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // sets the activity intent to the new intent
        //function for getting the intent value
//        handleIntent(intent);
    }

//    private void handleIntent(Intent intent) {
//        // Assuming "YourDataType" is a String for simplicity; adjust as necessary
//        User newUser = intent.getParcelableExtra("keyRegister");
//        if(newUser != null) {
//            etEmail.setText(newUser.email.toString());
//            etPassword.setText(newUser.password.toString());
//        }
//    }
}