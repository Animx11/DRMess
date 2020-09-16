package com.example.drmess.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;

import com.example.drmess.ChatActivity;
import com.example.drmess.R;
import com.example.drmess.doubleratchet.User;

import java.net.SocketException;
import java.util.Arrays;

public class ReceiveMessage extends AsyncTask<User, Void, String> {

    public interface ReceiveMessageInterface{
        public void receivedMessage(String message);
    }

    private ReceiveMessageInterface listener;
    Handler mHandler = new Handler();

    public ReceiveMessage() {
        this.listener = null;
    }

    @Override
    protected String doInBackground(User... users) {

        User user = users[0];
        String message = new String(user.receivingMessage());

        if(message.compareTo(new String("".getBytes())) == 0){
            message = "11ErrorSocket11";
        }

        return message;
    }

    public void setReceiveMessageListener(ReceiveMessageInterface listener){
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        listener.receivedMessage(message);

    }
}
