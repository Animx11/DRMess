package com.example.drmess.connect;

import android.os.AsyncTask;

import com.example.drmess.doubleratchet.ConnectHandler;

import java.net.ServerSocket;
import java.net.Socket;

public class WaitForConnect extends AsyncTask<String, Void, Void> {

    public interface WaitForConnectInterface{
        public void onConnect();
    }

    private WaitForConnectInterface listener;

    public WaitForConnect() {
        this.listener = null;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try{

            ConnectHandler connectHandler = new ConnectHandler();
            ServerSocket serverSocket = new ServerSocket(6666);
            Socket socket = serverSocket.accept();
            connectHandler.setSocket(socket);
            listener.onConnect();
            //bob.initializeMasterKeyExchange(false);


        } catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    public void setWaitForConnectListener(WaitForConnectInterface listener) {

        this.listener = listener;

    }

}
