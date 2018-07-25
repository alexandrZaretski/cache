package com.cache.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class TransferenceStream implements Runnable {
    private static Logger logger = LogManager.getLogger(TransferenceStream.class);

    private ConcurrentHashMap<String, WeakReference<CachValue.Node>> youngMap = CachValue.youngMap;


    private ConcurrentHashMap<String, SoftReference<CachValue.Node>> oldMap = CachValue.oldMap;


    private static int max = 10;

    public TransferenceStream() {


    }

    @Override
    public void run() {

        logger.info("start with youngMap.size()= " + youngMap.size() + " oldMap.size()=" + oldMap.size());
        logger.debug("start with youngMap= " + youngMap + " oldMap=" + oldMap);
        for (ConcurrentHashMap.Entry<String, WeakReference<CachValue.Node>> entry : youngMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().get() != null) {
                if (entry.getValue().get().count > max) {
                    oldMap.put(entry.getKey(), new SoftReference<CachValue.Node>(entry.getValue().get()));
                    youngMap.remove(entry.getKey());
                }
            } else {

                youngMap.remove(entry.getKey());
            }
        }
        logger.info("finish with youngMap.size()= " + youngMap.size() + " oldMap.size()=" + oldMap.size());
        logger.debug("finish with youngMap= " + youngMap + " oldMap=" + oldMap);


    }


}
