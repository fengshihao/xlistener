package com.fengshihao.example.xlistener;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengshihao on 18-10-14.
 */

public class CameraListenerListTemplate implements CameraListener {
    private List<CameraListener> mListeners = new ArrayList<>();
    private static final String TAG = "CameraListenerList";
    private Handler mHandler;

    public void attachToCurrentThread() {
        if (Looper.myLooper() == null) {
            Log.e(TAG, "CameraListenerListTemplate: this thread do not has looper!");
            return;
        }
        mHandler = new Handler(Looper.myLooper());
    }

    public void attachToMainThread() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    private boolean isRightThread() {
        return mHandler.getLooper() == Looper.myLooper();
    }

    @Override
    public void onClosed () {
        for (CameraListener l: mListeners) {
            l.onClosed();
        }
    }

    @Override
    public void onOpen (android.graphics.Camera camera, int open) {
        if (mHandler == null || isRightThread()) {
            for (CameraListener l: mListeners) {
                l.onOpen(camera,open);
            }
        } else {
            mHandler.post(() -> {
                for (CameraListener l: mListeners) {
                    l.onOpen(camera,open);
                }
            });
        }
    }

    @Override
    public void hasEvent (int event, java.lang.String error) {
        for (CameraListener l: mListeners) {
            l.hasEvent(event,error);
        }
    }
    public void addListener(CameraListener listener) {
        if (listener == null) {
            Log.e(TAG, "addListener: wrong arg null");
            return;
        }
        if (mListeners.contains(listener)) {
            Log.e(TAG, "addListener: already in " + listener);
            return;
        }
        mListeners.add(listener);
        Log.d(TAG, "addListener: now has listener=" + mListeners.size());
    }

    public CameraListener removeListener(CameraListener listener) {
        if (listener == null) {
            Log.w(TAG, "removeListener: wrong arg null");
            return null;
        }
        if (mListeners.isEmpty()) {
            return null;
        }
        int idx = mListeners.indexOf(listener);
        if (idx == -1) {
            Log.e(TAG, "removeListener: did not find this listener " + listener);
            return null;
        }
        CameraListener r = mListeners.remove(idx);
        Log.d(TAG, "removeListener: now has listener=" + mListeners.size());
        return r;
    }


    public void clean() {
        Log.d(TAG, "clean() called");
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
        }
        mListeners.clear();
    }
}
