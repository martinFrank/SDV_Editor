package com.github.martinfrank.sdveditor;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Console {

    private final TextView textView;

    public Console(TextView textView){
        this.textView = textView;
    }

    public void print(String text) {
        String newLine  = lineEntry(text) + textView.getText();
        textView.setText(newLine);
    }

    private String lineEntry(String text) {
        return "["+timeStamp()+"] "+text+"\n";
    }

    private String timeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return simpleDateFormat.format(calendar.getTime());
    }
}
