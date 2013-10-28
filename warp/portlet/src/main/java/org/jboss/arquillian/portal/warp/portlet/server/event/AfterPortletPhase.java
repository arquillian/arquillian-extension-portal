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
package org.jboss.arquillian.portal.warp.portlet.server.event;

import org.jboss.arquillian.portal.warp.Phase;
import org.jboss.arquillian.warp.spi.WarpLifecycleEvent;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The lifecycle event which comes with {@link org.jboss.arquillian.portal.warp.AfterPortletPhase} verification execution.
 * </p>
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class AfterPortletPhase extends WarpLifecycleEvent {

    private Phase phase;

    public AfterPortletPhase(Phase phase) {
        this.phase = phase;
    }

    @Override
    public List<Annotation> getQualifiers() {
        return Arrays.asList((Annotation) new org.jboss.arquillian.portal.warp.AfterPortletPhase() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return org.jboss.arquillian.portal.warp.AfterPortletPhase.class;
                    }

                    @Override
                    public Phase value() {
                        return phase;
                    }
                }
        );
    }
}
