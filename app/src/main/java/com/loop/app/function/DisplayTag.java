package com.loop.app.function;

import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.loop.app.MainActivity;

public class DisplayTag {
    public static void displayTag(String s, TextView textView) {
        String tag = s;

        for(int i=0,delay=300;i<15;i++,delay+=100){
            char c = tag.charAt(i);
            if (i == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(c + "");
                    }
                }, delay);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.append(c + "");
                    }
                }, delay);

                if (i == tag.length()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Intent intent = new Intent(MainActivity.class, DisplayTag.class);
//                            startActivity(intent);
//                            finish()
                            Toast.makeText(textView.getContext(), "Completed", Toast.LENGTH_SHORT).show();
                        }
                    }, delay + 800);
                }
            }
        }
    }
}
