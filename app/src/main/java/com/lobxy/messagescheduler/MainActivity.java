package com.lobxy.messagescheduler;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Screen";
    private static final int REQUEST_CODE = 200;

    private EditText edit_message;
    private EditText edit_phone;

    private Button pickTimer;

    private TextView text_timer;

    private String mMessage = "";
    private String mPhone = "";
    private String mDate = "";
    private String mTime = "";
    private String mPickedTime = "";

    private long mCurrentTimeInMillis;
    private long mPickedTimeInMillis;

    CountDownTimer countDownTimer;

    //Todo: figure out permission asking
    //todo: add background services.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_message = findViewById(R.id.main_edit_message);
        edit_phone = findViewById(R.id.main_edit_phone);

        text_timer = findViewById(R.id.main_timer);

        pickTimer = findViewById(R.id.main_button_timer);
        Button submit = findViewById(R.id.main_submit);

        checkForPermissions();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessage = edit_message.getText().toString().trim();
                mPhone = edit_phone.getText().toString().trim();

                if (mMessage.isEmpty() || mPhone.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Field Empty", Toast.LENGTH_SHORT).show();
                } else if (mPhone.length() < 10) {
                    Toast.makeText(MainActivity.this, "Invalid Contact Number", Toast.LENGTH_SHORT).show();
                } else if (mDate.isEmpty() || mTime.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Pick Time", Toast.LENGTH_SHORT).show();
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    checkForPermissions();
                } else {
                    startTimer();
                }

            }
        });

        pickTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_timer.setText("Time not set");
                datePicker();
            }
        });

    }

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDate = (day + "/" + (month + 1) + "/" + year);
                Log.i("Main", "onTimeSet: mDate:" + mDate);

                timePicker(datePicker);
            }
        }, mYear, mMonth, mDay);

        dateDialog.show();
    }

    private void timePicker(final DatePicker datePicker) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                mTime = hourOfDay + ":" + minute;

                //show selected time to user.
                mPickedTime = mDate + " " + mTime;
                text_timer.setText(mPickedTime);

                mPickedTimeInMillis = convertIntoMillis(datePicker, timePicker);
            }
        }, mHour, mMinute, false);

        dialog.show();
    }

    private long convertIntoMillis(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        return calendar.getTimeInMillis();
    }

    private void startTimer() {
        //get current time.
        mCurrentTimeInMillis = System.currentTimeMillis();

        Log.i(TAG, "startTimer: current: " + mCurrentTimeInMillis + "\tPicked: " + mPickedTimeInMillis);

        long timerValue = mPickedTimeInMillis - mCurrentTimeInMillis;
        Log.i(TAG, "startTimer: timer: " + timerValue);

        countDownTimer = new CountDownTimer(timerValue, 1000) {
            @Override
            public void onTick(long l) {
                showTime(l);
            }

            @Override
            public void onFinish() {
                sendMessage();
            }
        }.start();

    }

    private void showTime(long timerValue) {
        long seconds = timerValue / 1000;
        long minutes = timerValue / 60000;
        long hour = minutes / 60;
        long days = hour / 24;

        String value = String.format(Locale.getDefault(), "%1d:%1d:%1d:%1d", days, hour, minutes, seconds);
        text_timer.setText(value);
    }

    private void sendMessage() {
        SmsManager smgr = SmsManager.getDefault();
        try {
            smgr.sendTextMessage(mPhone, null, mMessage, null, null);
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error! Retry.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "sendMessage: error: " + e.getLocalizedMessage());
        }
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }

}//EOC
