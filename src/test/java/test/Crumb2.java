package test;

import org.crumbs.core.annotation.Crumb;
import org.crumbs.core.annotation.CrumbRef;

@Crumb
public class Crumb2 {

    @CrumbRef
    private Crumb1 crumb1;

    public void testCall() {
        System.out.println("Called Crumb 2 test method");
    }

    public void callCrumb1() {
        crumb1.testCall();
    }
}
