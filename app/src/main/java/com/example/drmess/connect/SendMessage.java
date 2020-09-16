package com.example.drmess.connect;

import android.os.AsyncTask;

import com.example.drmess.doubleratchet.User;

public class SendMessage extends AsyncTask<User, Void, Void> {


    @Override
    protected Void doInBackground(User... users) {

        User user = users[0];
        user.sendMessage();
        System.out.println("Send");

        return null;
    }



}
