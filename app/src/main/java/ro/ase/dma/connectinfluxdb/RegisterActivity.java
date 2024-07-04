package ro.ase.dma.connectinfluxdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns; // user for checking if the email matches a normal format of an email
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private UserRoomDataBase userDatabase;
    private UserDao userDao;
    private Button btnSignup;
    private EditText etEmail;
    private EditText etURL;
    private EditText etURLVerify;
    private TextView tvAlertEmail;
    private TextView tvAlertPassword;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        btnSignup  = findViewById(R.id.btnSignup);
        etEmail = findViewById(R.id.etEmail);
        etURL = findViewById(R.id.etURL);
        etURLVerify = findViewById(R.id.etURLVerify);
        tvAlertEmail = findViewById(R.id.tvAlertEmail);
        tvAlertPassword = findViewById(R.id.tvAlertPassword);
        tvLogin = findViewById(R.id.tvLogin);

        userDatabase = UserRoomDataBase.getInstance(this);
        userDao = userDatabase.getUserDao();

        // real time validation of second password being the same as the first password
        // validation of characters in email and password

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvAlertEmail.setText("");
                tvAlertPassword.setText("");
                String email = etEmail.getText().toString();
                String password = etURL.getText().toString();
                String passwordVerify = etURLVerify.getText().toString();

                if(userDao.getUserByEmail(email) != null)   // if the user exists in the database
                {
                    Toast.makeText(getApplicationContext(), "Email unavailable! Pick another one!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (verifyCredentials(email, password, passwordVerify)) {
                        new Thread(new Runnable() {     // insert user into database on a secondary thread in order not to block the activity thread.
                            @Override
                            public void run() {
                                //insert into the database in a separate thread in order not to block the main thread
                                User newUser = new User(email, password, null);  // set the engineSeries to null// TO-DO
                                userDao.insert(newUser);
                                Log.w("User:", newUser.toString());
                                //saved user to database then proceed to the login page in order for user to log in into the application
                                // start login activity with user in the intent on the main thread in order to transmit the logged user.
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("keyRegister", newUser);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        //FLAG_ACTIVITY_CLEAR_TOP brings the LoginActivity to the top of the stack, closing the ones above it
                                        //FLAG_ACTIVITY_SINGLE_TOP if the activity is alive, Android won't create another activity.
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).start();
                    } else {
                        // display a error message
                        Toast.makeText(getApplicationContext(), "Fill the fields properly!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private Boolean verifyCredentials(String email, String URL, String URLVerify){
        String IPregex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(:\\d{1,5})?$";
        if( email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email).matches()))
        {
            tvAlertEmail.setText("Email format: joedoe@email.com");
            return false;
        }
        else if ( URL.isEmpty() || URLVerify.isEmpty() || !(URL.length()>6) || !(URL.equals(URLVerify)) || !URL.matches(IPregex))
        {
            tvAlertPassword.setText("URL Wrong! Try again!");
            tvAlertEmail.setText("");
            return false;
        }
        else{
            return true;
        }

    }
}