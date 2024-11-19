package com.example.projectsos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CALL_PERMISSION = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 3;
    private String[] phoneNumbers = {"+916282237900", "+917025185532", "+918714733600", "+918589078474", "+919947640096"};
    private String emergencyContact = "+917025185532"; // Single emergency contact for sosButton2
    private String message = "This is an SOS message. I need help!";
    private FusedLocationProviderClient fusedLocationClient;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;




    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        ImageButton sosButton = findViewById(R.id.sos_button);
        ImageButton sosButton2 = findViewById(R.id.sos_button2);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set the NavigationView's item selected listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set the first button's functionality to contact all numbers in phoneNumbers
        if (sosButton != null) {
            sosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performSOSActionAllContacts();
                }
            });
        }

        // Set the second button's functionality to contact only the emergency contact
        if (sosButton2 != null) {
            sosButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performSOSActionSingleContact();
                }
            });
        }
    }


    private void performSOSActionAllContacts() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CALL_PERMISSION);
        } else {
            // Permissions granted, perform actions
            sendSMSToAllContacts();
            makeRepetitiveCalls();
            shareLocationWithAllContacts();
        }
    }

    private void performSOSActionSingleContact() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permissions granted, perform actions
            sendSMSToSingleContact();
            shareLocationWithSingleContact();
        }
    }

    private void sendSMSToAllContacts() {
        SmsManager smsManager = SmsManager.getDefault();
        for (String phoneNumber : phoneNumbers) {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
        Toast.makeText(this, "SOS SMS sent to all contacts", Toast.LENGTH_SHORT).show();
    }

    private void sendSMSToSingleContact() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(emergencyContact, null, message, null, null);
        Toast.makeText(this, "SOS SMS sent to emergency contact", Toast.LENGTH_SHORT).show();
    }

    private void makeRepetitiveCalls() {
        for (String phoneNumber : phoneNumbers) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
                Toast.makeText(this, "Calling " + phoneNumber, Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(2000); // Delay for 2 seconds
                } catch (InterruptedException e) {
                    Log.e("MainActivity", "Error in call delay: " + e.getMessage());
                }
            }
        }
    }

    private void shareLocationWithAllContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String locationMessage = "My current location: https://www.google.com/maps/search/?api=1&query=" +
                                    location.getLatitude() + "," + location.getLongitude();
                            SmsManager smsManager = SmsManager.getDefault();
                            for (String phoneNumber : phoneNumbers) {
                                smsManager.sendTextMessage(phoneNumber, null, message + "\n" + locationMessage, null, null);
                            }
                            Toast.makeText(MainActivity.this, "Location shared with all contacts", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void shareLocationWithSingleContact() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String locationMessage = "My current location: https://www.google.com/maps/search/?api=1&query=" +
                                    location.getLatitude() + "," + location.getLongitude();
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(emergencyContact, null, message + "\n" + locationMessage, null, null);
                            Toast.makeText(MainActivity.this, "Location shared with emergency contact", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performSOSActionAllContacts();
            } else {
                Toast.makeText(this, "Permission denied for calls or SMS", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performSOSActionSingleContact();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
 }
}
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle item 1 click
        } else if (id == R.id.add_contact) {
            // Handle item 2 click
        } // ... handle other menu items ...

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}