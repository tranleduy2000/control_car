package com.duy.controlcar;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Giao tiep bluetooth
 * Created by Duy on 19/7/2016
 */
public class Connector {
    private static final String TAG = Connector.class.getSimpleName();
    @NonNull
    public BluetoothSocket socket;
    @Nullable
    public OnServerListener onServerListener = null;
    @Nullable
    private OutputStream out;
    private AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    public Connector(@NonNull BluetoothSocket socket) {
        this.socket = socket;
        connect();
    }

    public boolean isConnect() {
        return socket.isConnected();
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    /**
     * Kết nối bluetooth với arduino
     */
    public void connect() {
        try {
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        new ServerListenerTask().execute();
//        if (onServerListener != null) {
//            onServerListener.onConnectChangeStatus(true);
//        }
    }

    /**
     * Ngắt kết nối bluetooth
     */
    public void disconnect() {
        try {
            atomicBoolean.set(false);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (onServerListener != null) {
            onServerListener.onConnectChangeStatus(false);
        }
    }

    public synchronized void sendMessage(String message) {
        Log.d(TAG, "sendMessage() called with: message = [" + message + "]");
        try {
            if (out != null) {
                out.write(message.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setOnServerListener(@Nullable OnServerListener onServerListener) {
        this.onServerListener = onServerListener;
    }

    public interface OnServerListener {
        void onConnectChangeStatus(boolean isConnect);

        void newMessageFromServer(String message);
    }

    private class ServerListenerTask extends AsyncTask<Void, String, Void> {
        private InputStream inputStream;
        private BufferedReader bufferedReader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            atomicBoolean.set(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground() called with: params = [" + Arrays.toString(params) + "]");

            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            StringBuilder msg = new StringBuilder();
            try {
                Log.d(TAG, "doInBackground");
                inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    publishProgress(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            try {
                inputStream.close();
                socket.close();
                if (onServerListener != null) {
                    onServerListener.onConnectChangeStatus(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "doInBackground: socket closed");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d(TAG, "onProgressUpdate() called with: values = [" + Arrays.toString(values) + "]");

            if (onServerListener != null) onServerListener.newMessageFromServer(values[0]);
        }
    }
}
