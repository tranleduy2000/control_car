package com.duy.controlcar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Duy on 21-May-17.
 */

public class MainActivity extends AppCompatActivity implements Connector.OnServerListener {
    public static final int REQUEST_CODE_CHOOSE_DEVICE = 1001;
    public static final UUID mUUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Nullable
    private Connector mConnector;
    private TextView txtStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bindView();
    }

    private void bindView() {
        txtStatus = (TextView) findViewById(R.id.txt_status);
        findViewById(R.id.img_up).setOnTouchListener(new CommandTouchListener(Protocol.MOVE_UP));
        findViewById(R.id.img_down).setOnTouchListener(new CommandTouchListener(Protocol.MOVE_DOWN));
        findViewById(R.id.img_left).setOnTouchListener(new CommandTouchListener(Protocol.MOVE_LEFT));
        findViewById(R.id.img_right).setOnTouchListener(new CommandTouchListener(Protocol.MOVE_RIGHT));
    }

    public void doConnect(View view) {
        Intent intent = new Intent(this, ChooseDeviceActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_DEVICE);
    }

    public void sendCommand(String cmd) {
        if (mConnector != null && mConnector.isConnect()) {
            mConnector.sendMessage(cmd);
        } else {
            msg(getString(R.string.not_connect));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_DEVICE:
                switch (resultCode) {
                    case RESULT_OK:
                        String deviceID = data.getStringExtra("data");
                        new ConnectBluetoothTask(this).execute(deviceID);
                        break;
                }
                break;
        }
    }

    public void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectChangeStatus(boolean isConnect) {
        txtStatus.setText(isConnect ? getString(R.string.connected) : getString(R.string.not_connect));
    }

    @Override
    public void newMessageFromServer(String message) {
        // TODO: 21-May-17  received data
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnector != null) {
            mConnector.disconnect();
        }
    }

    private class CommandTouchListener implements View.OnTouchListener {
        private String cmd;

        CommandTouchListener(String cmd) {
            this.cmd = cmd;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "onTouch() called with: v = [" + v + "], event = [" + event + "]");

                sendCommand(cmd);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                Log.d(TAG, "onTouch() called with: v = [" + v + "], event = [" + event + "]");

                sendCommand(Protocol.STOP);
                return true;
            }
            return false;
        }
    }

    public class ConnectBluetoothTask extends AsyncTask<String, Void, BluetoothSocket> {
        private BluetoothSocket bluetoothSocket;

        public ConnectBluetoothTask(Context context) {
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtStatus.setText(getString(R.string.connecting));
        }

        @Override
        protected BluetoothSocket doInBackground(String... params) {
            Log.d(TAG, "doInBackground() called with: params = [" + Arrays.toString(params) + "]");
            try {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(params[0]);
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(mUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                bluetoothSocket.connect();
                return bluetoothSocket;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(BluetoothSocket socket) {
            super.onPostExecute(socket);
            if (socket == null) {
                msg(getString(R.string.text_1));
                txtStatus.setText(R.string.not_connect);
            } else {
                if (mConnector == null) {
                    mConnector = new Connector(socket);
                    mConnector.setOnServerListener(MainActivity.this);
                } else {
                    mConnector.setSocket(socket);
                    mConnector.connect();
                }
                msg(getString(R.string.connected));
                txtStatus.setText(R.string.dis_connect);
            }
        }


    }
}
