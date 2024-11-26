package com.example.projectsos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CALL_PERMISSION = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 3;
    private String[] phoneNumbers = {"+916282237900", "+917025185532", "+918714733600", "+918589078474", "+919947640096"};
    private String emergencyContact = "+917025185532"; // Single emergency contact for sosButton2
    private String message = "This is an SOS message. I need help!";
    private FusedLocationProviderClient fusedLocationClient;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private CountDownTimer sosTimer; // Countdown timer
    private boolean isTimerRunning = false; // Timer state tracker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        ImageButton sosButton = findViewById(R.id.sos_button);
        ImageButton sosButton2 = findViewById(R.id.sos_button2);
        TextView timerText = findViewById(R.id.timer_text); // TextView to display the countdown timer

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (sosButton != null) {
            sosButton.setOnClickListener(v -> handleSOSButtonClick(timerText, this::performSOSActionAllContacts));
        }

        if (sosButton2 != null) {
            sosButton2.setOnClickListener(v -> handleSOSButtonClick(timerText, this::performSOSActionSingleContact));
        }
    }

    private void handleSOSButtonClick(TextView timerText, Runnable action) {
        if (isTimerRunning) {
            sosTimer.cancel();
            timerText.setVisibility(View.GONE);
            isTimerRunning = false;
            Toast.makeText(this, "SOS action canceled", Toast.LENGTH_SHORT).show();
        } else {
            timerText.setVisibility(View.VISIBLE);

            sosTimer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerText.setText("SOS in " + (millisUntilFinished / 1000) + " seconds");
                }

                @Override
                public void onFinish() {
                    timerText.setVisibility(View.GONE);
                    isTimerRunning = false;
                    action.run();
                }
            };
            isTimerRunning = true;
            sosTimer.start();
        }
    }

    private void performSOSActionAllContacts() {
        if (checkAndRequestPermissions()) {
            sendSMSToAllContacts();
            makeRepetitiveCalls();
            shareLocationWithAllContacts();
        }
    }

    private void performSOSActionSingleContact() {
        if (checkAndRequestPermissions()) {
            sendSMSToSingleContact();
            shareLocationWithSingleContact();
        }
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CALL_PERMISSION);
            return false;
        }
        return true;
    }

    private void sendSMSToAllContacts() {
        for (String phoneNumber : phoneNumbers) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
        Toast.makeText(this, "SOS message sent to all contacts", Toast.LENGTH_SHORT).show();
    }

    private void sendSMSToSingleContact() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(emergencyContact, null, message, null, null);
        Toast.makeText(this, "SOS message sent to the emergency contact", Toast.LENGTH_SHORT).show();
    }

    private void makeRepetitiveCalls() {
        for (String phoneNumber : phoneNumbers) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            }
        }
    }

    private void shareLocationWithAllContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String locationMessage = message + "\nLocation: https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                for (String phoneNumber : phoneNumbers) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, locationMessage, null, null);
                }
            }
        });
    }

    private void shareLocationWithSingleContact() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String locationMessage = message + "\nLocation: https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(emergencyContact, null, locationMessage, null, null);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
