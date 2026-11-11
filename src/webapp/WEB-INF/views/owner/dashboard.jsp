<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.*" %>
<%
    request.setAttribute("pageTitle", "Owner Dashboard – FindMyStay");
    User owner = (User) session.getAttribute("currentUser");
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    List<Enquiry> enquiries = (List<Enquiry>) request.getAttribute("enquiries");

    Integer totalProps = (Integer) request.getAttribute("totalProperties");
    Integer totalBookings = (Integer) request.getAttribute("totalBookings");
    Integer pendingBookings = (Integer) request.getAttribute("pendingBookings");
    Integer approvedBookings = (Integer) request.getAttribute("approvedBookings");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container dashboard-layout">
    <!-- LEFT SIDEBAR -->
    <aside class="dashboard-sidebar">
        <div class="user-profile-summary">
            <div class="avatar-large" style="background:var(--color-terracotta);">
                <i class="fas fa-building"></i>
            </div>
            <h3 style="font-size:18px;color:var(--color-olive);margin-bottom:4px;"><%= owner != null ? owner.getFullName() : "Host" %></h3>
            <span class="badge badge-warning" style="margin-bottom:8px;">Property Owner</span>
            <div style="font-size:12px;color:var(--color-charcoal-muted);"><%= owner != null ? owner.getEmail() : "" %></div>
        </div>

        <ul class="sidebar-nav">
            <li><a href="#properties" class="sidebar-link active"><i class="fas fa-home"></i> My Properties (<%= totalProps != null ? totalProps : 0 %>)</a></li>
            <li><a href="<%= request.getContextPath() %>/owner/property/add" class="sidebar-link" style="color:var(--color-terracotta);"><i class="fas fa-plus-circle"></i> Add New Stay</a></li>
            <li><a href="#bookings" class="sidebar-link"><i class="fas fa-calendar-check"></i> Bookings (<%= pendingBookings != null ? pendingBookings : 0 %> pending)</a></li>
            <li><a href="#enquiries" class="sidebar-link"><i class="fas fa-envelope"></i> Customer Enquiries</a></li>
            <li><a href="<%= request.getContextPath() %>/logout" class="sidebar-link" style="color:#C62828;"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
        </ul>
    </aside>

    <!-- RIGHT MAIN CONTENT -->
    <main class="dashboard-main">
        <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

        <!-- STATS CARDS -->
        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon olive"><i class="fas fa-hotel"></i></div>
                <div class="stat-info">
                    <h3><%= totalProps != null ? totalProps : 0 %></h3>
                    <p>Listed Stays</p>
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
                    <p>Approved Bookings</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon terracotta"><i class="fas fa-comments"></i></div>
                <div class="stat-info">
                    <h3><%= enquiries != null ? enquiries.size() : 0 %></h3>
                    <p>Enquiries Received</p>
                </div>
            </div>
        </section>

        <!-- PROPERTY INVENTORY TABLE -->
        <section id="properties" class="dashboard-card">
            <div class="dashboard-card-header">
                <div>
                    <h2 class="dashboard-card-title"><i class="fas fa-list"></i> My Accommodation Listings</h2>
                    <p style="font-size:13px;color:var(--color-charcoal-muted);margin-top:2px;">Manage details, edit pricing, or update live room availability.</p>
                </div>
                <a href="<%= request.getContextPath() %>/owner/property/add" class="btn btn-terracotta btn-sm">
                    <i class="fas fa-plus"></i> Post New Property
                </a>
            </div>

            <% if (properties != null && !properties.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Listing</th>
                                <th>Type</th>
                                <th>Monthly Rent</th>
                                <th>Live Availability</th>
                                <th>Admin Verification</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Property p : properties) { %>
                                <tr>
                                    <td>
                                        <div style="display:flex;align-items:center;gap:12px;">
                                            <img src="<%= request.getContextPath() %>/<%= p.getFeaturedImage() %>" alt="<%= p.getTitle() %>" style="width:48px;height:48px;border-radius:6px;object-fit:cover;">
                                            <div>
                                                <strong><a href="<%= request.getContextPath() %>/property?id=<%= p.getId() %>" style="color:var(--color-olive);"><%= p.getTitle() %></a></strong>
                                                <div style="font-size:12px;color:var(--color-charcoal-muted);"><%= p.getCity() %> &bull; <%= p.getRoomSharing() %></div>
                                            </div>
                                        </div>
                                    </td>
                                    <td><span class="badge badge-neutral"><%= p.getPropertyType() %></span></td>
                                    <td style="font-weight:700;color:var(--color-terracotta);">₹<%= String.format("%,.0f", p.getMonthlyRent()) %></td>
                                    <td>
                                        <!-- Inline Live Availability Updater -->
                                        <form action="<%= request.getContextPath() %>/owner/availability" method="post" style="display:flex;align-items:center;gap:6px;">
                                            <input type="hidden" name="propertyId" value="<%= p.getId() %>">
                                            <input type="number" name="availableRooms" value="<%= p.getAvailableRooms() %>" min="0" max="<%= p.getTotalRooms() %>" style="width:60px;padding:4px 6px;font-size:13px;border-radius:4px;border:1px solid var(--border-color);">
                                            <button type="submit" class="btn btn-secondary btn-sm" style="padding:4px 8px;" title="Update Live Count">
                                                <i class="fas fa-sync-alt"></i>
                                            </button>
                                        </form>
                                    </td>
                                    <td>
                                        <% if (p.isVerified()) { %>
                                            <span class="badge badge-verified"><i class="fas fa-check"></i> Verified</span>
                                        <% } else { %>
                                            <span class="badge badge-warning"><i class="fas fa-clock"></i> Pending Review</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <div style="display:flex;gap:6px;">
                                            <a href="<%= request.getContextPath() %>/owner/property/edit?id=<%= p.getId() %>" class="btn btn-secondary btn-sm" title="Edit Property">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <form action="<%= request.getContextPath() %>/owner/property/delete" method="post" onsubmit="return confirm('Delete this property permanently?')">
                                                <input type="hidden" name="id" value="<%= p.getId() %>">
                                                <button type="submit" class="btn btn-outline btn-sm" style="color:#C62828;border-color:#C62828;" title="Delete">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div style="text-align:center;padding:50px 20px;color:var(--color-charcoal-muted);">
                    <i class="fas fa-home" style="font-size:40px;color:var(--color-sage);margin-bottom:14px;"></i>
                    <p>You haven't listed any properties yet.</p>
                    <a href="<%= request.getContextPath() %>/owner/property/add" class="btn btn-terracotta btn-sm" style="margin-top:12px;">Add Your First Stay</a>
                </div>
            <% } %>
        </section>

        <!-- BOOKING REQUESTS -->
        <section id="bookings" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-calendar-alt"></i> Booking Requests</h2>
            </div>

            <% if (bookings != null && !bookings.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Ref &amp; Stay</th>
                                <th>Tenant Name</th>
                                <th>Phone</th>
                                <th>Move-In Date</th>
                                <th>Duration</th>
                                <th>Total Rent</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Booking b : bookings) { %>
                                <tr>
                                    <td>
                                        <strong><%= b.getBookingRef() %></strong>
                                        <div style="font-size:12px;color:var(--color-charcoal-muted);"><%= b.getPropertyTitle() %></div>
                                    </td>
                                    <td><strong><%= b.getCustomerName() %></strong></td>
                                    <td><%= b.getContactNumber() %></td>
                                    <td><%= b.getMoveInDate() %></td>
                                    <td><%= b.getDurationMonths() %> mo</td>
                                    <td style="font-weight:700;color:var(--color-terracotta);">₹<%= String.format("%,.0f", b.getTotalAmount()) %></td>
                                    <td><span class="badge <%= b.getStatusBadgeClass() %>"><%= b.getStatus() %></span></td>
                                    <td>
                                        <% if (b.getStatus() == Booking.BookingStatus.PENDING) { %>
                                            <div style="display:flex;gap:6px;">
                                                <form action="<%= request.getContextPath() %>/owner/booking/status" method="post" style="display:inline;">
                                                    <input type="hidden" name="bookingId" value="<%= b.getId() %>">
                                                    <input type="hidden" name="status" value="APPROVED">
                                                    <button type="submit" class="btn btn-primary btn-sm" style="background:#2E7D32;" title="Approve Request">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                </form>
                                                <form action="<%= request.getContextPath() %>/owner/booking/status" method="post" style="display:inline;">
                                                    <input type="hidden" name="bookingId" value="<%= b.getId() %>">
                                                    <input type="hidden" name="status" value="REJECTED">
                                                    <button type="submit" class="btn btn-outline btn-sm" style="color:#C62828;border-color:#C62828;" title="Reject Request">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        <% } else { %>
                                            <span style="font-size:12px;color:var(--color-charcoal-muted);"><%= b.getStatus() %></span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p style="text-align:center;padding:30px;color:var(--color-charcoal-muted);">No booking requests received yet.</p>
            <% } %>
        </section>

        <!-- CUSTOMER ENQUIRIES -->
        <section id="enquiries" class="dashboard-card">
            <div class="dashboard-card-header">
                <h2 class="dashboard-card-title"><i class="fas fa-envelope-open-text"></i> Customer Inquiries</h2>
            </div>

            <% if (enquiries != null && !enquiries.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Property</th>
                                <th>Visitor / Tenant</th>
                                <th>Contact Info</th>
                                <th>Question</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Enquiry e : enquiries) { %>
                                <tr>
                                    <td><strong><%= e.getPropertyTitle() %></strong></td>
                                    <td><%= e.getSenderName() %></td>
                                    <td><%= e.getSenderPhone() %><br><span style="font-size:12px;color:var(--color-charcoal-muted);"><%= e.getSenderEmail() %></span></td>
                                    <td><%= e.getMessage() %></td>
                                    <td>
                                        <span class="badge <%= e.getStatus() == Enquiry.EnquiryStatus.NEW ? "badge-warning" : "badge-success" %>"><%= e.getStatus() %></span>
                                    </td>
                                    <td>
                                        <% if (e.getStatus() == Enquiry.EnquiryStatus.NEW) { %>
                                            <form action="<%= request.getContextPath() %>/owner/enquiry/status" method="post">
                                                <input type="hidden" name="enquiryId" value="<%= e.getId() %>">
                                                <input type="hidden" name="status" value="CONTACTED">
                                                <button type="submit" class="btn btn-secondary btn-sm">Mark Contacted</button>
                                            </form>
                                        <% } else { %>
                                            <span style="font-size:12px;color:#2E7D32;"><i class="fas fa-check"></i> Resolved</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p style="text-align:center;padding:30px;color:var(--color-charcoal-muted);">No customer inquiries right now.</p>
            <% } %>
        </section>
    </main>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

