package com.fengshihao.example.xlistener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengshihao on 18-10-14.
 */

public class CameraListenerListTemplate implements CameraListener {
    private List<CameraListener> mListeners = new ArrayList<>();
    private String logTag = "CameraListenerList";

    @Override
    public void onClosed () {
        for (CameraListener l: mListeners) {
            l.onClosed();
        }
    }

    @Override
    public void onOpen (android.graphics.Camera camera, int open) {
        for (CameraListener l: mListeners) {
            l.onOpen(camera,open);
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
            loge("addListener: wrong arg null");
            return;
        }
        if (mListeners.contains(listener)) {
            loge("addListener: already in " + listener);
            return;
        }
        mListeners.add(listener);
        log("addListener: now has listener=" + mListeners.size());
    }

    public CameraListener removeListener(CameraListener listener) {
        if (listener == null) {
            loge("removeListener: wrong arg null");
            return null;
        }
        if (mListeners.isEmpty()) {
            return null;
        }
        int idx = mListeners.indexOf(listener);
        if (idx == -1) {
            loge("removeListener: did not find this listener " + listener);
            return null;
        }
        CameraListener r = mListeners.remove(idx);
        log("removeListener: now has listener=" + mListeners.size());
        return r;
    }


    public void clean() {
        log("clean() called");
        mListeners.clear();
    }

    public List<CameraListener> getListeners() {
        return mListeners;
    }

    private void log(String info) {
        System.out.println(logTag + " " + info);
    }

    private void loge(String info) {
        System.err.println(logTag + " " + info);
    }
    
}
