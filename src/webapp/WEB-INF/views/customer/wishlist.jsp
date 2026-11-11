<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.Wishlist" %>
<%@ page import="com.findmystay.model.Property" %>
<%
    request.setAttribute("pageTitle", "Saved Stays – My Wishlist – FindMyStay");
    List<Wishlist> wishlists = (List<Wishlist>) request.getAttribute("wishlists");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container" style="padding-top:40px;padding-bottom:80px;">
    <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

    <div style="margin-bottom:30px;">
        <h1 style="font-size:30px;color:var(--color-olive);">My Saved Wishlist</h1>
        <p style="color:var(--color-charcoal-muted);font-size:14px;">
            Quickly compare and track stays you are considering
        </p>
    </div>

    <% if (wishlists != null && !wishlists.isEmpty()) { %>
        <div class="properties-grid">
            <% for (Wishlist w : wishlists) {
                Property p = w.getProperty();
                if (p == null) continue;
            %>
                <div class="property-card">
                    <div class="card-image-wrap">
                        <img src="<%= request.getContextPath() %>/<%= p.getFeaturedImage() %>" alt="<%= p.getTitle() %>">
                        <div class="card-badges">
                            <% if (p.isVerified()) { %>
                                <span class="badge badge-verified"><i class="fas fa-check-shield"></i> Verified</span>
                            <% } %>
                            <span class="badge <%= p.getGenderBadgeClass() %>"><%= p.getGenderPreference() %></span>
                        </div>

                        <!-- Remove Button -->
                        <form action="<%= request.getContextPath() %>/wishlist/remove" method="post" style="display:inline;">
                            <input type="hidden" name="propertyId" value="<%= p.getId() %>">
                            <input type="hidden" name="redirect" value="<%= request.getContextPath() %>/wishlist">
                            <button type="submit" class="card-wishlist-btn active" title="Remove from Wishlist">
                                <i class="fas fa-times"></i>
                            </button>
                        </form>
                    </div>

                    <div class="card-body">
                        <div class="card-meta-row">
                            <span class="badge badge-neutral"><%= p.getPropertyType() %></span>
                            <div class="card-rating">
                                <i class="fas fa-star"></i> <%= p.getRating() %>
                            </div>
                        </div>

                        <h3 class="card-title">
                            <a href="<%= request.getContextPath() %>/property?id=<%= p.getId() %>"><%= p.getTitle() %></a>
                        </h3>

                        <div class="card-location">
                            <i class="fas fa-map-marker-alt"></i> <%= p.getAddress() %>, <strong><%= p.getCity() %></strong>
                        </div>

                        <div class="card-features">
                            <span class="feature-tag"><%= p.getRoomSharing() %></span>
                            <span class="feature-tag"><%= p.getFoodAvailability().split(" ")[0] %></span>
                        </div>

                        <div class="card-footer">
                            <div class="card-price">
                                <span class="amount">₹<%= String.format("%,.0f", p.getMonthlyRent()) %></span>
                                <span class="period">/ month</span>
                            </div>

                            <a href="<%= request.getContextPath() %>/property?id=<%= p.getId() %>" class="btn btn-primary btn-sm">
                                View Details
                            </a>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    <% } else { %>
        <div style="background:var(--color-white);padding:60px 20px;text-align:center;border-radius:var(--radius-md);border:1px dashed var(--border-color);max-width:600px;margin:0 auto;">
            <i class="fas fa-heart-broken" style="font-size:48px;color:var(--color-sage);margin-bottom:16px;"></i>
            <h3 style="font-size:22px;color:var(--color-olive);margin-bottom:8px;">Your Wishlist is Empty</h3>
            <p style="color:var(--color-charcoal-muted);margin-bottom:24px;">Click the heart icon on any stay card while browsing to save it here for later.</p>
            <a href="<%= request.getContextPath() %>/stays" class="btn btn-terracotta">Explore Stays Now</a>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

