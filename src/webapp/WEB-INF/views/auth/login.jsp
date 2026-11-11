<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setAttribute("pageTitle", "Login – FindMyStay");
    String redirect = (String) request.getAttribute("redirect");
    if (redirect == null) redirect = request.getParameter("redirect");
    String email = (String) request.getAttribute("email");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <i class="fas fa-home" style="color:var(--color-terracotta);"></i> Find<span>MyStay</span>
            </div>
            <h1 class="auth-title">Welcome Back</h1>
            <p class="auth-subtitle">Sign in to your customer, owner, or admin account</p>
        </div>

        <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

        <form action="<%= request.getContextPath() %>/login" method="post">
            <% if (redirect != null && !redirect.trim().isEmpty()) { %>
                <input type="hidden" name="redirect" value="<%= redirect %>">
            <% } %>

            <div class="form-group" style="margin-bottom:16px;">
                <label class="form-label">Email Address</label>
                <input type="email" id="loginEmail" name="email" class="form-control" placeholder="name@example.com" value="<%= email != null ? email : "" %>" required autofocus>
            </div>

            <div class="form-group" style="margin-bottom:20px;">
                <label class="form-label">Password</label>
                <input type="password" id="loginPassword" name="password" class="form-control" placeholder="••••••••" required>
            </div>

            <button type="submit" class="btn btn-terracotta btn-lg" style="width:100%;">
                <i class="fas fa-sign-in-alt"></i> Sign In to FindMyStay
            </button>
        </form>

        <!-- DEMO CREDENTIALS SHORTCUT BOX -->
        <div class="demo-credentials-box">
            <h6><i class="fas fa-key text-warning"></i> Demo Accounts (Click to Fill):</h6>
            <div style="display:flex;flex-wrap:wrap;gap:6px;margin-top:8px;">
                <span class="demo-tag" onclick="fillCredentials('customer@findmystay.com', 'Customer@123')">
                    <i class="fas fa-user"></i> Customer
                </span>
                <span class="demo-tag" onclick="fillCredentials('owner@findmystay.com', 'Owner@123')">
                    <i class="fas fa-building"></i> Owner
                </span>
                <span class="demo-tag" onclick="fillCredentials('admin@findmystay.com', 'Admin@123')">
                    <i class="fas fa-shield-alt"></i> Admin
                </span>
            </div>
            <div style="font-size:11px;color:var(--color-charcoal-muted);margin-top:6px;">
                Pass: <code>Customer@123</code> / <code>Owner@123</code> / <code>Admin@123</code>
            </div>
        </div>

        <div class="auth-footer">
            Don't have an account? 
            <a href="<%= request.getContextPath() %>/register" style="color:var(--color-terracotta);font-weight:700;">Create One Now</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

