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
package org.jboss.portletbridge.test;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class FakeUserPrincipalFilter implements Filter {

    private Principal fakePrincipal;
    private List principalRoles;

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (fakePrincipal != null && request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {

                public String getRemoteUser() {
                    return fakePrincipal.getName();
                }

                public Principal getUserPrincipal() {
                    return fakePrincipal;
                }

                public boolean isUserInRole(String roleName) {
                    return principalRoles != null && principalRoles.contains(roleName);
                }

            };
            chain.doFilter(wrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void init(final FilterConfig config) throws ServletException {
        final String principalName = System.getProperty("org.apache.pluto.embedded.principalName");
        final String principalRolesStr = System.getProperty("org.apache.pluto.embedded.principalRoles");
        if (principalName != null) {
            if (principalRolesStr != null) {
                String[] roles = principalRolesStr.split(",");
                principalRoles = Arrays.asList(roles);
            }
            fakePrincipal = new Principal() {
                public String getName() {
                    return principalName;
                }

                public String toString() {
                    return "PrincipalName: " + principalName + ", Roles: " + principalRoles;
                }
            };
        }
    }

}