package com.example.ankitsharma.testvm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ThreeActivity extends AppCompatActivity implements BluetoothWrapper.MessageHandler {

    TextView responseView;
    BluetoothWrapper bluetoothWrapper;

    BroadcastReceiver connectReceiver;
    BroadcastReceiver disconnectReceiver;

    String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        responseView = (TextView) findViewById(R.id.response);
        bluetoothWrapper = new BluetoothWrapper(this, this, "VM");

        command = getIntent().getStringExtra("command");

        connectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(ThreeActivity.this, "Bluetooth connected", Toast.LENGTH_SHORT).show();
            }
        };

        disconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(ThreeActivity.this, "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(connectReceiver, new IntentFilter(BluetoothWrapper.BLUETOOTH_CONNECTED));
        LocalBroadcastManager.getInstance(this).registerReceiver(disconnectReceiver, new IntentFilter(BluetoothWrapper.BLUETOOTH_DISCONNECTED));
        LocalBroadcastManager.getInstance(this).registerReceiver(disconnectReceiver, new IntentFilter(BluetoothWrapper.BLUETOOTH_FAILED));
    }

    public void onSendClick(View v){
        try {
            bluetoothWrapper.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int read(int bufferSize, byte[] buffer) {
        String str = new String(buffer);
        responseView.setText(str);
        return 0;

    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothWrapper.onResume();
    }
}
