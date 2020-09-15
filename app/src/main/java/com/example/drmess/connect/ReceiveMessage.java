package com.example.drmess.connect;

import android.os.AsyncTask;

import com.example.drmess.doubleratchet.User;

public class ReceiveMessage extends AsyncTask<User, Void, String> {

    public interface ReceiveMessageInterface{
        public void receivedMessage(String receivedMessage);
    }

    private ReceiveMessageInterface listener;

    public ReceiveMessage() {
        this.listener = null;
    }

    @Override
    protected String doInBackground(User... users) {

        User user = users[0];
        String message = new String(user.receivingMessage());
        //listener.receivedMessage(message);
        return message;
    }

    public void setReceiveMessageListener(ReceiveMessageInterface listener){
        this.listener = listener;
    }

}
