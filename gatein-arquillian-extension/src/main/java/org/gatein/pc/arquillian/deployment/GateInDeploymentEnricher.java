/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.gatein.pc.arquillian.deployment;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webfragment30.WebFragmentDescriptor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class GateInDeploymentEnricher implements ApplicationArchiveProcessor {

    public static final String EMBED_PATH = "embed";

    private static final String EMBED_SERVLET_NAME = "EmbedServlet";
    private static final String EMBED_SERVLET_CLASS = "org.gatein.pc.embed.EmbedServlet";

    private static final ArchivePath WEB_XML_PATH = ArchivePaths.create("WEB-INF/web.xml");

    /**
     * @see org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor#process(org.jboss.shrinkwrap.api.Archive, org.jboss.arquillian.test.spi.TestClass)
     */
    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        if (testClass.isAnnotationPresent(PortalTest.class)) {
            if (applicationArchive instanceof WebArchive) {
                WebArchive webArchive = (WebArchive) applicationArchive;

                // Add GateIn PC Embed and Deps to WEB-INF/lib
                File[] files = Maven.resolver()
                                        .loadPomFromFile("pom.xml")
                                        .resolve("org.gatein.pc:pc-embed:2.4.0.Beta02")
                                        .withTransitivity()
                                        .as(File.class);
                webArchive.addAsLibraries(files);

                files = Maven.resolver()
                                 .loadPomFromFile("pom.xml")
                                 .resolve("javax.portlet:portlet-api:2.0")
                                 .withoutTransitivity()
                                 .as(File.class);
                webArchive.addAsLibraries(files);

                // Add EmbedServlet to web.xml
                addEmbed(webArchive);
            }
        }
    }

    private void addEmbed(WebArchive webArchive) {
        Node webXmlNode = webArchive.get(WEB_XML_PATH);
        if (null != webXmlNode) {
            WebAppDescriptor webXml = Descriptors.importAs(WebAppDescriptor.class).fromStream(webXmlNode.getAsset().openStream());

            // SHRINKWRAP-187, too eager on not allowing overrides, delete it first
            webArchive.delete(WEB_XML_PATH);

            webArchive.setWebXML(new StringAsset(addEmbedToDescriptor(webXml).exportAsString()));
        } else {
            // No web.xml present so add Embed Servlet to web fragment
            WebFragmentDescriptor webFrag = Descriptors.create(WebFragmentDescriptor.class);
            JavaArchive jar = ShrinkWrap.create(JavaArchive.class);
            jar.addAsManifestResource(new StringAsset(addEmbedToFragment(webFrag).exportAsString()), "web-fragment.xml");
            webArchive.addAsLibrary(jar);
        }
    }

    private WebFragmentDescriptor addEmbedToFragment(WebFragmentDescriptor webFragment) {
        webFragment.createServlet()
                        .servletName(EMBED_SERVLET_NAME)
                        .servletClass(EMBED_SERVLET_CLASS)
                        .loadOnStartup(0)
                        .up()
                    .createServletMapping()
                        .servletName(EMBED_SERVLET_NAME)
                        .urlPattern("/" + EMBED_PATH + "/*")
                        .up();
        return webFragment;
    }

    private WebAppDescriptor addEmbedToDescriptor(WebAppDescriptor webXml) {
        webXml.createServlet()
                  .servletName(EMBED_SERVLET_NAME)
                  .servletClass(EMBED_SERVLET_CLASS)
                  .loadOnStartup(0)
                  .up()
              .createServletMapping()
                  .servletName(EMBED_SERVLET_NAME)
                  .urlPattern("/" + EMBED_PATH + "/*")
                  .up();
        return webXml;
    }

}
