package com.cache.model;

import com.cache.model.entity.ObjectKeyValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CachValue {
    private static Logger logger = LogManager.getLogger(CachValue.class);

    private static volatile int count;
    private static int maxCount = 10;
    private static int max = 10;

   protected static ConcurrentHashMap<String, WeakReference<Node>> youngMap = new ConcurrentHashMap<String, WeakReference<Node>>();

    protected static ConcurrentHashMap<String, SoftReference<Node>> oldMap = new ConcurrentHashMap<String, SoftReference<Node>>();


    public class Node<T> {
        public Node(T value) {
            this.value = value;
        }

        public T value;
        public int count;

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", count=" + count +
                    '}';
        }
    }

    public CachValue() {

    }

    public ObjectKeyValue put(ObjectKeyValue ob) {
        count++;


        String key = ob.getKeyMy();
        WeakReference<Node> valueEnteredYoung = new WeakReference<Node>(new Node<String>(ob.getValue()));
        SoftReference<Node> valueEnteredOld = new SoftReference<>(new Node<String>(ob.getValue()));
        WeakReference<Node> valueYoung = youngMap.get(key);
        SoftReference<Node> valueOld = oldMap.get(key);

        if (valueYoung == null && valueOld == null) {
            System.out.println("value2 == " + valueYoung);
            youngMap.put(key, valueEnteredYoung);
            logger.debug("method Put object=" + youngMap.get(key).get() + "by key empty in youngMap and oldMap");
        } else if (valueYoung != null) {
            if (valueYoung.get() != null) {

                // Node prev = ;
                int count = youngMap.get(key).get().count;
                count++;
                valueEnteredYoung.get().count = count;
                youngMap.put(key, valueEnteredYoung);
                logger.debug("method Put object=" + ob + "by key have in youngMap");
            } else {
                System.out.println("Remove  value2.get() != null " + key + " =ккк " + valueYoung.get());
                youngMap.remove(key);
                logger.debug("method Put object=" + ob + "by key have  in youngMap but empty value .This key" + key + " will  delete");
            }
        } else {
            if (valueOld.get() != null) {


                int count = oldMap.get(key).get().count;
                count++;
                valueEnteredOld.get().count = count;
                oldMap.put(key, valueEnteredOld);
                logger.debug("method Put object=" + ob + "by key have in oldMap" + oldMap);
            } else {
                System.out.println("Remove  value2.get() != null " + key + " = " + valueOld.get() != null);
                oldMap.remove(key);
                logger.debug("method Put object=" + ob + "by key have  in oldMap but empty value .This key" + key + " will  delete");
            }
        }


        return ob;

    }

    public ObjectKeyValue get(String key) {
        ++count;
        System.out.println("count="+count);
        if (count > maxCount) {
            count = 0;
           transferFromYoungToOld() ;
        }

        WeakReference<Node> nodeYoung = youngMap.get(key);
        SoftReference<Node> nodeOld = oldMap.get(key);

        ObjectKeyValue ob = new ObjectKeyValue();
        ob.setKeyMy(key);

        if (nodeYoung == null && nodeOld == null) {


        } else if (nodeYoung != null) {
            maximizeCountForNode(nodeYoung, ob, key);
        } else {
            maximizeCountForNode(nodeOld, ob, key);
        }

        return ob;

    }

private void transferFromYoungToOld(){
    logger.info("start with youngMap.size()= " + youngMap.size() + " oldMap.size()=" + oldMap.size());
    logger.debug("start with youngMap= " + youngMap + " oldMap=" + oldMap);
    for (ConcurrentHashMap.Entry<String, WeakReference<Node>> entry : youngMap.entrySet()) {
        System.out.println(entry.getValue().get()==null);

        if (entry.getValue() != null && entry.getValue().get() != null) {
            System.out.println(entry.getValue().get().count);
            if (entry.getValue().get().count > max) {
                oldMap.put(entry.getKey(), new SoftReference<Node>(entry.getValue().get()));
                youngMap.remove(entry.getKey());
            }
        } else {

            youngMap.remove(entry.getKey());
        }
    }
    logger.info("finish with youngMap.size()= " + youngMap.size() + " oldMap.size()=" + oldMap.size());
    logger.debug("finish with youngMap= " + youngMap + " oldMap=" + oldMap);

}
    private void maximizeCountForNode(Reference<Node> node, ObjectKeyValue ob, String key) {


        if (node.get() != null) {
            if (node.getClass().equals(SoftReference.class)) {
                int count = node.get().count;
                count++;
                node.get().count = count;
                oldMap.put(key, (SoftReference) node);
                ob.setValue((String) node.get().value);
            } else {
                int count = node.get().count;
                count++;
                node.get().count = count;
                youngMap.put(key, (WeakReference<Node>) node);
            }
            ob.setValue((String) node.get().value);
        } else if (node.getClass().equals(SoftReference.class)) {
            oldMap.remove(key);
        } else {
            youngMap.remove(key);
        }


    }


}
