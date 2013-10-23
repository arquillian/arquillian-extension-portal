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
package org.jboss.arquillian.portal.impl.it;

import category.RequiresJSF;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.PortletBridgeConstants;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.WebAppVersionType;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@RunWith(Arquillian.class)
@Category(RequiresJSF.class)
@PortalTest
public class JSFPortletTest {

    public static final String NEW_VALUE = "New Value";

    @Deployment
    public static PortletArchive getDeployment() {
        PortletArchive archive = ShrinkWrap.create(PortletArchive.class, "JSFPortlet.war");

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.jboss.portletbridge:portletbridge-impl")
                .withTransitivity()
                .asFile();

        archive.createFacesPortlet("JsfFormSubmit", "JSF Form Portlet", "form.xhtml")
                .addAsWebResource("form.xhtml", "form.xhtml")
                .addClass(Bean.class)
                .addAsLibraries(libs);

        WebAppDescriptor webXml = Descriptors.create(WebAppDescriptor.class)
                .addDefaultNamespaces()
                .version(WebAppVersionType._3_0)
                .createContextParam()
                    .paramName(PortletBridgeConstants.REQUEST_SCOPE_PRESERVED)
                    .paramValue("true")
                    .up();

        archive.setWebXML(new StringAsset(webXml.exportAsString()));

        return archive;
    }

    @FindBy(id = "output")
    private WebElement outputField;

    @FindBy(xpath = "//input[@type='text']")
    private WebElement inputField;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submitButton;

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Test
    @InSequence(1)
    @RunAsClient
    public void renderFormPortlet() throws Exception {
        browser.navigate().to(portalURL);

        assertEquals("Output text set.", Bean.HELLO_JSF_PORTLET, outputField.getText());

        assertEquals("Input text set.", Bean.HELLO_JSF_PORTLET, inputField.getAttribute("value"));

        assertEquals("Submit button value should be 'Ok'", "Ok", submitButton.getAttribute("value"));
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void testSubmitAndRemainOnPage() throws Exception {
        browser.navigate().to(portalURL);

        inputField.clear();
        inputField.sendKeys(NEW_VALUE);
        submitButton.click();

        assertTrue("Output text updated.", outputField.getText().equals(NEW_VALUE));

        assertTrue("Input text updated.", inputField.getAttribute("value").equals(NEW_VALUE));

        // Re-render page
        browser.navigate().refresh();

        assertTrue("Output text unchanged.", outputField.getText().equals(NEW_VALUE));

        assertTrue("Input text unchanged.", inputField.getAttribute("value").equals(NEW_VALUE));
    }
}
