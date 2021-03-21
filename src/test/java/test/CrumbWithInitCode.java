package test;

import org.crumbs.core.annotation.Crumb;
import org.crumbs.core.annotation.CrumbInit;

@Crumb
public class CrumbWithInitCode {

    @CrumbInit
    private void init() {
        System.out.println("Called init code for crumb with init code");
    }
}
