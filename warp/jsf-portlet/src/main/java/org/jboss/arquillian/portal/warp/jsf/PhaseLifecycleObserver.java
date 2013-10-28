package org.jboss.arquillian.portal.warp.jsf;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.warp.jsf.PhaseLifecycleEvent;
import org.jboss.arquillian.warp.spi.LifecycleManager;
import org.jboss.arquillian.warp.spi.LifecycleManagerStore;
import org.jboss.arquillian.warp.spi.exception.ObjectNotAssociatedException;

import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

/**
 * Captures the {@link PhaseLifecycleEvent} from Warp JSF and combines it with {@link PortletPhase} to trigger
 * {@link org.jboss.arquillian.warp.Inspection} methods that match the two qualifiers.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class PhaseLifecycleObserver {

    public void handlePhaseLifecycle(@Observes PhaseLifecycleEvent event, PortletRequest request, FacesContext facesContext) {
        try {
            LifecycleManager manager = LifecycleManagerStore.get(FacesContext.class, facesContext);
            manager.fireEvent(PortletPhaseLifecycleEvent.getInstance(event, request));
        } catch (ObjectNotAssociatedException e) {
            throw new IllegalStateException(e);
        }
    }
}
