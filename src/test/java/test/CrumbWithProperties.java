package test;

import org.crumbs.core.annotation.Crumb;
import org.crumbs.core.annotation.Property;

import java.time.Duration;

@Crumb
public class CrumbWithProperties {

    @Property("some.string")
    private String someString;

    @Property("some.int")
    private Integer someInt;

    @Property("some.double")
    private Double someDouble;

    @Property("some.long")
    private Long someLong;

    @Property("some.boolean")
    private Boolean someBoolean;

    @Property("some.duration")
    private Duration someDuration;


    public String getSomeString() {
        return someString;
    }

    public Integer getSomeInt() {
        return someInt;
    }

    public Double getSomeDouble() {
        return someDouble;
    }

    public Long getSomeLong() {
        return someLong;
    }

    public Boolean getSomeBoolean() {
        return someBoolean;
    }

    public Duration getSomeDuration() {
        return someDuration;
    }
}
