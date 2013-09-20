package org.jboss.arquillian.portal.warp;

import org.jboss.arquillian.warp.spi.WarpLifecycleTest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.METHOD)
@WarpLifecycleTest
public @interface AfterPortletPhase {
    Phase value();
}
