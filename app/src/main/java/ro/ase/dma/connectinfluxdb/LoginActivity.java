package ro.ase.dma.connectinfluxdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEmail;
    private EditText etURL;
    private TextView tvSignUp;
    private UserRoomDataBase userDatabase;
    private UserDao userDao;
    private TextView tvAlertEmail;
    private TextView tvAlertURL;

    public SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);


        // Update the security provider to protect against SSL exploits
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is outdated or unavailable.
            // Prompt the user to install/update Google Play Services.
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily resolvable.
        }


        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etURL = findViewById(R.id.etURL);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvAlertEmail = findViewById(R.id.tvAlertEmail);
        tvAlertURL = findViewById(R.id.tvAlertURL);

        userDatabase = UserRoomDataBase.getInstance(this);
        userDao = userDatabase.getUserDao();


        // test if any intent is received and place the user attributes into the editText
        Intent intent = getIntent();
        if(intent != null)
        {
            handleIntent(getIntent());
        }


        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String URL = etURL.getText().toString();
                if(verifyCredentials(email,URL))   // if the email and URL are written properly
                {// save the email and URL in a shared preference resource in order to dynamic change the IP and receiver email
                    sharedPreferences = getSharedPreferences("loginData",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.putString("email",email);
                    editor.putString("URL",URL);
                    editor.commit();
                    if(userDao.getUserByEmail(email) != null)   // if the user exists in the database
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //get user from the db (the email is unique ) in a new thread not to block main th
                                User loggedUser = userDao.getUserByEmail(email);
                                if(loggedUser!= null)
                                {
                                    loggedUser.setURL(URL);
                                    userDao.update(loggedUser);
                                    runOnUiThread(new Runnable() {      // get back on the main thread to start a new activity
                                        @Override
                                        public void run() {
                                            Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                            loginIntent.putExtra("keyLogin", loggedUser);
                                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(loginIntent);
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "User not registered! Please register!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    // if not , throw and error message and put the user to log in again
                    Toast.makeText(getApplicationContext(), "Fill the fields properly!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private Boolean verifyCredentials(String email, String url){
        String IPregex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(:\\d{1,5})?$";
        if(   email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email).matches()) )
        {
            tvAlertEmail.setText("Email format: joedoe@email.com");
            return false;
        }
        else if(url.isEmpty() ||  !(url.length()>6) || !url.matches(IPregex))
        {
            tvAlertURL.setText("URL Wrong! Try again!");
            tvAlertEmail.setText("");
            return false;
        }
        else{
            return true;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // sets the activity intent to the new intent
        //function for getting the intent value
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Assuming "YourDataType" is a String for simplicity; adjust as necessary
        User newUser = intent.getParcelableExtra("keyRegister");
        if(newUser != null) {
            etEmail.setText(newUser.email.toString());
            etURL.setText(newUser.URL.toString());
        }
    }
}