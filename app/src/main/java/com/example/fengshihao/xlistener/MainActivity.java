package com.example.fengshihao.xlistener;

import android.app.Activity;
import android.graphics.Camera;
import android.os.Bundle;

import com.fengshihao.xlistener.XListener;
import com.fengshihao.xlistenerprocessor.GenerateNotifier;

import io.reactivex.annotations.NonNull;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
