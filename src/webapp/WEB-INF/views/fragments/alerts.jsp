<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String successMsg = (String) request.getAttribute("successMessage");
    if (successMsg == null) successMsg = request.getParameter("success");

    String errorMsg = (String) request.getAttribute("errorMessage");
    if (errorMsg == null) errorMsg = request.getParameter("error");
%>
<% if (successMsg != null && !successMsg.trim().isEmpty()) { %>
    <div class="alert alert-success">
        <i class="fas fa-check-circle"></i>
        <span><%= successMsg %></span>
    </div>
<% } %>
<% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
    <div class="alert alert-danger">
        <i class="fas fa-exclamation-circle"></i>
        <span><%= errorMsg %></span>
    </div>
<% } %>

