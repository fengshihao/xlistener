package com.fengshihao.example.xlistener;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.fengshihao.xlistener.XListener;

@XListener
interface TestListener {
    default void onX(int x) {}
    default void onY(int x, float y, String z) {}
    void onZ();
}


/**
 * Created by fengshihao on 18-7-8.
 */

public class GenerateTest {
    private static final String TAG = "GenerateTest";

    HandlerThread mWorkThread = new HandlerThread("WorkerThread");
    Handler mWorkHandler;
    private void init() {
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
    }

    private void createListenerListAndCallMethods() {
        Log.d(TAG, "createListenerListAndCallMethods: thread=" + Thread.currentThread().getName());
        TestListenerList t = new TestListenerList();
        t.attachToMainThread();
        t.addListener(new TestListener() {
            @Override
            public void onX(int x) {
                Log.d(TAG, "onX() called with: x = [" + x + "] thread=" + Thread.currentThread().getName());
            }

            @Override
            public void onZ() {
                Log.d(TAG, "onZ() called on thread=" + Thread.currentThread().getName());
            }
        });

        t.onX(100);
        t.onZ();
    }

    public void run() {
        init();
        mWorkHandler.post(() -> createListenerListAndCallMethods());
    }
}
