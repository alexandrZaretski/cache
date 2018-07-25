package cachTest;

import com.cache.model.CachValue;
import com.cache.model.entity.ObjectKeyValue;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;


import static org.hamcrest.MatcherAssert.assertThat;
@RunWith(JUnit4.class)
public class cachTest {


    CachValue cach = new CachValue();

   private Field field = CachValue.class.getDeclaredField("youngMap");
    private Field field2 = CachValue.class.getDeclaredField("oldMap");

    private static ConcurrentHashMap<String, WeakReference<CachValue.Node>> youngMap ;
    private static ConcurrentHashMap<String, SoftReference<CachValue.Node>> oldMap;

    public cachTest() throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        youngMap = (ConcurrentHashMap<String, WeakReference<CachValue.Node>>) field.get(null);
        field2.setAccessible(true);
        oldMap  = (ConcurrentHashMap<String, SoftReference<CachValue.Node>>) field2.get(null);
    }


@Test
    public void testCorrectValue() throws InterruptedException {
        for(int i =0 ;i<3;i++){
            ObjectKeyValue ob = new ObjectKeyValue();
            ob.setValue("value"+i);
            ob.setKeyMy("key"+i);
            System.out.println("object"+ob);
            cach.put(ob );
        }
assertThat(youngMap.size(),CoreMatchers.is(3));

    for(int i =0  ;i<19;i++){

        cach.get( "key"+1);

    }

    assertThat(youngMap.size(),CoreMatchers.is(2));
    assertThat(oldMap.size(),CoreMatchers.is(1));
    youngMap.clear();
    oldMap.clear();
    }
    @Test
    public void testCorrectHurd() throws InterruptedException {
    for(int i=0;i<1000;i++){
        new Thread(new Runnable() {

            @Override
            public void run() {

                String key =String.valueOf((int)(Math.random()*10));
                String value=String.valueOf((int)(Math.random()*10));
                ObjectKeyValue ob = new ObjectKeyValue();
                ob.setValue(value);
                ob.setKeyMy(key);
               cach.put(ob );
                key =String.valueOf((int)(Math.random()*10));
                cach.get(key);
            }
        }).start();
    }

        youngMap.clear();
        oldMap.clear();

    }
}
