package com.example.drmess.connect;

import android.os.AsyncTask;

import com.example.drmess.doubleratchet.ConnectHandler;
import com.example.drmess.doubleratchet.TypeConverter;
import com.example.drmess.doubleratchet.User;

import java.net.Socket;

public class ConnectWithPhone extends AsyncTask<String, Void, User> {


    public interface ConnectWithPhoneInterface{
        public void onConnect();
    }

    private ConnectWithPhoneInterface listener;

    public ConnectWithPhone() {
        this.listener = null;
    }

    @Override
    protected User doInBackground(String... strings) {

        try{
/*
            User alice = new User();
            Connect connect = new Connect();

            System.out.println("Click");

            String ip = strings[0];

            connect.connect(ip, 6666);

            alice.initializeMasterKeyExchange(true, connect);
            //alice.sendMessage("TestMessage");
*/

            TypeConverter typeConverter = new TypeConverter();
            String ip = strings[0];
            ConnectHandler connectHandler = new ConnectHandler();
            Socket socket = new Socket(ip, 6666);
            connectHandler.setSocket(socket);
            listener.onConnect();
            //alice.initializeMasterKeyExchange(true);

            return null;

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void setConnectWithPhoneListener(ConnectWithPhoneInterface listener) {

        this.listener = listener;

    }

}
