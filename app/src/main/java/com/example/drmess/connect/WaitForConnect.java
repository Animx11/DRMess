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
    ConnectHandler connectHandler;
    ServerSocket serverSocket;
    Socket socket;

    public WaitForConnect() {
        this.listener = null;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try{

            connectHandler = new ConnectHandler();
            serverSocket = new ServerSocket(6666);
            socket = serverSocket.accept();
            connectHandler.setSocket(socket);
            listener.onConnect();


        } catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    public void setWaitForConnectListener(WaitForConnectInterface listener) {

        this.listener = listener;

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        connectHandler = null;
        socket = null;
        serverSocket = null;

    }
}
