package com.example.drmess.connect;

import android.os.AsyncTask;

import com.example.drmess.doubleratchet.ConnectHandler;
import com.example.drmess.doubleratchet.User;

import java.net.ServerSocket;
import java.net.Socket;

public class ExchangeMasterKeys extends AsyncTask<Boolean, Void, User> {

    ConnectHandler connectHandler;

    @Override
    protected User doInBackground(Boolean... booleans) {

        Boolean isAlice = booleans[0];
        User user = new User();
        user.initializeMasterKeyExchange(isAlice);
        if(isAlice){
            user.messageToSend = "InitialMessage";
            user.sendMessage();
            user.receivedMessage = new String(user.receivingMessage());
        } else {
            user.receivedMessage = new String(user.receivingMessage());
            user.messageToSend = "InitialMessage";
            user.sendMessage();
        }



        return user;
    }
}
