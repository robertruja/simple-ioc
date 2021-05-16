package test;

import org.crumbs.core.context.ConfigLoader;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TestConfRef {

    @Test
    public void TestConfRef() {
        Map<String, String> props = ConfigLoader.loadProperties();

        assertEquals("prefix_some-test_suffix", props.get("test.ref"));
    }

    @Test
    public void shouldReplacePropFromEnv() {
        System.setProperty("test.some", "sys-some-test");

        Map<String, String> props = ConfigLoader.loadProperties();

        assertEquals("prefix_sys-some-test_suffix", props.get("test.ref"));
    }

    @Test
    public void TestConfRefNoSuffix() {
        Map<String, String> props = ConfigLoader.loadProperties();

        assertEquals("prefixsome-test", props.get("test.ref2"));
    }

    @Test
    public void TestConfPropertiesFromSystem() {
        System.setProperty("non.existent", "test");
        Map<String, String> props = ConfigLoader.loadProperties();

        assertEquals("prefixsome-test", props.get("test.ref2"));
        assertEquals("test", props.get("non.existent"));
    }
}
