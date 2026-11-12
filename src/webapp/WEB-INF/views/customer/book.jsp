<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.Property" %>
<%@ page import="com.findmystay.model.RoomType" %>
<%@ page import="com.findmystay.model.User" %>
<%
    Property property = (Property) request.getAttribute("property");
    User user = (User) request.getAttribute("user");
    request.setAttribute("pageTitle", "Book Your Stay – FindMyStay");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container" style="padding-top:40px;padding-bottom:80px;">
    <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

    <div style="max-width:800px;margin:0 auto;">
        <div style="margin-bottom:24px;">
            <a href="<%= request.getContextPath() %>/property?id=<%= property.getId() %>" style="color:var(--color-charcoal-muted);font-size:14px;">
                &larr; Back to <%= property.getTitle() %>
            </a>
            <h1 style="font-size:28px;color:var(--color-olive);margin-top:8px;">Complete Your Booking Request</h1>
            <p style="color:var(--color-charcoal-muted);font-size:14px;">
                Direct reservation with zero brokerage fees. Your request will be reviewed by the owner.
            </p>
        </div>

        <div style="display:grid;grid-template-columns:1.5fr 1fr;gap:30px;">
            <!-- Booking Form -->
            <div style="background:var(--color-white);padding:30px;border-radius:var(--radius-md);border:1px solid var(--border-color);box-shadow:var(--card-shadow);">
                <form action="<%= request.getContextPath() %>/book" method="post">
                    <input type="hidden" name="propertyId" value="<%= property.getId() %>">
                    <input type="hidden" name="csrfToken" value="<%= com.findmystay.util.CsrfUtil.getToken(session) %>">

                    <div class="form-group" style="margin-bottom:16px;">
                        <label class="form-label">Selected Property</label>
                        <input type="text" class="form-control" value="<%= property.getTitle() %>" readonly style="background:#F0EDE6;font-weight:600;">
                    </div>

                    <div class="form-group" style="margin-bottom:16px;">
                        <label class="form-label">Select Room Type</label>
                        <select name="roomType" class="form-select" required>
                            <%
                                List<RoomType> rts = property.getRoomTypes();
                                if (rts != null && !rts.isEmpty()) {
                                    for (RoomType rt : rts) {
                            %>
                                <option value="<%= rt.getName() %>">
                                    <%= rt.getName() %> - ₹<%= String.format("%,.0f", rt.getRent()) %>/month (<%= rt.getAvailableCount() %> open)
                                </option>
                            <%
                                    }
                                } else {
                            %>
                                <option value="Standard Room">Standard Room - ₹<%= String.format("%,.0f", property.getMonthlyRent()) %>/month</option>
                            <% } %>
                        </select>
                    </div>

                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-bottom:16px;">
                        <div class="form-group">
                            <label class="form-label">Move-In Date</label>
                            <input type="date" name="moveInDate" class="form-control" required min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                        </div>

                        <div class="form-group">
                            <label class="form-label">Duration (Months)</label>
                            <input type="number" id="bookingDuration" name="durationMonths" class="form-control" value="1" min="1" max="24" required>
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom:16px;">
                        <label class="form-label">Primary Contact Number</label>
                        <input type="tel" name="contactNumber" class="form-control" value="<%= user != null ? user.getPhone() : "" %>" placeholder="10-digit mobile number" required>
                    </div>

                    <div class="form-group" style="margin-bottom:20px;">
                        <label class="form-label">Additional Message / Note for Owner</label>
                        <textarea name="message" class="form-control" rows="3" placeholder="College / Workplace details, dietary requests, or expected arrival time..."></textarea>
                    </div>

                    <button type="submit" class="btn btn-terracotta btn-lg" style="width:100%;">
                        <i class="fas fa-paper-plane"></i> Confirm &amp; Submit Booking Request
                    </button>
                </form>
            </div>

            <!-- Booking Summary Card -->
            <div>
                <div style="background:var(--color-white);padding:24px;border-radius:var(--radius-md);border:1px solid var(--border-color);box-shadow:var(--card-shadow);position:sticky;top:100px;">
                    <h3 style="font-size:18px;color:var(--color-olive);margin-bottom:14px;border-bottom:1px solid var(--border-color);padding-bottom:10px;">
                        Price Summary
                    </h3>

                    <div style="margin-bottom:16px;">
                        <img src="<%= request.getContextPath() %>/<%= property.getFeaturedImage() %>" alt="<%= property.getTitle() %>" style="border-radius:var(--radius-sm);height:120px;width:100%;object-fit:cover;margin-bottom:10px;">
                        <h4 style="font-size:15px;color:var(--color-charcoal);"><%= property.getTitle() %></h4>
                        <span style="font-size:13px;color:var(--color-charcoal-muted);"><%= property.getCity() %></span>
                    </div>

                    <div style="display:flex;justify-content:space-between;font-size:14px;margin-bottom:8px;">
                        <span>Monthly Rent:</span>
                        <strong id="monthlyRentValue" data-rent="<%= property.getMonthlyRent() %>">₹<%= String.format("%,.0f", property.getMonthlyRent()) %></strong>
                    </div>

                    <div style="display:flex;justify-content:space-between;font-size:14px;margin-bottom:8px;">
                        <span>Security Deposit (Refundable):</span>
                        <strong>₹<%= String.format("%,.0f", property.getSecurityDeposit()) %></strong>
                    </div>

                    <div style="display:flex;justify-content:space-between;font-size:14px;margin-bottom:16px;color:#2E7D32;">
                        <span>Brokerage Fee:</span>
                        <strong>FREE (₹0)</strong>
                    </div>

                    <div style="display:flex;justify-content:space-between;align-items:center;padding-top:14px;border-top:1.5px dashed var(--border-color);margin-top:10px;">
                        <span style="font-weight:700;font-size:15px;">Est. Total Rent:</span>
                        <span id="bookingTotalAmount" style="font-family:var(--font-heading);font-size:22px;font-weight:800;color:var(--color-terracotta);">
                            ₹<%= String.format("%,.0f", property.getMonthlyRent()) %>
                        </span>
                    </div>

                    <div style="background:var(--bg-cream);padding:12px;border-radius:var(--radius-sm);margin-top:18px;font-size:12px;color:var(--color-charcoal-muted);line-height:1.5;">
                        <i class="fas fa-info-circle text-info"></i> No online payment required right now. You pay directly to the property owner upon physical move-in.
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

