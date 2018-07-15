package com.cache.model;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;

public class TransferenceStream implements Runnable {
    private Map<String, WeakReference<CachValue.Node>> youngMap;
    private Map<String, SoftReference<CachValue.Node>> oldMap;
    private static int max = 10;

    public TransferenceStream(Map<String, WeakReference<CachValue.Node>> youngMap, Map<String, SoftReference<CachValue.Node>> oldMap) {
        this.youngMap = youngMap;
        this.oldMap = oldMap;
    }

    @Override
    public void run() {
        for (Map.Entry<String, WeakReference<CachValue.Node>> entry : youngMap.entrySet()) {
            if (entry.getValue().get().count > max) {
                oldMap.put(entry.getKey(), new SoftReference<CachValue.Node>(entry.getValue().get()));
                youngMap.remove(entry.getKey());
            }
        }
        System.out.println(oldMap);
    }


}
