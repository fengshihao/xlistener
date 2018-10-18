package com.fengshihao.example.xlistener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler mMainHandler = new Handler(Looper.getMainLooper());
        if (mMainHandler != null) {
            mMainHandler.post(() -> {

            });
        }

        GenerateTest test = new GenerateTest();
        test.testCameraListenerList();
    }
}
