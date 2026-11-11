<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.findmystay.model.User" %>
<%@ page import="com.findmystay.model.PropertyComparison" %>
<%
    User navUser = (User) session.getAttribute("currentUser");
    PropertyComparison navCompare = (PropertyComparison) session.getAttribute("comparison");
    int compareCount = (navCompare != null) ? navCompare.getCount() : 0;
%>
<header class="navbar">
    <div class="container nav-container">
        <a href="<%= request.getContextPath() %>/" class="nav-brand">
            <span class="brand-icon"><i class="fas fa-home"></i></span>
            Find<span>MyStay</span>
        </a>

        <ul class="nav-links">
            <li><a href="<%= request.getContextPath() %>/" class="nav-link">Home</a></li>
            <li><a href="<%= request.getContextPath() %>/stays" class="nav-link">Explore Stays</a></li>
            <li><a href="<%= request.getContextPath() %>/stays?type=PG" class="nav-link">PGs</a></li>
            <li><a href="<%= request.getContextPath() %>/stays?type=HOSTEL" class="nav-link">Hostels</a></li>
            <li><a href="<%= request.getContextPath() %>/stays?type=ROOM" class="nav-link">Rental Rooms</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/compare" class="nav-link">
                    Compare
                    <% if (compareCount > 0) { %>
                        <span class="badge badge-terracotta" style="background:#C8795B;color:#FFF;padding:2px 7px;font-size:11px;"><%= compareCount %></span>
                    <% } %>
                </a>
            </li>
        </ul>

        <div class="nav-actions">
            <% if (navUser == null) { %>
                <a href="<%= request.getContextPath() %>/login" class="btn btn-outline btn-sm">Login</a>
                <a href="<%= request.getContextPath() %>/register" class="btn btn-terracotta btn-sm">Register</a>
            <% } else { %>
                <% if (navUser.isCustomer()) { %>
                    <a href="<%= request.getContextPath() %>/wishlist" class="btn btn-secondary btn-sm" title="Saved Wishlist">
                        <i class="fas fa-heart text-danger"></i> Wishlist
                    </a>
                    <a href="<%= request.getContextPath() %>/customer/dashboard" class="btn btn-primary btn-sm">
                        <i class="fas fa-user-circle"></i> <%= navUser.getFullName().split(" ")[0] %>
                    </a>
                <% } else if (navUser.isOwner()) { %>
                    <a href="<%= request.getContextPath() %>/owner/property/add" class="btn btn-terracotta btn-sm">
                        <i class="fas fa-plus"></i> Post Property
                    </a>
                    <a href="<%= request.getContextPath() %>/owner/dashboard" class="btn btn-primary btn-sm">
                        <i class="fas fa-building"></i> Owner Hub
                    </a>
                <% } else if (navUser.isAdmin()) { %>
                    <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn btn-terracotta btn-sm">
                        <i class="fas fa-shield-alt"></i> Admin Panel
                    </a>
                <% } %>
                <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline btn-sm" title="Logout">
                    <i class="fas fa-sign-out-alt"></i>
                </a>
            <% } %>
        </div>
    </div>
</header>

