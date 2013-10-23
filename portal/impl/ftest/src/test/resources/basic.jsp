<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<portlet:actionURL var="myAction" />
<form action="<%= myAction %>" method="POST">
    <input id="submit" type="Submit" />
</form>

<div id="output"><%= renderRequest.getParameter("data") %></div>