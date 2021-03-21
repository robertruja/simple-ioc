package test;

import org.crumbs.core.annotation.Crumb;
import org.crumbs.core.annotation.CrumbRef;

@Crumb
public class Crumb1 {

    @CrumbRef
    private Crumb2 crumb2;

    public void testCall() {
        System.out.println("Called Crumb 1 test method");
    }

    public void callCrumb2() {
        crumb2.testCall();
    }
}
