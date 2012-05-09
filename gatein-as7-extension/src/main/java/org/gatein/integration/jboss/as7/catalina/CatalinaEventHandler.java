/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.integration.jboss.as7.catalina;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.catalina.Container;
import org.apache.catalina.Engine;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.gatein.wci.jboss.ServletContainerContextHelper;
import org.gatein.wci.jboss.JB7ServletContainerContext;

/**
 * @author <a href="mailto:ken@kenfinnigan.me">Ken Finnigan</a>
 */
public class CatalinaEventHandler implements CatalinaEvents {

    protected final Server server;
    protected JB7ServletContainerContext containerContext;

    // Flags used to ignore redundant or invalid events
    protected final AtomicBoolean init = new AtomicBoolean(false);

    public CatalinaEventHandler(Server server) {
        this.server = server;
    }

    /**
     * @see org.gatein.integration.jboss.as7.catalina.CatalinaEvents#start()
     */
    @Override
    public void start() {
        if (!(server instanceof Lifecycle)) {
            throw new IllegalStateException();
        }

        Lifecycle lifecycle = (Lifecycle) server;

        if (!this.containsListener(lifecycle)) {
            Container container = server.findServices()[0].getContainer();
            containerContext = new JB7ServletContainerContext((Engine) container);
            ServletContainerContextHelper.callServletContainerContextStart(containerContext);
            lifecycle.addLifecycleListener(containerContext);
        }

        if (this.init.compareAndSet(false, true)) {
            this.init(server);
        }
    }

    /**
     * @see org.gatein.integration.jboss.as7.catalina.CatalinaEvents#stop()
     */
    @Override
    public void stop() {
        if (!(server instanceof Lifecycle))
            throw new IllegalStateException();

        Lifecycle lifecycle = (Lifecycle) server;

        lifecycle.removeLifecycleListener(containerContext);

        if (this.init.compareAndSet(true, false)) {
            this.destroy(server);
        }
    }

    private boolean containsListener(Lifecycle lifecycle) {
        for (LifecycleListener listener : lifecycle.findLifecycleListeners()) {
            if (listener instanceof JB7ServletContainerContext) {
                return true;
            }
        }

        return false;
    }

    protected void init(Server server) {
        this.addListeners(server);
    }

    protected void destroy(Server server) {
        this.removeListeners(server);
    }

    private void addListeners(Server server) {
        // Register ourself as a listener for child services
        for (Service service : server.findServices()) {
            Container engine = service.getContainer();
            engine.addContainerListener(containerContext);
            ((Lifecycle) engine).addLifecycleListener(containerContext);

            for (Container host : engine.findChildren()) {
                host.addContainerListener(containerContext);

                for (Container context : host.findChildren()) {
                    ((Lifecycle) context).addLifecycleListener(containerContext);
                }
            }
        }
    }

    private void removeListeners(Server server) {
        // Unregister ourself as a listener to child components
        for (Service service : server.findServices()) {
            Container engine = service.getContainer();
            engine.removeContainerListener(containerContext);
            ((Lifecycle) engine).removeLifecycleListener(containerContext);

            for (Container host : engine.findChildren()) {
                host.removeContainerListener(containerContext);

                for (Container context : host.findChildren()) {
                    ((Lifecycle) context).removeLifecycleListener(containerContext);
                }
            }
        }
    }

}
