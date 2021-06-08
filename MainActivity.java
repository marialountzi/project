/*
 * Sensors Monitor (v1)
 *
 *  UI Activity
 */
package com.sensormonitor.app.sensorsmonitor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener, LoginDialog.LoginDialogListener {
    public static String APP_SETTINGS = "settings",
                         SETTING_EMAIL = "email",
                         SETTING_PASSWORD = "password";

    private static final int ALARM_PI_ID     = 1234,     // PendingIntent, unique Intent ID
                             ALARM_EVERY_MS  = 5000;     // Alarm Interval in Millis.

    // AlarmManager
    private AlarmManager alarmManager = null;

    String userEmail, userPassword;

    // Setup UI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get AlarmManager instance (break on null)
        if((alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE)) == null) {
            Toast.makeText(this, R.string.main_alarm_manager_missing, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ((Button)findViewById(R.id.buttonStart)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonStop)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonView)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonExit)).setOnClickListener(this);

        // Get last used user email and password
        SharedPreferences settings = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE);
        userEmail = settings.getString(SETTING_EMAIL, "");
        userPassword = settings.getString(SETTING_PASSWORD, "");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            // Create an exact (for API Level 18 or less) Alarm using AlarmManager
            case R.id.buttonStart:
                new LoginDialog(this, userEmail, userPassword, this);
                return;
            // Cancel AlarmManager repeating Alarm
            case R.id.buttonStop:
                alarmManager.cancel(PendingIntent.getBroadcast(this, ALARM_PI_ID,
                    new Intent(this, AlarmManagerBr.class), PendingIntent.FLAG_CANCEL_CURRENT));
                return;
            case R.id.buttonView:
                Toast.makeText(this, "todo", Toast.LENGTH_SHORT).show();;
                return;
            case R.id.buttonExit:
                finish();
                return;
        }
    }

    @Override
    public void onLoginDialog(int which, String email, String password) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            // Store user login credentials to app. settings
            getSharedPreferences(APP_SETTINGS, MODE_PRIVATE).edit()
                .putString(SETTING_EMAIL, email)
                .putString(SETTING_PASSWORD, password)
                .commit();

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + ALARM_EVERY_MS, ALARM_EVERY_MS,
                PendingIntent.getBroadcast(this, ALARM_PI_ID, new Intent(this, AlarmManagerBr.class),
                PendingIntent.FLAG_CANCEL_CURRENT));
        }
    }
}
