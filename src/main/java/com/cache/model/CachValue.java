package com.cache.model;

import com.cache.model.entity.ObjectKeyValue;
import org.springframework.data.repository.NoRepositoryBean;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

@NoRepositoryBean
public class CachValue {
    private static int count;
    private static int maxCount = 10;
    private Map<String, WeakReference<Node>> youngMap = new HashMap<String, WeakReference<Node>>();
    private Map<String, SoftReference<Node>> oldMap = new HashMap<String, SoftReference<Node>>();
    public static CachValue cash = new CachValue();


    protected class Node<T> {
        public Node(T value) {
            this.value = value;
        }

        T value;
        int count;

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", count=" + count +
                    '}';
        }
    }

    private CachValue() {
    }

    public synchronized ObjectKeyValue put(ObjectKeyValue ob) {
        count++;
        if (count > maxCount) {
            new Thread(new TransferenceStream(youngMap, oldMap)).start();
            count = 0;
        }
        String key = ob.getKeyMy();
        WeakReference<Node> value = new WeakReference<Node>(new Node<String>(ob.getValue()));
        if (youngMap.get(key) == null) {
            youngMap.put(key, value);
        } else {
            int count = youngMap.get(key).get().count;
            count++;
            value.get().count = count;
            youngMap.put(key, value);

        }
        System.out.println(youngMap.get(key).get().count);
        return ob;

    }

    public synchronized ObjectKeyValue get(String key) {
        count++;
        if (count > maxCount) {
            count = 0;
            new Thread(new TransferenceStream(youngMap, oldMap)).start();
        }
        ObjectKeyValue ob = new ObjectKeyValue();
        ob.setKeyMy(key);
        WeakReference<Node> nodeYoung = youngMap.get(key);
        SoftReference<Node> nodeOld = oldMap.get(key);

        if (nodeYoung == null && nodeOld == null) {
            ob.setKeyMy(key);
            System.out.println(ob);
        } else if (nodeYoung != null) {
            maximizeCountForNode(nodeYoung, ob, key);
        } else {
            maximizeCountForNode(nodeOld, ob, key);
        }
        return ob;

    }

    public void maximizeCountForNode(Reference<Node> node, ObjectKeyValue ob, String key) {
        int count = node.get().count;
        count++;
        node.get().count = count;
        if (node.getClass().equals(SoftReference.class)) {
            oldMap.put(key, (SoftReference) node);
        } else {
            youngMap.put(key, (WeakReference<Node>) node);
        }
        ob.setValue((String) node.get().value);
        System.out.println(node.get());
    }


}
