package ro.ase.dma.connectinfluxdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvSignUp;
    private UserRoomDataBase userDatabase;
    private UserDao userDao;
    private TextView tvAlertEmail;
    private TextView tvAlertPassword;



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
        etPassword = findViewById(R.id.etPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvAlertEmail = findViewById(R.id.tvAlertEmail);
        tvAlertPassword = findViewById(R.id.tvAlertPassword);

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
                String password = etPassword.getText().toString();
                //check if the credentials are saved in the database
                if(verifyCredentials(email,password))   // if the email and password are written properly
                {
                    if(userDao.getUserByEmail(email) != null)   // if the user exists in the database
                    {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                //get the user from the database by his email ( the email is unique to each user) in a new thread in order not to block the main one
                                User loggedUser = userDao.getUserByEmail(email);
                                if(loggedUser!= null)
                                {
                                    Log.w("USERRRRRR", loggedUser.toString());

                                    // need to lunch the main dashboard
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

    private Boolean verifyCredentials(String email, String password){

        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).+$"; // ^/$ start and end of regex string
        //  (?=.*[A-Z]) checks if an uppercase letter is present in the password input
        //  (?=.*[0-9]) checks if a number is present in the password input
        //so if the email does not match email format or the password does not contain uppercase and number or the length is smaller than 7 it will return false
        if(   email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email).matches()) )
        {
            tvAlertEmail.setText("Email format: joedoe@email.com");
            return false;
        }
        else if(password.isEmpty() || !(password.matches(passwordRegex))  || !(password.length()>6))
        {
            tvAlertPassword.setText("Must contain: a-z/A-Z/0-9 and length>7");
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
            etPassword.setText(newUser.password.toString());
        }
    }
}