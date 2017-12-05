package com.example.ankitsharma.testvm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class TwoActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    BluetoothDevice btDevice;
    BluetoothSocket btSocket;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String command;

    TextView responseView;

    boolean stopWorker = false;
    int readBufferPosition = 0;
    Thread workerThread;
    byte[] readBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        command = getIntent().getStringExtra("command");

        responseView = (TextView) findViewById(R.id.response);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        } else if(!btAdapter.isEnabled()){
            Toast.makeText(getApplicationContext(), "Turn on bluetooth", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Finding paired device", Toast.LENGTH_LONG).show();

            pairedDevices = btAdapter.getBondedDevices();

            if(pairedDevices != null && pairedDevices.size() > 0){
                //we have a device
                btDevice = pairedDevices.iterator().next();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);

                            if(btSocket != null){
                                postMsgToUI("Bluetooth connected successfully");
                                btSocket.connect();
                            } else {
                                postMsgToUI("Bluetooth connection FAILED");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            postMsgToUI("Bluetooth connection FAILED");
                        }
                    }
                });


            } else {
                Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void postMsgToUI(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSendClick(View v){
        if(btSocket != null){
            try {
                btSocket.getOutputStream().write(command.getBytes());
                beginListenForData();
                Toast.makeText(getApplicationContext(), "Command sent Success", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Command sent Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 13; //This is the ASCII code for a newline character

        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        InputStream inputStream = btSocket.getInputStream();

                        int bytesAvailable = inputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            responseView.append(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopWorker = true;
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
