package org.jboss.arquillian.portal.warp.jsf;

import org.jboss.arquillian.portal.warp.Phase;
import org.jboss.arquillian.warp.jsf.PhaseLifecycleEvent;
import org.jboss.arquillian.warp.spi.WarpLifecycleEvent;

import javax.portlet.ActionRequest;
import javax.portlet.EventRequest;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public abstract class PortletPhaseLifecycleEvent extends WarpLifecycleEvent {

    private PhaseLifecycleEvent phaseLifecycleEvent;

    private Phase portletPhase;

    private PortletPhaseLifecycleEvent(PhaseLifecycleEvent event, Phase portletPhase) {
        this.phaseLifecycleEvent = event;
        this.portletPhase = portletPhase;
    }

    @Override
    public List<Annotation> getQualifiers() {
        List<Annotation> qualifiers = new ArrayList<>(phaseLifecycleEvent.getQualifiers());

        qualifiers.add(
            new PortletPhase() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return PortletPhase.class;
                }

                @Override
                public Phase value() {
                    return portletPhase;
                }
            }
        );

        return qualifiers;
    }

    public static PortletPhaseLifecycleEvent getInstance(PhaseLifecycleEvent event, PortletRequest request) {
        if (request instanceof ActionRequest) {
            return new ActionRequestPhaseEvent(event);
        } else if (request instanceof EventRequest) {
            return new EventRequestPhaseEvent(event);
        } else if (request instanceof RenderRequest) {
            return new RenderRequestPhaseEvent(event);
        } else if (request instanceof ResourceRequest) {
            return new ResourceRequestPhaseEvent(event);
        }
        throw new IllegalStateException("Unsupported PortletRequest: " + request);
    }

    public static class ActionRequestPhaseEvent extends PortletPhaseLifecycleEvent {
        private ActionRequestPhaseEvent(PhaseLifecycleEvent event) {
            super(event, Phase.ACTION);
        }
    }

    public static class EventRequestPhaseEvent extends PortletPhaseLifecycleEvent {
        private EventRequestPhaseEvent(PhaseLifecycleEvent event) {
            super(event, Phase.EVENT);
        }
    }

    public static class RenderRequestPhaseEvent extends PortletPhaseLifecycleEvent {
        private RenderRequestPhaseEvent(PhaseLifecycleEvent event) {
            super(event, Phase.RENDER);
        }
    }

    public static class ResourceRequestPhaseEvent extends PortletPhaseLifecycleEvent {
        private ResourceRequestPhaseEvent(PhaseLifecycleEvent event) {
            super(event, Phase.RESOURCE);
        }
    }
}
