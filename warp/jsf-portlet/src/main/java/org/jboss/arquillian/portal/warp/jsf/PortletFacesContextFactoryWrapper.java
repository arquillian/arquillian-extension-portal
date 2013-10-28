package org.jboss.arquillian.portal.warp.jsf;

import org.jboss.arquillian.warp.jsf.FacesContextFactoryWrapper;
import org.jboss.arquillian.warp.jsf.FacesContextInitialized;
import org.jboss.arquillian.warp.spi.LifecycleManager;
import org.jboss.arquillian.warp.spi.LifecycleManagerStore;
import org.jboss.arquillian.warp.spi.exception.ObjectAlreadyAssociatedException;
import org.jboss.arquillian.warp.spi.exception.ObjectNotAssociatedException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContextWrapper;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.PortletRequest;
import java.util.logging.Logger;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PortletFacesContextFactoryWrapper extends FacesContextFactory {

    private Logger log = PortalWarpJSFCommons.LOG;

    private FacesContextFactory delegate;

    public PortletFacesContextFactoryWrapper(FacesContextFactory facesContextFactory) {
        delegate = facesContextFactory;
    }

    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
            throws FacesException {

        FacesContext facesContext = new WrappedFacesContext(delegate.getFacesContext(context, request, response, lifecycle));

        if (request instanceof PortletRequest) {
            PortletRequest portletReq = (PortletRequest) request;

            facesContext.getAttributes().put(FacesContextFactoryWrapper.WARP_ENABLED, Boolean.FALSE);

            try {
                LifecycleManager manager = LifecycleManagerStore.get(PortletRequest.class, portletReq);

                manager.bindTo(FacesContext.class, facesContext);
                facesContext.getAttributes().put(FacesContextFactoryWrapper.WARP_ENABLED, Boolean.TRUE);

                manager.fireEvent(new FacesContextInitialized(facesContext));
            } catch (ObjectNotAssociatedException e) {
                log.fine("no association of manager found for this PortletRequest");
            } catch (ObjectAlreadyAssociatedException e) {
                throw new IllegalStateException(e);
            }
        }

        return facesContext;
    }

    public class WrappedFacesContext extends FacesContextWrapper {

        private FacesContext wrapped;

        public WrappedFacesContext(FacesContext wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public FacesContext getWrapped() {
            return wrapped;
        }

        @Override
        public void release() {
            try {
                if ((Boolean) this.getAttributes().remove(FacesContextFactoryWrapper.WARP_ENABLED)) {
                    LifecycleManager manager = LifecycleManagerStore.get(FacesContext.class, this);

                    manager.unbindFrom(FacesContext.class, this);
                }
            } catch (ObjectNotAssociatedException e) {
                throw new IllegalStateException(e);
            } finally {
                super.release();
            }
        }

    }
}
