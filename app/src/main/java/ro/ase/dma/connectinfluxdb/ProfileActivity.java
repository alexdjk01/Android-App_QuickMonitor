package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private EditText etEmail;
    private EditText etURL;

    private ImageView ivEditEmail;
    private ImageView ivEditURL;

    private Button btnSave;
    private Button btnLogout;

    private TextView tvDelete;
    private TextView tvAlertEmail;
    private TextView tvAlertURL;
    private UserRoomDataBase userDatabase;
    private UserDao userDao;

    private User receivedUserHome;

    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigation = findViewById(R.id.navigationMenuBar);
        etEmail = findViewById(R.id.etEmail);
        etURL = findViewById(R.id.etURL);
        ivEditEmail = findViewById(R.id.ivEditEmail);
        ivEditURL = findViewById(R.id.ivEditURL);
        tvAlertEmail = findViewById(R.id.tvAlertEmail);
        tvAlertURL = findViewById(R.id.tvAlertURL);

        userDatabase = UserRoomDataBase.getInstance(this);
        userDao = userDatabase.getUserDao();


        // need to implement the functionality for those 2 buttons
        btnSave = findViewById(R.id.btnSave);
        // butonu de save o sa salveze useru in baza de date ( am acolo un thread si un update care trb bagat in onclick la save
        btnLogout = findViewById(R.id.btnLogout);
        // text view that will delete the user for the database when clicked
        tvDelete = findViewById(R.id.tvDelete);


        Intent receivedIntentLogged = getIntent();
        if(receivedIntentLogged!=null) {
            receivedUserHome = receivedIntentLogged.getParcelableExtra("keyHome");
            if (receivedUserHome != null) {
                // auto complete the editexts with the values of the logged user
                etEmail.setText(receivedUserHome.email.toString());
                etURL.setText((receivedUserHome.URL.toString()));
                //set the property of writing the imput to galse in order not to edit it by mistake, the user shoud press the edit icon to edit
                etEmail.setEnabled(false);
                etURL.setEnabled(false);
                Log.e("Profile activity: ", receivedUserHome.toString());
                // data was loaded correctly
            }
            else
            {
                Log.e("Profile activity: ", "receivedUserHome is null");
            }
        }


        //edit the email
        ivEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // enable to edit the edittext
                etEmail.setEnabled(true);
                etEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //here nothing
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // here also nothing
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        receivedUserHome.setEmail(editable.toString());
                        Log.e(" User EDITED: ", receivedUserHome.toString());
                    }
                });
            }
        });

        //edit the password
        ivEditURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etURL.setEnabled(true);
                etURL.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //nothing
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //nothing
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        receivedUserHome.setURL(editable.toString());
                        Log.e(" User EDITED: ", receivedUserHome.toString());
                    }
                });
            }
        });

        //saves the changes made into the database
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAlertURL.setText("");
                tvAlertEmail.setText("");
                sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
                String currentEmailLogged = sharedPreferences.getString("email","djkmata.djkmata@gmail.com");
                if(userDao.getUserByEmail(etEmail.getText().toString()) != null && !userDao.getUserByEmail(etEmail.getText().toString()).email.equals(currentEmailLogged) )   // if the user exists in the database
                {
                    Toast.makeText(getApplicationContext(), "Email unavailable! Pick another one!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (verifyCredentials(etEmail.getText().toString(), etURL.getText().toString())) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                userDao.update(receivedUserHome);
                            }
                        }).start();
                        sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("email", String.valueOf(etEmail.getText()));
                        editor.putString("URL", String.valueOf(etURL.getText()));
                        editor.commit();
                        Intent toHome = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(toHome);
                    } else {
                        Toast.makeText(getApplicationContext(), "Fill the fields properly!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //logs out the user and sends the user to the home UI
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toStartApp = new Intent(getApplicationContext(), StartAppActivity.class);
                toStartApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toStartApp);
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //implement a alert dialog
                alertDialog();
            }
        });


        //we use setOnItemSelected for the bottom navigation bar and will write the implementation of each "button" in the menu
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navHome)
                {
                    Intent toHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(toHomeIntent);

                }
                else if(item.getItemId() == R.id.navSettings)
                {
                    Intent toSettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(toSettingsIntent);

                }
                else
                {
                  //do nothing
                }
                return true;
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // sets the activity intent to the new intent
    }

    //alert dialog for getting user attention when pressing delete account
    // TO-DO design - maybe round the corners idk
    public void alertDialog()
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alertBuilder.setTitle("Delete account");
        alertBuilder.setMessage("Are you sure?");
        alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete the user and go back to the starting page
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDao.delete(receivedUserHome);
                    }
                }).start();
                Intent toStartApp = new Intent(getApplicationContext(), StartAppActivity.class);
                toStartApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toStartApp);
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // nothing happens. When clicked, it will just close the dialog and returns to the previous page
            }
        });

        AlertDialog alertDialog = alertBuilder.create();

        //style the buttons
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red_cancel));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.green_avocado));
            }
        });
        alertDialog.show();
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



}

