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
                Log.e("Profile activity: ", receivedUserHome.toString());
                // data was loaded correctly
            }
        }
        // auto complete the editexts with the values of the logged user
        etEmail.setText(receivedUserHome.email.toString());
        etURL.setText((receivedUserHome.URL.toString()));
        //set the property of writing the imput to galse in order not to edit it by mistake, the user shoud press the edit icon to edit
        etEmail.setEnabled(false);
        etURL.setEnabled(false);

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

                Log.e("User IN Save:", receivedUserHome.toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userDao.update(receivedUserHome);
                    }
                }).start();

                sharedPreferences = getSharedPreferences("loginData",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("email", String.valueOf(etEmail.getText()));
                editor.putString("URL", String.valueOf(etURL.getText()));
                editor.commit();

                Intent toHome = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(toHome);
            }
        });

        //logs out the user and sends the user to the home UI
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toStartApp = new Intent(getApplicationContext(), StartAppActivity.class);
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
                    Toast.makeText(getApplicationContext(),"HOME PRESSED!",Toast.LENGTH_SHORT).show();
                }
                else if(item.getItemId() == R.id.navSettings)
                {
                    Intent toSettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(toSettingsIntent);
                    Toast.makeText(getApplicationContext(),"Settings PRESSED!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Profile PRESSED!",Toast.LENGTH_SHORT).show();
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

                //opens the starting activity
                Intent toStartApp = new Intent(getApplicationContext(), StartAppActivity.class);
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


}

