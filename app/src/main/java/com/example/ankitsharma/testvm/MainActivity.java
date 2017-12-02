package com.example.ankitsharma.testvm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText productName, productCode, motorNo, quantity, macaddress, comId, orderNo, connectivity, otherView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productName = (EditText) findViewById(R.id.ProductName);
        productCode = (EditText) findViewById(R.id.ProductCode);
        motorNo = (EditText) findViewById(R.id.motornumber);
        quantity = (EditText) findViewById(R.id.vend_qty);
        macaddress = (EditText) findViewById(R.id.macaddress);
        comId = (EditText) findViewById(R.id.comid);
        orderNo = (EditText) findViewById(R.id.order_no);
        connectivity = (EditText) findViewById(R.id.connectivity);
        otherView = (EditText) findViewById(R.id.other);

        //get mac address
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String mAddress = info.getMacAddress();

        macaddress.setText(mAddress);
    }

    private String getValue(EditText editText){

        if(editText == null){
            return "null";
        }

        String str = editText.getText().toString();
        if(str.trim().length() != 0){
            return str;
        }

        return "null";
    }

    public String getCommand(){

        String finalCommand = "";

        String other = otherView.getText().toString();

        if(!TextUtils.isEmpty(other)){
            //append enter
            return other + System.lineSeparator();
        }

        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        String dateStr = dateFormat.format(date);
        String timeStr = timeFormat.format(date);

//        dateView.setText(dateStr);
//        timeView.setText(timeStr);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ProductName", getValue(productName));
            jsonObject.put("ProductCode", getValue(productCode));
            jsonObject.put("ProductNo", getValue(motorNo));
            jsonObject.put("VendQTY", getValue(quantity));
            jsonObject.put("Status", getValue(null));
            jsonObject.put("ERROR", getValue(null));
            jsonObject.put("DeviceID", getValue(macaddress));
            jsonObject.put("COMID", getValue(comId));
            jsonObject.put("OrderNo", getValue(orderNo));
            jsonObject.put("Connectivity", getValue(connectivity));
            jsonObject.put("Response", getValue(null));
            jsonObject.put("Date", dateStr);
            jsonObject.put("Time", timeStr);


            finalCommand = jsonObject.toString();

            Log.d(">>>>>>", finalCommand);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return finalCommand;
    }

    public void onOneClick(View v){

        final String command = getCommand();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("final command \n\n" + command);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(MainActivity.this, OneActivity.class);
                intent.putExtra("command", command);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onTwoClick(View v){

        final String command = getCommand();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("final command \n\n" + command);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(MainActivity.this, TwoActivity.class);
                intent.putExtra("command", command);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onThreeClick(View v){

        final String command = getCommand();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("final command \n\n" + command);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(MainActivity.this, ThreeActivity.class);
                intent.putExtra("command", command);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
