package com.example.projectsos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private Switch themeSwitch;
    private Button customizeButton, restoreDefaultsButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity); // Set the layout

        // Initialize UI elements
        languageSpinner = findViewById(R.id.languageSpinner);
        themeSwitch = findViewById(R.id.themeSwitch);
        customizeButton = findViewById(R.id.customizeButton);
        restoreDefaultsButton = findViewById(R.id.restoreDefaultsButton);

        // Setup SharedPreferences
        sharedPreferences = getSharedPreferences("SOSAppSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Populate Language Spinner
        String[] languages = getResources().getStringArray(R.array.language_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Load saved preferences
        loadSettings();

        // Set Theme Switch Listener
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("darkMode", isChecked);
            editor.apply();

            // Apply theme change immediately
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            // You might want to recreate the activity for the theme change to take full effect
            // recreate();
        });

        // Set Customize SOS Button Listener (Placeholder)
        customizeButton.setOnClickListener(v -> {
            // Implement your customization logic here
            Toast.makeText(this, "Customize SOS Button clicked", Toast.LENGTH_SHORT).show();
        });

        // Restore Defaults Button Listener
        restoreDefaultsButton.setOnClickListener(v -> {
            editor.clear();
            editor.apply();
            loadSettings(); // Reload settings after restoring defaults
            Toast.makeText(this, "Settings restored to default", Toast.LENGTH_SHORT).show();

            // Apply default theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            // You might want to recreate the activity for the theme change to take full effect
            // recreate();
        });
    }

    private void loadSettings() {
        // Load saved preferences or set defaults
        boolean darkMode = sharedPreferences.getBoolean("darkMode", false);
        themeSwitch.setChecked(darkMode);

        int languagePosition = sharedPreferences.getInt("languagePosition", 0);
        languageSpinner.setSelection(languagePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save current settings
        int selectedLanguagePosition = languageSpinner.getSelectedItemPosition();
        editor.putInt("languagePosition", selectedLanguagePosition);
        editor.apply();
    }
}