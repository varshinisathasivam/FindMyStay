<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setAttribute("pageTitle", "Create an Account – FindMyStay");
    String fullName = (String) request.getAttribute("fullName");
    String email = (String) request.getAttribute("email");
    String phone = (String) request.getAttribute("phone");
    String role = (String) request.getAttribute("role");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="auth-wrapper">
    <div class="auth-card wide">
        <div class="auth-header">
            <div class="auth-logo">
                <i class="fas fa-home" style="color:var(--color-terracotta);"></i> Find<span>MyStay</span>
            </div>
            <h1 class="auth-title">Join FindMyStay</h1>
            <p class="auth-subtitle">Discover comfortable stays or list your property today</p>
        </div>

        <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

        <form action="<%= request.getContextPath() %>/register" method="post" id="registerForm">
            <!-- Role Selection -->
            <div style="background:var(--bg-cream);padding:14px;border-radius:var(--radius-sm);margin-bottom:18px;border:1px solid var(--border-color);">
                <label class="form-label" style="margin-bottom:8px;">I want to register as a:</label>
                <div style="display:flex;gap:24px;font-size:14px;font-weight:600;">
                    <label style="display:flex;align-items:center;gap:6px;cursor:pointer;">
                        <input type="radio" name="role" value="CUSTOMER" <%= (!"OWNER".equals(role)) ? "checked" : "" %>>
                        <i class="fas fa-user" style="color:var(--color-olive);"></i> Customer / Tenant
                    </label>
                    <label style="display:flex;align-items:center;gap:6px;cursor:pointer;">
                        <input type="radio" name="role" value="OWNER" <%= "OWNER".equals(role) ? "checked" : "" %>>
                        <i class="fas fa-building" style="color:var(--color-terracotta);"></i> Property Owner / PG Host
                    </label>
                </div>
            </div>

            <div class="form-grid-2">
                <div class="form-group" style="margin-bottom:14px;">
                    <label class="form-label">Full Name</label>
                    <input type="text" name="fullName" class="form-control" placeholder="e.g. Karthik Raja" value="<%= fullName != null ? fullName : "" %>" required>
                </div>

                <div class="form-group" style="margin-bottom:14px;">
                    <label class="form-label">Email Address</label>
                    <input type="email" name="email" class="form-control" placeholder="name@example.com" value="<%= email != null ? email : "" %>" required>
                </div>
            </div>

            <div class="form-group" style="margin-bottom:14px;">
                <label class="form-label">Phone Number (10 Digits)</label>
                <input type="tel" name="phone" class="form-control" placeholder="e.g. 9876543210" value="<%= phone != null ? phone : "" %>" required pattern="[6-9][0-9]{9}">
            </div>

            <div class="form-grid-2">
                <div class="form-group" style="margin-bottom:20px;">
                    <label class="form-label">Password (min 6 chars)</label>
                    <input type="password" name="password" class="form-control" placeholder="••••••••" required minlength="6">
                </div>

                <div class="form-group" style="margin-bottom:20px;">
                    <label class="form-label">Confirm Password</label>
                    <input type="password" name="confirmPassword" class="form-control" placeholder="••••••••" required minlength="6">
                </div>
            </div>

            <button type="submit" class="btn btn-terracotta btn-lg" style="width:100%;">
                <i class="fas fa-user-plus"></i> Create Account
            </button>
        </form>

        <div class="auth-footer">
            Already have an account? 
            <a href="<%= request.getContextPath() %>/login" style="color:var(--color-olive);font-weight:700;">Login Here</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

