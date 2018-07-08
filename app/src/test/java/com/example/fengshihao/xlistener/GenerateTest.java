package com.example.fengshihao.xlistener;

import android.graphics.Camera;
import android.util.Log;

import com.fengshihao.xlistener.XListener;
import com.fengshihao.xlistenerprocessor.GenerateNotifier;

import java.nio.Buffer;

import io.reactivex.annotations.NonNull;


@GenerateNotifier
interface TestListener {
    default void onX(int x) {}
    default void onY(int x, float y, String cc, Buffer bf) {};
}


@GenerateNotifier
interface CameraListener {
    default void onOpen(Camera camera, int open) {};
    default void onClosed() {};
    default void hasEvent(int event, @NonNull String error) {};
}

/**
 * Created by fengshihao on 18-7-8.
 */

public class GenerateTest {
    XListener<CameraListener> mCameraListeners = new XListener<>(CameraListener.class.getSimpleName());

    XListener<TestListener> mTestListeners = new XListener<>(TestListener.class.getSimpleName());


    public void testCameraListener() {
        mCameraListeners.addListener(new CameraListener() {
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
        CameraListenerNotifier.notifyHasEvent(mCameraListeners, 5, "hello");
        CameraListenerNotifier.notifyOnClosed(mCameraListeners);
        CameraListenerNotifier.notifyOnOpen(mCameraListeners, null, 10);
        mCameraListeners.clean();
    }


    public void testListener() {
        mTestListeners.addListener(new TestListener() {
            @Override
            public void onX(int x) {
                log("onX() called with: x = [" + x + "]");
            }
        });
        TestListenerNotifier.notifyOnX(mTestListeners, 100);
        TestListenerNotifier.notifyOnY(mTestListeners, 19, 2, "hello", null);

        mCameraListeners.clean();
    }

    private void log(String s) {
        System.out.println(s);
    }
}
