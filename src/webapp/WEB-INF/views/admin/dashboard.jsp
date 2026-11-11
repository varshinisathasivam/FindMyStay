<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.*" %>
<%
    request.setAttribute("pageTitle", "Administration Center – FindMyStay");
    User admin = (User) session.getAttribute("currentUser");

    Integer totalCustomers = (Integer) request.getAttribute("totalCustomers");
    Integer totalOwners = (Integer) request.getAttribute("totalOwners");
    Integer totalProperties = (Integer) request.getAttribute("totalProperties");
    Integer pendingVerification = (Integer) request.getAttribute("pendingVerification");
    Integer totalBookings = (Integer) request.getAttribute("totalBookings");

    List<User> allUsers = (List<User>) request.getAttribute("allUsers");
    List<Property> allProperties = (List<Property>) request.getAttribute("allProperties");
    List<Booking> allBookings = (List<Booking>) request.getAttribute("allBookings");
    List<Review> allReviews = (List<Review>) request.getAttribute("allReviews");
    List<Complaint> allComplaints = (List<Complaint>) request.getAttribute("allComplaints");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container dashboard-layout">
    <!-- LEFT ADMIN SIDEBAR -->
    <aside class="dashboard-sidebar">
        <div class="user-profile-summary">
            <div class="avatar-large" style="background:#283C32;">
                <i class="fas fa-shield-alt"></i>
            </div>
            <h3 style="font-size:18px;color:var(--color-olive);margin-bottom:4px;">Master Admin</h3>
            <span class="badge badge-verified" style="margin-bottom:8px;">Platform Administrator</span>
            <div style="font-size:12px;color:var(--color-charcoal-muted);"><%= admin != null ? admin.getEmail() : "" %></div>
        </div>

        <ul class="sidebar-nav">
            <li><a href="#overview" class="sidebar-link active"><i class="fas fa-chart-line"></i> System Overview</a></li>
            <li><a href="#verification" class="sidebar-link" style="color:var(--color-terracotta);"><i class="fas fa-check-shield"></i> Pending Verifications (<%= pendingVerification != null ? pendingVerification : 0 %>)</a></li>
            <li><a href="#properties" class="sidebar-link"><i class="fas fa-hotel"></i> All Listings (<%= totalProperties != null ? totalProperties : 0 %>)</a></li>
            <li><a href="#users" class="sidebar-link"><i class="fas fa-users"></i> User Directory</a></li>
            <li><a href="#bookings" class="sidebar-link"><i class="fas fa-calendar-check"></i> All Bookings (<%= totalBookings != null ? totalBookings : 0 %>)</a></li>
            <li><a href="#reviews" class="sidebar-link"><i class="fas fa-star"></i> Review Moderation</a></li>
            <li><a href="#complaints" class="sidebar-link"><i class="fas fa-exclamation-triangle"></i> Complaints (<%= allComplaints != null ? allComplaints.size() : 0 %>)</a></li>
            <li><a href="<%= request.getContextPath() %>/logout" class="sidebar-link" style="color:#C62828;"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
        </ul>
    </aside>

    <!-- RIGHT MAIN CONTENT -->
    <main class="dashboard-main">
        <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

        <!-- 5 SYSTEM METRICS -->
        <section id="overview" class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon olive"><i class="fas fa-users"></i></div>
                <div class="stat-info">
                    <h3><%= totalCustomers != null ? totalCustomers : 0 %></h3>
                    <p>Total Customers</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon terracotta"><i class="fas fa-user-tie"></i></div>
                <div class="stat-info">
                    <h3><%= totalOwners != null ? totalOwners : 0 %></h3>
                    <p>Property Owners</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon sage"><i class="fas fa-building"></i></div>
                <div class="stat-info">
                    <h3><%= totalProperties != null ? totalProperties : 0 %></h3>
                    <p>Total Properties</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon terracotta"><i class="fas fa-clock"></i></div>
                <div class="stat-info">
                    <h3><%= pendingVerification != null ? pendingVerification : 0 %></h3>
                    <p>Pending Verifications</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon olive"><i class="fas fa-calendar-check"></i></div>
                <div class="stat-info">
                    <h3><%= totalBookings != null ? totalBookings : 0 %></h3>
                    <p>Total Bookings</p>
                </div>
            </div>
        </section>

        <!-- PROPERTY VERIFICATION & MANAGEMENT -->
        <section id="properties" class="dashboard-card">
            <div class="dashboard-card-header">
                <div>
                    <h2 class="dashboard-card-title"><i class="fas fa-hotel"></i> Property Listing Approvals &amp; Status</h2>
                    <p style="font-size:13px;color:var(--color-charcoal-muted);">Verify stay compliance or toggle listing visibility across the platform.</p>
                </div>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Stay / Property</th>
                            <th>Host / Owner</th>
                            <th>City</th>
                            <th>Monthly Rent</th>
                            <th>Verified?</th>
                            <th>Active?</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (allProperties != null) {
                            for (Property p : allProperties) {
                        %>
                            <tr>
                                <td>
                                    <strong><a href="<%= request.getContextPath() %>/property?id=<%= p.getId() %>" style="color:var(--color-olive);"><%= p.getTitle() %></a></strong>
                                    <div style="font-size:12px;color:var(--color-charcoal-muted);"><%= p.getPropertyType() %> &bull; <%= p.getRoomSharing() %></div>
                                </td>
                                <td><%= p.getOwnerName() %><br><span style="font-size:12px;color:var(--color-charcoal-muted);"><%= p.getOwnerPhone() %></span></td>
                                <td><%= p.getCity() %></td>
                                <td style="font-weight:700;color:var(--color-terracotta);">₹<%= String.format("%,.0f", p.getMonthlyRent()) %></td>
                                <td>
                                    <% if (p.isVerified()) { %>
                                        <span class="badge badge-verified"><i class="fas fa-check"></i> Verified</span>
                                    <% } else { %>
                                        <span class="badge badge-warning"><i class="fas fa-clock"></i> Unverified</span>
                                    <% } %>
                                </td>
                                <td>
                                    <span class="badge <%= p.isActive() ? "badge-success" : "badge-danger" %>">
                                        <%= p.isActive() ? "Active" : "Hidden" %>
                                    </span>
                                </td>
                                <td>
                                    <div style="display:flex;gap:6px;">
                                        <!-- Verify / Unverify Toggle -->
                                        <form action="<%= request.getContextPath() %>/admin/verify" method="post" style="display:inline;">
                                            <input type="hidden" name="propertyId" value="<%= p.getId() %>">
                                            <input type="hidden" name="verified" value="<%= !p.isVerified() %>">
                                            <button type="submit" class="btn btn-sm <%= p.isVerified() ? "btn-secondary" : "btn-primary" %>" style="padding:4px 8px;" title="<%= p.isVerified() ? "Revoke Verification" : "Approve & Verify" %>">
                                                <i class="fas fa-<%= p.isVerified() ? "ban" : "check" %>"></i>
                                            </button>
                                        </form>

                                        <!-- Activate / Deactivate Toggle -->
                                        <form action="<%= request.getContextPath() %>/admin/property/toggle" method="post" style="display:inline;">
                                            <input type="hidden" name="propertyId" value="<%= p.getId() %>">
                                            <input type="hidden" name="active" value="<%= !p.isActive() %>">
                                            <button type="submit" class="btn btn-sm btn-outline" style="padding:4px 8px;" title="<%= p.isActive() ? "Deactivate Listing" : "Re-activate Listing" %>">
                                                <i class="fas fa-power-off"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </section>

        <!-- USER DIRECTORY (CUSTOMERS & OWNERS) -->
        <section id="users" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-users-cog"></i> User Management</h2>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Member Since</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (allUsers != null) {
                            for (User u : allUsers) {
                        %>
                            <tr>
                                <td>#<%= u.getId() %></td>
                                <td><strong><%= u.getFullName() %></strong></td>
                                <td><%= u.getEmail() %></td>
                                <td><%= u.getPhone() %></td>
                                <td>
                                    <span class="badge <%= u.isAdmin() ? "badge-danger" : (u.isOwner() ? "badge-warning" : "badge-neutral") %>">
                                        <%= u.getRole() %>
                                    </span>
                                </td>
                                <td style="font-size:12px;"><%= u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0, 10) : "" %></td>
                                <td>
                                    <% if (!u.isAdmin()) { %>
                                        <form action="<%= request.getContextPath() %>/admin/user/delete" method="post" onsubmit="return confirm('Delete this user account?')">
                                            <input type="hidden" name="userId" value="<%= u.getId() %>">
                                            <button type="submit" class="btn btn-outline btn-sm" style="color:#C62828;border-color:#C62828;padding:4px 8px;" title="Delete User">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    <% } else { %>
                                        <span style="font-size:11px;color:var(--color-charcoal-muted);">Protected</span>
                                    <% } %>
                                </td>
                            </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </section>

        <!-- ALL BOOKINGS -->
        <section id="bookings" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-calendar-check"></i> Master Bookings Log</h2>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Ref</th>
                            <th>Tenant</th>
                            <th>Property</th>
                            <th>Move-In</th>
                            <th>Duration</th>
                            <th>Amount</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (allBookings != null) {
                            for (Booking b : allBookings) {
                        %>
                            <tr>
                                <td><strong><%= b.getBookingRef() %></strong></td>
                                <td><%= b.getCustomerName() %><br><span style="font-size:12px;color:var(--color-charcoal-muted);"><%= b.getCustomerPhone() %></span></td>
                                <td><%= b.getPropertyTitle() %><br><span style="font-size:12px;color:var(--color-charcoal-muted);"><%= b.getPropertyCity() %></span></td>
                                <td><%= b.getMoveInDate() %></td>
                                <td><%= b.getDurationMonths() %> mo</td>
                                <td style="font-weight:700;color:var(--color-terracotta);">₹<%= String.format("%,.0f", b.getTotalAmount()) %></td>
                                <td><span class="badge <%= b.getStatusBadgeClass() %>"><%= b.getStatus() %></span></td>
                            </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </section>

        <!-- REVIEWS MODERATION -->
        <section id="reviews" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-comments"></i> Resident Reviews Moderation</h2>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Stay</th>
                            <th>Reviewer</th>
                            <th>Rating</th>
                            <th>Comment</th>
                            <th>Date</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (allReviews != null) {
                            for (Review r : allReviews) {
                        %>
                            <tr>
                                <td><strong><%= r.getPropertyTitle() %></strong></td>
                                <td><%= r.getUserName() %></td>
                                <td style="color:#F4B400;font-weight:700;"><i class="fas fa-star"></i> <%= r.getRating() %></td>
                                <td style="max-width:280px;"><%= r.getComment() %></td>
                                <td style="font-size:12px;"><%= r.getCreatedAt() != null ? r.getCreatedAt().toString().substring(0, 10) : "" %></td>
                                <td>
                                    <form action="<%= request.getContextPath() %>/admin/review/delete" method="post" onsubmit="return confirm('Delete this review?')">
                                        <input type="hidden" name="reviewId" value="<%= r.getId() %>">
                                        <button type="submit" class="btn btn-outline btn-sm" style="color:#C62828;border-color:#C62828;padding:4px 8px;">
                                            <i class="fas fa-trash"></i> Remove
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </section>

        <!-- COMPLAINTS TICKETS -->
        <section id="complaints" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-exclamation-triangle"></i> Complaints &amp; Grievances</h2>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Subject</th>
                            <th>Submitted By</th>
                            <th>Property</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (allComplaints != null) {
                            for (Complaint c : allComplaints) {
                        %>
                            <tr>
                                <td><strong><%= c.getSubject() %></strong></td>
                                <td><%= c.getUserName() %><br><span style="font-size:12px;color:var(--color-charcoal-muted);"><%= c.getUserEmail() %></span></td>
                                <td><%= c.getPropertyTitle() != null ? c.getPropertyTitle() : "General" %></td>
                                <td><%= c.getDescription() %></td>
                                <td>
                                    <span class="badge <%= c.getStatus() == Complaint.ComplaintStatus.RESOLVED ? "badge-success" : "badge-warning" %>">
                                        <%= c.getStatus() %>
                                    </span>
                                </td>
                                <td>
                                    <% if (c.getStatus() != Complaint.ComplaintStatus.RESOLVED) { %>
                                        <form action="<%= request.getContextPath() %>/admin/complaint/status" method="post">
                                            <input type="hidden" name="complaintId" value="<%= c.getId() %>">
                                            <input type="hidden" name="status" value="RESOLVED">
                                            <button type="submit" class="btn btn-primary btn-sm" style="padding:4px 8px;">Mark Resolved</button>
                                        </form>
                                    <% } else { %>
                                        <span style="font-size:12px;color:#2E7D32;"><i class="fas fa-check"></i> Closed</span>
                                    <% } %>
                                </td>
                            </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

