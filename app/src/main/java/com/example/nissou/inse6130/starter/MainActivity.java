package com.example.nissou.inse6130.starter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nissou.inse6130.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static Button start;
    private static EditText phoneNumber;
    private int permissionCheck;
    private int checkPermissionWifiState;
    private final int REQUEST_PERMISSION_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(android.R.style.ThemeOverlay_Material_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        permissionCheck = this.checkSelfPermission(Manifest.permission.SEND_SMS);

        checkPermissionWifiState =  this.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);


        if (Build.VERSION.SDK_INT > 9){

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        start = (Button) findViewById(R.id.etStart);
        phoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

        start.setOnClickListener(this);




        boolean checkWifi = this.checkWifiPermission();

        if (checkWifi == true){

            String ipAddress = this.getDeviceCurrentIPAddress();

            Toast.makeText(this.getApplicationContext(), "Your IP Address is : " + ipAddress, Toast.LENGTH_LONG).show();

        }else{

            Toast.makeText(this.getApplicationContext(), "You are not connected or you do not have wifi connection!!!", Toast.LENGTH_LONG).show();


        }






    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.etStart:


                if (!phoneNumber.getText().toString().equals("") || phoneNumber.getText().toString() != ""){



                    String phoneNumberStr = phoneNumber.getText().toString();



                    Toast.makeText(this.getApplicationContext(), phoneNumberStr, Toast.LENGTH_LONG).show();


                    String message = "Hello, this is a test message.";


                    sendMessage(message, phoneNumberStr);










                    break;

                }else {


                    Toast.makeText(getApplicationContext(), "Please enter a phone number.", Toast.LENGTH_LONG).show();
                }


        }

    }



    public void sendMessage(String message, String phoneNumberStr){



        PackageManager packageManager = this.getPackageManager();

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) && !packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)){

            Toast.makeText(this.getApplicationContext(), "Sorry, your device probably cannot send SMS...", Toast.LENGTH_LONG).show();



        }else {


            SmsManager sms = SmsManager.getDefault();

            final Context currentContext = this.getApplicationContext();

            PendingIntent sentPending = PendingIntent.getBroadcast(currentContext, 0, new Intent("SENT"), 0);

            currentContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    switch (getResultCode()){


                        case MainActivity.RESULT_OK:
                            Toast.makeText(currentContext, "Sent.",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(currentContext, "Not Sent: Generic failure.",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(currentContext, "Not Sent: No service (possibly, no SIM-card).",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(currentContext, "Not Sent: Null PDU.",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(currentContext, "Not Sent: Radio off (possibly, Airplane mode enabled in Settings).",
                                    Toast.LENGTH_LONG).show();
                            break;


                    }

                }
            }, new IntentFilter("SENT"));



            PendingIntent deliveredPending = PendingIntent.getBroadcast(currentContext,
                    0, new Intent("DELIVERED"), 0);

            currentContext.registerReceiver(
                    new BroadcastReceiver()
                    {
                        @Override
                        public void onReceive(Context arg0,Intent arg1)
                        {
                            switch (getResultCode())
                            {
                                case MainActivity.RESULT_OK:
                                    Toast.makeText(currentContext, "Delivered.",
                                            Toast.LENGTH_LONG).show();
                                    break;
                                case MainActivity.RESULT_CANCELED:
                                    Toast.makeText(currentContext, "Not Delivered: Canceled.",
                                            Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }, new IntentFilter("DELIVERED"));



            if (permissionCheck == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this.getApplicationContext(), "Permission granted to send sms", Toast.LENGTH_LONG).show();
                sms.sendTextMessage(phoneNumberStr, null, message, sentPending, deliveredPending);


            }else{

                //Toast.makeText(this.getApplicationContext(), "Permission denied to send sms", Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSION_SEND_SMS);
            }






        }

    }




    public boolean checkWifiPermission(){


        if (checkPermissionWifiState == PackageManager.PERMISSION_GRANTED){


            return true;


        }else{

            return false;


        }



    }

    public String getDeviceCurrentIPAddress(){





        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();









        return Formatter.formatIpAddress(ipAddress);
    }
}
