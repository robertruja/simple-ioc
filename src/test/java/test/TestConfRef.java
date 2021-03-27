package test;

import org.crumbs.core.context.ConfigLoader;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TestConfRef {

    @Test
    public void TestConfRef() {
        Map<String, String> props = ConfigLoader.loadProperties();

        Assert.assertEquals("prefix_some-test_suffix", props.get("test.ref"));
    }

    @Test
    public void shouldReplacePropFromEnv() {
        System.setProperty("test.some", "sys-some-test");

        Map<String, String> props = ConfigLoader.loadProperties();

        Assert.assertEquals("prefix_sys-some-test_suffix", props.get("test.ref"));
    }

    @Test
    public void TestConfRefNoSuffix() {
        Map<String, String> props = ConfigLoader.loadProperties();

        Assert.assertEquals("prefixsome-test", props.get("test.ref2"));
    }
}
