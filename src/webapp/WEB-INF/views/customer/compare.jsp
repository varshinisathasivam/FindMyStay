<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.PropertyComparison" %>
<%@ page import="com.findmystay.model.Property" %>
<%
    request.setAttribute("pageTitle", "Compare Accommodations – FindMyStay");
    PropertyComparison comparison = (PropertyComparison) request.getAttribute("comparison");
    List<Property> props = (comparison != null) ? comparison.getProperties() : null;
    int count = (props != null) ? props.size() : 0;
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container compare-container">
    <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:28px;">
        <div>
            <h1 style="font-size:30px;color:var(--color-olive);">Compare Accommodations (<%= count %>/3)</h1>
            <p style="color:var(--color-charcoal-muted);font-size:14px;">
                Evaluate rent, amenities, rules and facilities side-by-side to make the smart choice
            </p>
        </div>

        <% if (count > 0) { %>
            <form action="<%= request.getContextPath() %>/compare/clear" method="post">
                <button type="submit" class="btn btn-secondary btn-sm">
                    <i class="fas fa-trash"></i> Clear Comparison
                </button>
            </form>
        <% } %>
    </div>

    <% if (count == 0) { %>
        <div style="background:var(--color-white);padding:60px 20px;text-align:center;border-radius:var(--radius-lg);border:1px dashed var(--border-color);max-width:600px;margin:0 auto;">
            <i class="fas fa-columns" style="font-size:48px;color:var(--color-sage);margin-bottom:16px;"></i>
            <h3 style="font-size:22px;color:var(--color-olive);margin-bottom:8px;">No Stays Added to Compare</h3>
            <p style="color:var(--color-charcoal-muted);margin-bottom:24px;">Browse any property and click the <strong>Compare</strong> button to add up to 3 stays here side-by-side.</p>
            <a href="<%= request.getContextPath() %>/stays" class="btn btn-primary">Browse Stays</a>
        </div>
    <% } else { %>
        <div class="table-responsive">
            <table class="compare-table">
                <!-- Header: Images & Names -->
                <tr>
                    <th>Property</th>
                    <% for (Property p : props) { %>
                        <td class="compare-header-cell">
                            <form action="<%= request.getContextPath() %>/compare/remove" method="post">
                                <input type="hidden" name="propertyId" value="<%= p.getId() %>">
                                <button type="submit" class="compare-remove-btn" title="Remove">&times;</button>
                            </form>
                            <img src="<%= request.getContextPath() %>/<%= p.getFeaturedImage() %>" alt="<%= p.getTitle() %>" class="compare-prop-img">
                            <h4 style="font-size:17px;color:var(--color-olive);margin-bottom:6px;"><%= p.getTitle() %></h4>
                            <span class="badge badge-neutral"><%= p.getPropertyType() %></span>
                            <span class="badge <%= p.getGenderBadgeClass() %>"><%= p.getGenderPreference() %></span>
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %>
                        <td class="empty-compare-slot">
                            <i class="fas fa-plus-circle" style="font-size:28px;color:var(--color-sage);margin-bottom:8px;display:block;"></i>
                            <span>Add Stay (<%= i + 1 %> of 3)</span>
                            <div style="margin-top:10px;">
                                <a href="<%= request.getContextPath() %>/stays" class="btn btn-secondary btn-sm">+ Choose</a>
                            </div>
                        </td>
                    <% } %>
                </tr>

                <!-- Monthly Rent -->
                <tr>
                    <th>Monthly Rent</th>
                    <% for (Property p : props) { %>
                        <td>
                            <span style="font-family:var(--font-heading);font-size:20px;font-weight:800;color:var(--color-terracotta);">
                                ₹<%= String.format("%,.0f", p.getMonthlyRent()) %>
                            </span>
                            <span style="font-size:12px;color:var(--color-charcoal-muted);">/ mo</span>
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Security Deposit -->
                <tr>
                    <th>Security Deposit</th>
                    <% for (Property p : props) { %>
                        <td>₹<%= String.format("%,.0f", p.getSecurityDeposit()) %></td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Location -->
                <tr>
                    <th>City &amp; Location</th>
                    <% for (Property p : props) { %>
                        <td><strong><%= p.getCity() %></strong><br><span style="font-size:13px;color:var(--color-charcoal-muted);"><%= p.getAddress() %></span></td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Room Types & Sharing -->
                <tr>
                    <th>Room Sharing</th>
                    <% for (Property p : props) { %>
                        <td><%= p.getRoomSharing() %></td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Food Included -->
                <tr>
                    <th>Food Availability</th>
                    <% for (Property p : props) { %>
                        <td><%= p.getFoodAvailability() %></td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Air Conditioning -->
                <tr>
                    <th>Air Conditioning</th>
                    <% for (Property p : props) { %>
                        <td>
                            <% if (p.isAcAvailable()) { %>
                                <span class="feature-check"><i class="fas fa-check"></i> AC Available</span>
                            <% } else { %>
                                <span class="feature-cross"><i class="fas fa-times"></i> Non-AC Only</span>
                            <% } %>
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Wi-Fi -->
                <tr>
                    <th>High-Speed Wi-Fi</th>
                    <% for (Property p : props) { %>
                        <td>
                            <% if (p.hasWifi()) { %>
                                <span class="feature-check"><i class="fas fa-check"></i> High-Speed Included</span>
                            <% } else { %>
                                <span class="feature-cross"><i class="fas fa-times"></i> Not Available</span>
                            <% } %>
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Laundry -->
                <tr>
                    <th>Washing Machine / Laundry</th>
                    <% for (Property p : props) { %>
                        <td>
                            <% if (p.hasAmenity("Laundry") || p.hasAmenity("Washing")) { %>
                                <span class="feature-check"><i class="fas fa-check"></i> Laundry Included</span>
                            <% } else { %>
                                <span class="feature-cross"><i class="fas fa-times"></i> Self / Paid Laundry</span>
                            <% } %>
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Rating -->
                <tr>
                    <th>Resident Rating</th>
                    <% for (Property p : props) { %>
                        <td>
                            <strong style="font-size:16px;"><i class="fas fa-star" style="color:#F4B400;"></i> <%= p.getRating() %></strong> / 5.0
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Live Availability -->
                <tr>
                    <th>Live Room Availability</th>
                    <% for (Property p : props) { %>
                        <td>
                            <% if (p.getAvailableRooms() > 0) { %>
                                <span style="color:#2E7D32;font-weight:700;"><%= p.getAvailableRooms() %> Rooms Open</span>
                            <% } else { %>
                                <span style="color:#C62828;font-weight:700;">Fully Booked</span>
                            <% } %>
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>

                <!-- Action Button -->
                <tr>
                    <th>Actions</th>
                    <% for (Property p : props) { %>
                        <td style="text-align:center;">
                            <a href="<%= request.getContextPath() %>/property?id=<%= p.getId() %>" class="btn btn-primary btn-sm" style="width:100%;margin-bottom:6px;">
                                View Details
                            </a>
                            <a href="<%= request.getContextPath() %>/book?propertyId=<%= p.getId() %>" class="btn btn-terracotta btn-sm" style="width:100%;">
                                Book Now
                            </a>
                        </td>
                    <% } %>
                    <% for (int i = count; i < 3; i++) { %><td>-</td><% } %>
                </tr>
            </table>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

