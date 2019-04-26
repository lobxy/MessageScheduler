package com.lobxy.messagescheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText edit_message;
    EditText edit_phone;
    Button pickTimer;

    private String mMessage;
    private String mPhone;
    private String mDate;
    private String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_message = findViewById(R.id.main_edit_message);
        edit_phone = findViewById(R.id.main_edit_phone);

        pickTimer = findViewById(R.id.main_button_timer);
        Button submit = findViewById(R.id.main_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessage = edit_message.getText().toString().trim();
                mPhone = edit_message.getText().toString().trim();
                if (mMessage.isEmpty() || mPhone.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Field Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if not picked a time. show toast.

                sendMessage();

            }
        });


    }

    private void sendMessage() {
        SmsManager smgr = SmsManager.getDefault();
        try {
            smgr.sendTextMessage(mPhone, null, mMessage, null, null);
        } catch (Exception e) {
            Toast.makeText(this, "Error! Retry.", Toast.LENGTH_SHORT).show();
            Log.i("Main Screen", "sendMessage: error: " + e.getLocalizedMessage());
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

                timePicker();
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
        dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 43200 * 1000);

        dateDialog.show();
    }

    private void timePicker() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    mTime = (hourOfDay + ":" + minute + ":" + "PM");
                } else {
                    mTime = (hourOfDay + ":" + minute + ":" + "AM");
                }

                Log.i("Main", "onTimeSet: mDate:" + mTime);

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

}
