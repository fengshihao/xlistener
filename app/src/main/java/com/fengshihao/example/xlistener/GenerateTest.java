package com.fengshihao.example.xlistener;

import android.graphics.Camera;
import android.util.Log;

import com.fengshihao.xlistener.XListener;

import java.nio.Buffer;
@XListener(notifyOnMainThread = true)
interface TestListener {
    default void onX(int x) {}
    default void onY(int x, float y, String cc, Buffer bf) {}
}


@XListener
interface CameraListener {
    default void onOpen(Camera camera, int open) {};
    default void onClosed() {};
    default void hasEvent(int event, String error) {};
}

/**
 * Created by fengshihao on 18-7-8.
 */

public class GenerateTest {
    private static final String TAG = "GenerateTest";

    public void testCameraListenerList() {
        CameraListenerList l = new CameraListenerList();
        l.addListener(new CameraListener() {
            @Override
            public void onOpen(Camera camera, int open) {
                Log.d(TAG, "onOpen() called with: camera = [" + camera + "], open = [" + open + "]");
            }

            @Override
            public void onClosed() {
                Log.d(TAG, "onClosed() called");
            }

            @Override
            public void hasEvent(int event, String error) {
                Log.d(TAG, "hasEvent() called with: event = [" + event + "], error = [" + error + "]");
            }
        });

        l.onClosed();
        l.onOpen(null, 10);


        TestListenerList t = new TestListenerList();
        t.attachToMainThread();
        t.addListener(new TestListener() {
            @Override
            public void onX(int x) {
                Log.d(TAG, "onX() called with: x = [" + x + "]");
            }
        });
    }
}
