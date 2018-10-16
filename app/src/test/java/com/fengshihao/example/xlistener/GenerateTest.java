package com.fengshihao.example.xlistener;

import android.graphics.Camera;

import com.fengshihao.xlistenerprocessor.GenerateNotifier;

import java.nio.Buffer;


@GenerateNotifier(notifyOnMainThread = true)
interface TestListener {
    default void onX(int x) {}
    default void onY(int x, float y, String cc, Buffer bf) {};
}


@GenerateNotifier
interface CameraListener {
    default void onOpen(Camera camera, int open) {};
    default void onClosed() {};
    default void hasEvent(int event, String error) {};
}

/**
 * Created by fengshihao on 18-7-8.
 */

public class GenerateTest {
    public void testCameraListenerList() {
        CameraListenerList l = new CameraListenerList();
        l.addListener(new CameraListener() {
            @Override
            public void onOpen(Camera camera, int open) {
                log( "onOpen() called with: camera = [" + camera + "], open = [" + open + "]");
            }

            @Override
            public void onClosed() {
                log( "onClosed() called");
            }

            @Override
            public void hasEvent(int event, String error) {
                log( "hasEvent() called with: event = [" + event + "], error = [" + error + "]");
            }
        });

        l.onClosed();
        l.onOpen(null, 10);
    }

    private void log(String s) {
        System.out.println(s);
    }
}
