package exporter.mifit.com.mifitexporter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        /*Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("exporter.mifit.com.mifitexporter", appContext.getPackageName());*/
        WeatherApi.getAirTempForTimestamp(System.currentTimeMillis(),53.356,-6.9574);
    }

    @Test
    public void test2(){
        WeatherApi.getAirTempForTimestamp(System.currentTimeMillis(),53.356,-6.9574);
    }
}
