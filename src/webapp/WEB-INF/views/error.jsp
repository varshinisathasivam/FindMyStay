<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%
    Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    if (statusCode == null) statusCode = 404;

    String errorMsg = (String) request.getAttribute("jakarta.servlet.error.message");
    if (errorMsg == null || errorMsg.trim().isEmpty()) {
        if (statusCode == 404) {
            errorMsg = "The page or accommodation you are looking for does not exist or may have been moved.";
        } else if (statusCode == 403) {
            errorMsg = "You do not have permission to access this resource. Please login with appropriate credentials.";
        } else {
            errorMsg = "An unexpected error occurred while processing your request. Please try again later.";
        }
    }

    String errorTitle = (statusCode == 404) ? "Page Not Found" : (statusCode == 403 ? "Access Denied" : "Something Went Wrong");
    request.setAttribute("pageTitle", statusCode + " - " + errorTitle + " – FindMyStay");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container" style="padding:80px 20px;text-align:center;min-height:60vh;display:flex;align-items:center;justify-content:center;">
    <div style="background:var(--color-white);max-width:560px;width:100%;padding:48px 32px;border-radius:var(--radius-lg);border:1px solid var(--border-color);box-shadow:var(--card-shadow);">
        <div style="font-family:var(--font-heading);font-size:72px;font-weight:800;color:var(--color-terracotta);line-height:1;margin-bottom:16px;">
            <%= statusCode %>
        </div>
        <h1 style="font-size:24px;color:var(--color-olive);margin-bottom:12px;">
            <%= errorTitle %>
        </h1>
        <p style="color:var(--color-charcoal-muted);font-size:15px;line-height:1.6;margin-bottom:32px;">
            <%= errorMsg %>
        </p>
        <div style="display:flex;gap:12px;justify-content:center;flex-wrap:wrap;">
            <a href="<%= request.getContextPath() %>/" class="btn btn-primary btn-sm">
                <i class="fas fa-home"></i> Back to Home
            </a>
            <a href="<%= request.getContextPath() %>/stays" class="btn btn-terracotta btn-sm">
                <i class="fas fa-search"></i> Browse Stays
            </a>
            <a href="javascript:history.back()" class="btn btn-outline btn-sm">
                <i class="fas fa-arrow-left"></i> Go Back
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

