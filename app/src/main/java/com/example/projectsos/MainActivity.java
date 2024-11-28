package com.example.projectsos;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CALL_PERMISSION = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 3;
    private FusedLocationProviderClient fusedLocationClient;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private CountDownTimer sosTimer;
    private boolean isTimerRunning = false;

    private ContactDao contactDao;

    private TextView navUsername;
    private TextView navEmail;
    private ImageView navProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (!isLoggedIn) {
            // If not logged in, redirect to login screen
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Finish current activity
        } else {
            setContentView(R.layout.activity_main);

            // Initialize Views
            ImageButton menuButton = findViewById(R.id.imageButton);
            ImageButton sosButton = findViewById(R.id.sos_button);
            ImageButton sosButton2 = findViewById(R.id.sos_button2);
            TextView timerText = findViewById(R.id.timer_text);

            // Drawer Setup
            drawerLayout = findViewById(R.id.drawer_layout);
            toolbar = findViewById(R.id.toolbar);
            navigationView = findViewById(R.id.nav_view);

            // Location Client
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            // Database Setup
            AppDatabase db = AppDatabase.getInstance(this);
            contactDao = db.contactDao();

            // Drawer Toggle
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);

            // Menu Button click action
            menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

            // Retrieve user details and set them in the navigation header
            View headerView = navigationView.getHeaderView(0);
            navUsername = headerView.findViewById(R.id.nav_username);
            navEmail = headerView.findViewById(R.id.nav_email);


            loadProfileData();

            // SOS Button actions
            sosButton.setOnClickListener(v -> handleSOSButtonClick(timerText, this::performSOSActionAllContacts));
            sosButton2.setOnClickListener(v -> handleSOSButtonClick(timerText, this::performSOSActionSingleContactWithDelay));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile data when the activity resumes
        loadProfileData();
    }

    // Method to load profile data from SharedPreferences and update the navigation header
    private void loadProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User Name");
        String email = sharedPreferences.getString("email", "user@example.com");
        String profileImageUri = sharedPreferences.getString("profile_image_uri", null);

        // Update navigation header with the new data
        navUsername.setText(username);
        navEmail.setText(email);

        // Update profile image if available
        if (profileImageUri != null) {
            navProfileImage.setImageURI(Uri.parse(profileImageUri));
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
        contactDao.getAllContacts().observe(this, contacts -> {
            if (contacts != null && !contacts.isEmpty()) {
                for (int i = 0; i < contacts.size(); i++) {
                    Contact contact = contacts.get(i);
                    Log.d("MainActivity", "Sending SOS to: " + contact.name + ", " + contact.phoneNumber);

                    // Send SOS message
                    sendSMSToSingleContact(contact.phoneNumber);

                    // Share location with the contact
                    shareLocationWithSingleContact(contact.phoneNumber);

                    // Delay next call and message by 7 seconds
                    int delay = i * 7000; // 7 seconds delay between each contact
                    new Handler().postDelayed(() -> makeCallToContact(contact.phoneNumber), delay);
                }
            } else {
                Toast.makeText(MainActivity.this, "No contacts found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSMSToSingleContact(String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "This is an SOS message. I need help!", null, null);
        Toast.makeText(this, "SOS message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
    }

    private void makeCallToContact(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
            Toast.makeText(this, "Calling " + phoneNumber, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission not granted for calling", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareLocationWithSingleContact(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String locationMessage = "SOS message with location: https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, locationMessage, null, null);
                Toast.makeText(MainActivity.this, "Location shared with emergency contact", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Location not available. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSOSActionSingleContactWithDelay() {
        String phoneNumber = "7025185532"; // Hardcoded phone number
        sendSMSToSingleContact(phoneNumber);

        // Fetch location and share with the contact
        shareLocationWithSingleContact(phoneNumber);

        // Delay before making the call
        new Handler().postDelayed(() -> makeCallToContact(phoneNumber), 5000); // Delay of 5 seconds
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_contact) {
            startActivity(new Intent(MainActivity.this, AddContactActivity.class));
        } else if (id == R.id.edit_profile) {
            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
        } else if (id == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

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
