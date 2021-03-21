package test;

import org.crumbs.core.annotation.CrumbsApplication;
import org.crumbs.core.context.CrumbsApp;
import org.crumbs.core.context.CrumbsContext;
import org.crumbs.core.logging.Logger;
import org.junit.Test;

@CrumbsApplication
public class TestApp {

    private Logger logger = Logger.getLogger(TestApp.class);

    @Test
    public void shouldNotThrowNullPointer() {
        CrumbsContext context = CrumbsApp.run(TestApp.class);
        Crumb2 crumb2 = context.getCrumb(Crumb2.class);
        Crumb1 crumb1 = context.getCrumb(Crumb1.class);
        crumb2.callCrumb1();
        crumb1.callCrumb2();
    }

    @Test
    public void shouldInjectProperties() {
        CrumbsContext context = CrumbsApp.run(TestApp.class);

        CrumbWithProperties crumbWithProperties = context.getCrumb(CrumbWithProperties.class);

        try {
            throw new IllegalArgumentException("Some error");
        } catch (IllegalArgumentException ex) {
            logger.error("Something went wrong", ex);
        }

        logger.info("Something went wrong first {}, second {}, third missing {}", 1, 2, "aaa");

        logger.info(crumbWithProperties.getSomeString());
        logger.warn(crumbWithProperties.getSomeBoolean());
        logger.error(crumbWithProperties.getSomeDouble());
        logger.info(crumbWithProperties.getSomeDuration());
        logger.info(crumbWithProperties.getSomeLong());
        logger.debug("Should not be visible");
    }
}
