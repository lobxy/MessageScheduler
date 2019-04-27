package com.lobxy.messagescheduler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    EditText edit_message;
    EditText edit_phone;

    Button pickTimer;

    TextView text_timer;

    private String mMessage;
    private String mPhone;
    private String mDate;
    private String mTime;

    private long mCurrentTimeInMillis;
    private long mPickedTimeInMillis;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_message = findViewById(R.id.main_edit_message);
        edit_phone = findViewById(R.id.main_edit_phone);

        text_timer = findViewById(R.id.main_timer);

        pickTimer = findViewById(R.id.main_button_timer);
        Button submit = findViewById(R.id.main_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mMessage = edit_message.getText().toString().trim();
//                mPhone = edit_message.getText().toString().trim();
//                if (mMessage.isEmpty() || mPhone.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Field Empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                //if not picked a time. show toast.
//                if (mDate.isEmpty() || mTime.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Pick Time", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                startTimer();
                // sendMessage();

            }
        });

        pickTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

    }

    //start countdowntimer for current - picked time.

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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Timer Up")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }.start();

    }

    private void showTime(long timerValue) {
        long minutes = timerValue / 60000;
        long hour = minutes / 60;
        long days = hour / 24;

        String value = String.format(Locale.getDefault(), "%1d:%1d:%1d", days, hour, minutes);
        text_timer.setText(value);
    }

    private void sendMessage() {
        SmsManager smgr = SmsManager.getDefault();
        try {
            smgr.sendTextMessage(mPhone, null, mMessage, null, null);
        } catch (Exception e) {
            Toast.makeText(this, "Error! Retry.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "sendMessage: error: " + e.getLocalizedMessage());
        }
    }

    //HANDLE DATA
    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mDate = (day + "-" + (month + 1) + "-" + year);
                Log.i("Main", "onTimeSet: mDate:" + mDate);

                timePicker(datePicker);
            }
        };

        DatePickerDialog dateDialog = new DatePickerDialog(this, dateSetListener, mYear, mMonth, mDay);
        dateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //do nothing.
                //todo. remove this.
            }
        });

        dateDialog.show();
    }

    private void timePicker(final DatePicker datePicker) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                mTime = hourOfDay + ":" + minute;

                mPickedTimeInMillis = convertIntoMillis(datePicker, timePicker);

                Log.i(TAG, "onTimeSet: pickedTimeInMillis: " + mPickedTimeInMillis);
            }
        };

        TimePickerDialog dialog = new TimePickerDialog(this, timeSetListener, mHour, mMinute, false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mDate = "";
            }
        });

        dialog.show();
    }

    private long convertIntoMillis(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        return calendar.getTimeInMillis();
    }

}
