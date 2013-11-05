/**
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
package org.jboss.arquillian.portal.warp.jsf.it;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.portal.warp.jsf.PortletPhase;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.jsf.AfterPhase;
import org.jboss.arquillian.warp.jsf.BeforePhase;
import org.jboss.arquillian.warp.jsf.Phase;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.faces.bean.ManagedProperty;
import java.io.File;
import java.net.URL;

import static org.jboss.arquillian.warp.client.filter.http.HttpFilters.request;
import static org.jboss.arquillian.portal.warp.Phase.ACTION;
import static org.jboss.arquillian.portal.warp.Phase.RENDER;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@WarpTest
@PortalTest
public class JSFPortletWarpTest {

    private static final String NEW_VALUE = "New Value";

    @Deployment
    public static PortletArchive createDeployment() {
        PortletArchive archive = ShrinkWrap.create(PortletArchive.class, "JSFPortletWarp.war");

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.jboss.portletbridge:portletbridge-impl")
                .withTransitivity()
                .asFile();

        archive.createFacesPortlet("JsfFormSubmit", "JSF Form Portlet", "form.xhtml")
                .addAsWebResource("form.xhtml", "form.xhtml")
                .addClass(Bean.class)
                .addAsLibraries(libs);

        return archive;
    }

    @FindByJQuery("[id$=':input']")
    private WebElement inputField;

    @FindByJQuery("[id$=':submit']")
    private WebElement submitButton;

    @Drone
    WebDriver browser;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @RunAsClient
    @Test
    public void test() {
        browser.navigate().to(portalURL);

        Warp
            .initiate(new Activity() {
                @Override
                public void perform() {
                    inputField.clear();
                    inputField.sendKeys(NEW_VALUE);
                    submitButton.click();
                }
            })
            .group()
                .observe(request().index(1))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 1L;

                    @ManagedProperty("#{bean}")
                    Bean bean;

                    @PortletPhase(ACTION) @BeforePhase(Phase.UPDATE_MODEL_VALUES)
                    public void testBeanValueBeforeUpdate() {
                        assertEquals("Bean value should not be updated yet.", Bean.HELLO_JSF_PORTLET, bean.getText());
                    }

                    @PortletPhase(ACTION) @AfterPhase(Phase.UPDATE_MODEL_VALUES)
                    public void testBeanValueAfterUpdate() {
                        assertEquals("Bean value should now be updated.", NEW_VALUE, bean.getText());
                    }
                })
            .group()
                .observe(request().index(2))
                .inspect(new Inspection() {
                    private static final long serialVersionUID = 1L;

                    @ManagedProperty("#{bean}")
                    Bean bean;

                    @PortletPhase(RENDER) @BeforePhase(Phase.RENDER_RESPONSE)
                    public void testBeanValueBeforeRenderResponse() {
                        assertEquals("Bean value should still contain new value.", NEW_VALUE, bean.getText());
                    }
                })
            .execute();
    }
}
