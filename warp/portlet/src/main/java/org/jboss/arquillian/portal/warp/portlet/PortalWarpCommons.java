/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.portal.warp.portlet;

import org.jboss.arquillian.portal.api.PortalTest;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public final class PortalWarpCommons {

    private PortalWarpCommons() {
        // Hide default constructor
    }

    /**
     * Checks whether either given class or its superclasses are annoated with {@link PortalTest} annotation indicating that the
     * Warp is used in the test.
     */
    public static boolean isPortalTest(Class<?> testClass) {
        Class<?> clazz = testClass;
        while (clazz != null) {
            if (clazz.isAnnotationPresent(PortalTest.class)) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }
}
