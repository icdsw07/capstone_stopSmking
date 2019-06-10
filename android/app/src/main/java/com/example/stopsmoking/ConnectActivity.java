package com.example.stopsmoking;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stopsmoking.data.UserData;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
public class ConnectActivity extends AppCompatActivity {
    Button connectBtn;
    Button startMeasureBtn;
    BluetoothSPP bt;
    UserData user;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        bt= new BluetoothSPP(this);
        mContext =this;
        connectBtn = (Button) findViewById(R.id.btn_connect_device);
        startMeasureBtn = (Button) findViewById(R.id.btn_start_measure);

        Intent intentData = getIntent();
        user = (UserData) intentData.getParcelableExtra("OBJECT");

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

//        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
//            public void onDataReceived(byte[] data, String message) {
//                Toast.makeText(ConnectActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() { // 블루투스 연결
            @Override
            public void onClick(View v) {
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.disconnect();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    public void setup() {
        startMeasureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
//                    Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);
//                    intent.putExtra("OBJECT",user);
//                    startActivity(intent);
//                }
//                else{
//                    Toast.makeText(getApplicationContext()
//                            , "측정 기기를 연결하세요", Toast.LENGTH_SHORT).show();
//                }
                Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);
                intent.putExtra("OBJECT",user);
                startActivity(intent);

            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
