package apollo.safety.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import apollo.safety.R;
import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {
    Button btnAddContacts, btnViewContacts;
    CardView cardView;
    private SimpleLocation location;
    LocationManager locationManager;
    String latitude, longitude;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
//    ArrayList<String> numberList;
    SmsManager smsManager;
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Safety");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setLogo(R.mipmap.ic_launcher_foreground);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        cardView = findViewById(R.id.cardView3);
        btnAddContacts = findViewById(R.id.btnAddContacts);
        btnViewContacts = findViewById(R.id.btnViewContacts);
        location = new SimpleLocation(MainActivity.this);
    //    numberList = new ArrayList<>();
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        sharedPreferences = getSharedPreferences("apollo.safety.USERNAME", MODE_PRIVATE);
        final String username = sharedPreferences.getString("Username", "");
        databaseReference = FirebaseDatabase.getInstance().getReference("contacts").child(username);
        try {
            if (locationManager != null) {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ignored) {
        }
        //check gps status
        if (!gps_enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Enable GPS")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            Toast.makeText(MainActivity.this, "Please enable Location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            Toast.makeText(MainActivity.this, "You have to enable GPS", Toast.LENGTH_SHORT).show();

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if (gps_enabled) {

            location = new SimpleLocation(MainActivity.this);
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

             /*____OnClick Listeners____*/

            //Add contacts
            btnAddContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ContactPickerActivity.class);
                    startActivity(intent);
                }
            });

            //View Contacts
            btnViewContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ContactViewerActivity.class);
                    startActivity(intent);
                }
            });

            //SOS
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String message = "I'm in danger. Here is my location ." + " http://maps.google.com/?q=" + latitude + "," + longitude;
                    smsManager = SmsManager.getDefault();
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String numbers = String.valueOf(dataSnapshot1.child("number").getValue());
                                if (numbers != null) {
                                //    numberList.add(numbers);
                                    Log.d("Numbers:", numbers);
                                    smsManager.sendTextMessage(numbers, null, message, null, null);
                                }
                            }
                            Toast.makeText(MainActivity.this, "Messages Sent", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
            });    //cardView listener
        }
    }
//onCreate ends here


    @Override
    protected void onResume() {
        super.onResume();
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //class ends here
}

