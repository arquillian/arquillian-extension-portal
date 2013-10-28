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
package org.jboss.arquillian.portal.warp.portlet.it;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.portal.warp.AfterPortletPhase;
import org.jboss.arquillian.portal.warp.BeforePortletPhase;
import org.jboss.arquillian.portal.warp.Phase;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.WebAppVersionType;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import java.net.URL;

import static org.jboss.arquillian.warp.client.filter.http.HttpFilters.request;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@PortalTest
@WarpTest
public class PortletWarpTest {

    @Deployment
    public static PortletArchive getDeployment() {
        PortletArchive archive = ShrinkWrap.create(PortletArchive.class, "BasicPortletWarp.war");
        archive.createSimplePortlet(BasicPortlet.class)
            .addClass(BasicPortlet.class)
            .addAsWebResource("basic.jsp", "basic.jsp");

        WebAppDescriptor webXml = Descriptors.create(WebAppDescriptor.class)
                .addDefaultNamespaces()
                .version(WebAppVersionType._3_0);

        archive.setWebXML(new StringAsset(webXml.exportAsString()));
        return archive;
    }

    @FindBy(id = "output")
    private WebElement outputDiv;

    @FindBy(id = "submit")
    private WebElement submitButton;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Test
    @RunAsClient
    public void portletAction() throws Exception {

        browser.navigate().to(portalURL);

        Warp
            .initiate(new Activity() {
                @Override
                public void perform() {
                    submitButton.click();
                }
            })
            .group()
                .observe(request().index(1))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 1L;

                    @ArquillianResource
                    ActionResponse actionResponse;

                    @BeforePortletPhase(Phase.ACTION)
                    public void beforeActionRequest() {
                        String[] values = actionResponse.getRenderParameterMap().get("data");
                        assertNull("Render parameter for data should not be set.", values);
                    }

                    @AfterPortletPhase(Phase.ACTION)
                    public void afterActionRequest() {
                        String[] values = actionResponse.getRenderParameterMap().get("data");
                        assertTrue("Render parameter for data should be set.", values.length == 1);
                        assertEquals("Render parameter set to incorrect value.", BasicPortlet.ACTION, values[0]);
                    }
                })
            .group()
                .observe(request().index(2))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 1L;

                    @ArquillianResource
                    RenderRequest renderRequest;

                    @BeforePortletPhase(Phase.RENDER)
                    public void beforeRenderRequest() {
                        String value = renderRequest.getParameter("data");
                        assertEquals("Render parameter set to incorrect value.", BasicPortlet.ACTION, value);
                    }
                })
            .execute();
    }
}
