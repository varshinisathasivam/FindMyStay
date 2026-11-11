<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.*" %>
<%
    request.setAttribute("pageTitle", "Customer Dashboard – FindMyStay");
    User user = (User) request.getAttribute("user");
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    List<Wishlist> wishlists = (List<Wishlist>) request.getAttribute("wishlists");
    List<Property> recentlyViewed = (List<Property>) request.getAttribute("recentlyViewed");

    Integer totalBookings = (Integer) request.getAttribute("totalBookings");
    Integer pendingBookings = (Integer) request.getAttribute("pendingBookings");
    Integer approvedBookings = (Integer) request.getAttribute("approvedBookings");
    Integer wishlistCount = (Integer) request.getAttribute("wishlistCount");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container dashboard-layout">
    <!-- LEFT SIDEBAR -->
    <aside class="dashboard-sidebar">
        <div class="user-profile-summary">
            <div class="avatar-large">
                <%= user != null ? user.getFullName().substring(0, 1).toUpperCase() : "U" %>
            </div>
            <h3 style="font-size:18px;color:var(--color-olive);margin-bottom:4px;"><%= user != null ? user.getFullName() : "Customer" %></h3>
            <span class="badge badge-neutral" style="margin-bottom:8px;">Customer Portal</span>
            <div style="font-size:12px;color:var(--color-charcoal-muted);"><%= user != null ? user.getEmail() : "" %></div>
        </div>

        <ul class="sidebar-nav">
            <li><a href="#overview" class="sidebar-link active"><i class="fas fa-th-large"></i> Overview</a></li>
            <li><a href="#bookings" class="sidebar-link"><i class="fas fa-calendar-alt"></i> My Bookings</a></li>
            <li><a href="<%= request.getContextPath() %>/wishlist" class="sidebar-link"><i class="fas fa-heart"></i> My Wishlist (<%= wishlistCount != null ? wishlistCount : 0 %>)</a></li>
            <li><a href="#profile" class="sidebar-link"><i class="fas fa-user-cog"></i> Profile Settings</a></li>
            <li><a href="<%= request.getContextPath() %>/stays" class="sidebar-link"><i class="fas fa-search"></i> Explore Stays</a></li>
            <li><a href="<%= request.getContextPath() %>/logout" class="sidebar-link" style="color:#C62828;"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
        </ul>
    </aside>

    <!-- RIGHT MAIN CONTENT -->
    <main class="dashboard-main">
        <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

        <!-- STATS OVERVIEW -->
        <section id="overview" class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon olive"><i class="fas fa-book"></i></div>
                <div class="stat-info">
                    <h3><%= totalBookings != null ? totalBookings : 0 %></h3>
                    <p>Total Bookings</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon terracotta"><i class="fas fa-clock"></i></div>
                <div class="stat-info">
                    <h3><%= pendingBookings != null ? pendingBookings : 0 %></h3>
                    <p>Pending Requests</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon sage"><i class="fas fa-check-circle"></i></div>
                <div class="stat-info">
                    <h3><%= approvedBookings != null ? approvedBookings : 0 %></h3>
                    <p>Approved Stays</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon terracotta"><i class="fas fa-heart"></i></div>
                <div class="stat-info">
                    <h3><%= wishlistCount != null ? wishlistCount : 0 %></h3>
                    <p>Saved Stays</p>
                </div>
            </div>
        </section>

        <!-- BOOKING HISTORY -->
        <section id="bookings" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-calendar-check"></i> Booking History</h2>
                <a href="<%= request.getContextPath() %>/stays" class="btn btn-secondary btn-sm">Find More Stays</a>
            </div>

            <% if (bookings != null && !bookings.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Booking Ref</th>
                                <th>Property</th>
                                <th>Room Type</th>
                                <th>Move-In Date</th>
                                <th>Duration</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Booking b : bookings) { %>
                                <tr>
                                    <td><strong><%= b.getBookingRef() %></strong></td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/property?id=<%= b.getPropertyId() %>" style="color:var(--color-olive);font-weight:600;">
                                            <%= b.getPropertyTitle() %>
                                        </a>
                                        <div style="font-size:12px;color:var(--color-charcoal-muted);"><%= b.getPropertyCity() %></div>
                                    </td>
                                    <td><%= b.getRoomType() %></td>
                                    <td><%= b.getMoveInDate() %></td>
                                    <td><%= b.getDurationMonths() %> Month(s)</td>
                                    <td style="font-weight:700;color:var(--color-terracotta);">₹<%= String.format("%,.0f", b.getTotalAmount()) %></td>
                                    <td><span class="badge <%= b.getStatusBadgeClass() %>"><%= b.getStatus() %></span></td>
                                    <td>
                                        <% if (b.getStatus() == Booking.BookingStatus.PENDING) { %>
                                            <form action="<%= request.getContextPath() %>/customer/booking/cancel" method="post" style="display:inline;">
                                                <input type="hidden" name="bookingId" value="<%= b.getId() %>">
                                                <button type="submit" class="btn btn-outline btn-sm" style="color:#C62828;border-color:#C62828;" onclick="return confirm('Cancel this booking request?')">
                                                    Cancel
                                                </button>
                                            </form>
                                        <% } else { %>
                                            <span style="color:var(--color-charcoal-muted);font-size:12px;">No action</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div style="text-align:center;padding:40px 20px;color:var(--color-charcoal-muted);">
                    <i class="fas fa-calendar-times" style="font-size:36px;color:var(--color-sage);margin-bottom:12px;"></i>
                    <p>You haven't requested any bookings yet.</p>
                    <a href="<%= request.getContextPath() %>/stays" class="btn btn-terracotta btn-sm" style="margin-top:12px;">Browse Stays</a>
                </div>
            <% } %>
        </section>

        <!-- RECENTLY VIEWED PROPERTIES -->
        <% if (recentlyViewed != null && !recentlyViewed.isEmpty()) { %>
            <section class="dashboard-card">
                <div class="dashboard-card-header">
                    <h2 class="dashboard-card-title"><i class="fas fa-history"></i> Recently Viewed Properties</h2>
                </div>
                <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(220px, 1fr));gap:16px;">
                    <% for (Property rp : recentlyViewed) { %>
                        <div style="background:var(--bg-cream);border-radius:var(--radius-sm);overflow:hidden;border:1px solid var(--border-color);display:flex;flex-direction:column;">
                            <img src="<%= request.getContextPath() %>/<%= rp.getFeaturedImage() %>" alt="<%= rp.getTitle() %>" style="height:110px;width:100%;object-fit:cover;">
                            <div style="padding:12px;flex-grow:1;display:flex;flex-direction:column;justify-content:space-between;">
                                <div>
                                    <h4 style="font-size:14px;margin-bottom:4px;"><a href="<%= request.getContextPath() %>/property?id=<%= rp.getId() %>"><%= rp.getTitle() %></a></h4>
                                    <span style="font-size:12px;color:var(--color-charcoal-muted);"><%= rp.getCity() %></span>
                                </div>
                                <div style="display:flex;justify-content:space-between;align-items:center;margin-top:10px;">
                                    <span style="font-weight:700;color:var(--color-terracotta);font-size:13px;">₹<%= String.format("%,.0f", rp.getMonthlyRent()) %>/mo</span>
                                    <a href="<%= request.getContextPath() %>/property?id=<%= rp.getId() %>" class="btn btn-primary btn-sm" style="padding:4px 8px;font-size:11px;">View</a>
                                </div>
                            </div>
                        </div>
                    <% } %>
                </div>
            </section>
        <% } %>

        <!-- PROFILE & PASSWORD SETTINGS -->
        <section id="profile" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-user-edit"></i> Profile &amp; Account Settings</h2>
            </div>

            <div style="display:grid;grid-template-columns:1fr 1fr;gap:30px;">
                <!-- Profile Form -->
                <div>
                    <h3 style="font-size:16px;color:var(--color-olive);margin-bottom:14px;">Update Personal Details</h3>
                    <form action="<%= request.getContextPath() %>/customer/dashboard" method="post">
                        <input type="hidden" name="action" value="updateProfile">
                        <div class="form-group" style="margin-bottom:12px;">
                            <label class="form-label">Full Name</label>
                            <input type="text" name="fullName" class="form-control" value="<%= user != null ? user.getFullName() : "" %>" required>
                        </div>
                        <div class="form-group" style="margin-bottom:12px;">
                            <label class="form-label">Email Address (Read-only)</label>
                            <input type="email" class="form-control" value="<%= user != null ? user.getEmail() : "" %>" readonly style="background:#F0EDE6;">
                        </div>
                        <div class="form-group" style="margin-bottom:18px;">
                            <label class="form-label">Phone Number</label>
                            <input type="text" name="phone" class="form-control" value="<%= user != null ? user.getPhone() : "" %>" required>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">Save Profile Changes</button>
                    </form>
                </div>

                <!-- Password Form -->
                <div>
                    <h3 style="font-size:16px;color:var(--color-olive);margin-bottom:14px;">Change Password</h3>
                    <form action="<%= request.getContextPath() %>/customer/dashboard" method="post">
                        <input type="hidden" name="action" value="changePassword">
                        <div class="form-group" style="margin-bottom:12px;">
                            <label class="form-label">Current Password</label>
                            <input type="password" name="currentPassword" class="form-control" placeholder="••••••••" required>
                        </div>
                        <div class="form-group" style="margin-bottom:12px;">
                            <label class="form-label">New Password (min 6 chars)</label>
                            <input type="password" name="newPassword" class="form-control" placeholder="••••••••" required minlength="6">
                        </div>
                        <div class="form-group" style="margin-bottom:18px;">
                            <label class="form-label">Confirm New Password</label>
                            <input type="password" name="confirmPassword" class="form-control" placeholder="••••••••" required minlength="6">
                        </div>
                        <button type="submit" class="btn btn-terracotta btn-sm">Update Password</button>
                    </form>
                </div>
            </div>
        </section>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

