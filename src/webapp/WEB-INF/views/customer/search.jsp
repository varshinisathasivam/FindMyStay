<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.Property" %>
<%
    request.setAttribute("pageTitle", "Explore PGs, Hostels & Rooms – FindMyStay");
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    Integer count = (Integer) request.getAttribute("resultCount");
    if (count == null) count = (properties != null) ? properties.size() : 0;

    String selectedCity = (String) request.getAttribute("selectedCity");
    String selectedType = (String) request.getAttribute("selectedType");
    String selectedGender = (String) request.getAttribute("selectedGender");
    String selectedSharing = (String) request.getAttribute("selectedSharing");
    String selectedMinRent = (String) request.getAttribute("selectedMinRent");
    String selectedMaxRent = (String) request.getAttribute("selectedMaxRent");
    Boolean selectedAc = (Boolean) request.getAttribute("selectedAc");
    Boolean selectedFood = (Boolean) request.getAttribute("selectedFood");
    Boolean selectedWifi = (Boolean) request.getAttribute("selectedWifi");
    Boolean selectedBath = (Boolean) request.getAttribute("selectedBath");
    String selectedSort = (String) request.getAttribute("selectedSort");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container" style="padding-top:30px;padding-bottom:80px;">
    <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:24px;">
        <div>
            <h1 style="font-size:30px;color:var(--color-olive);">Explore Accommodations</h1>
            <p style="color:var(--color-charcoal-muted);font-size:14px;">
                Showing <strong><%= count %></strong> verified stays matching your criteria
            </p>
        </div>
        <div>
            <a href="<%= request.getContextPath() %>/stays" class="btn btn-secondary btn-sm">
                <i class="fas fa-redo"></i> Reset All Filters
            </a>
        </div>
    </div>

    <!-- FILTER BAR / CARD -->
    <div style="background:var(--color-white);padding:24px;border-radius:var(--radius-md);border:1px solid var(--border-color);box-shadow:var(--card-shadow);margin-bottom:30px;">
        <form action="<%= request.getContextPath() %>/stays" method="get" id="filterForm">
            <!-- Row 1: Primary Dropdowns -->
            <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(180px, 1fr));gap:16px;margin-bottom:18px;">
                <div class="form-group">
                    <label class="form-label">City / Hub</label>
                    <select name="city" class="form-select" onchange="document.getElementById('filterForm').submit()">
                        <option value="">All Cities</option>
                        <option value="Chennai" <%= "Chennai".equalsIgnoreCase(selectedCity) ? "selected" : "" %>>Chennai</option>
                        <option value="Coimbatore" <%= "Coimbatore".equalsIgnoreCase(selectedCity) ? "selected" : "" %>>Coimbatore</option>
                        <option value="Salem" <%= "Salem".equalsIgnoreCase(selectedCity) ? "selected" : "" %>>Salem</option>
                        <option value="Bangalore" <%= "Bangalore".equalsIgnoreCase(selectedCity) ? "selected" : "" %>>Bangalore</option>
                        <option value="Hyderabad" <%= "Hyderabad".equalsIgnoreCase(selectedCity) ? "selected" : "" %>>Hyderabad</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Property Type</label>
                    <select name="type" class="form-select" onchange="document.getElementById('filterForm').submit()">
                        <option value="">All Types</option>
                        <option value="PG" <%= "PG".equalsIgnoreCase(selectedType) ? "selected" : "" %>>PG (Paying Guest)</option>
                        <option value="HOSTEL" <%= "HOSTEL".equalsIgnoreCase(selectedType) ? "selected" : "" %>>Hostel</option>
                        <option value="ROOM" <%= "ROOM".equalsIgnoreCase(selectedType) ? "selected" : "" %>>Rental Room</option>
                        <option value="FLAT" <%= "FLAT".equalsIgnoreCase(selectedType) ? "selected" : "" %>>Shared Flat</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Gender Preference</label>
                    <select name="gender" class="form-select" onchange="document.getElementById('filterForm').submit()">
                        <option value="">Any Gender</option>
                        <option value="BOYS" <%= "BOYS".equalsIgnoreCase(selectedGender) ? "selected" : "" %>>Boys Only</option>
                        <option value="GIRLS" <%= "GIRLS".equalsIgnoreCase(selectedGender) ? "selected" : "" %>>Girls Only</option>
                        <option value="CO_LIVING" <%= "CO_LIVING".equalsIgnoreCase(selectedGender) ? "selected" : "" %>>Co-Living / Unisex</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Room Sharing</label>
                    <select name="sharing" class="form-select" onchange="document.getElementById('filterForm').submit()">
                        <option value="">All Sharing Options</option>
                        <option value="Single" <%= "Single".equalsIgnoreCase(selectedSharing) ? "selected" : "" %>>Single Private</option>
                        <option value="Double" <%= "Double".equalsIgnoreCase(selectedSharing) ? "selected" : "" %>>Double Sharing</option>
                        <option value="Triple" <%= "Triple".equalsIgnoreCase(selectedSharing) ? "selected" : "" %>>Triple Sharing</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Sort By</label>
                    <select name="sort" class="form-select" onchange="document.getElementById('filterForm').submit()">
                        <option value="">Recommended / Verified First</option>
                        <option value="price_asc" <%= "price_asc".equals(selectedSort) ? "selected" : "" %>>Price: Low to High</option>
                        <option value="price_desc" <%= "price_desc".equals(selectedSort) ? "selected" : "" %>>Price: High to Low</option>
                        <option value="rating_desc" <%= "rating_desc".equals(selectedSort) ? "selected" : "" %>>Highest Rated</option>
                    </select>
                </div>
            </div>

            <!-- Row 2: Budget Inputs & Amenity Toggles -->
            <div style="display:flex;flex-wrap:wrap;justify-content:space-between;align-items:center;padding-top:16px;border-top:1px solid var(--border-color);gap:16px;">
                <div style="display:flex;align-items:center;gap:12px;flex-wrap:wrap;">
                    <span style="font-size:13px;font-weight:600;color:var(--color-olive);">Budget (₹):</span>
                    <input type="number" name="minRent" class="form-control" placeholder="Min Rent" style="width:120px;padding:8px 10px;" value="<%= selectedMinRent != null ? selectedMinRent : "" %>">
                    <span>–</span>
                    <input type="number" name="maxRent" class="form-control" placeholder="Max Rent" style="width:120px;padding:8px 10px;" value="<%= selectedMaxRent != null ? selectedMaxRent : "" %>">
                </div>

                <div style="display:flex;align-items:center;gap:18px;flex-wrap:wrap;font-size:13px;font-weight:600;">
                    <label style="display:flex;align-items:center;gap:6px;cursor:pointer;">
                        <input type="checkbox" name="ac" value="true" <%= Boolean.TRUE.equals(selectedAc) ? "checked" : "" %> onchange="document.getElementById('filterForm').submit()">
                        <i class="fas fa-snowflake" style="color:var(--color-olive);"></i> AC
                    </label>
                    <label style="display:flex;align-items:center;gap:6px;cursor:pointer;">
                        <input type="checkbox" name="food" value="true" <%= Boolean.TRUE.equals(selectedFood) ? "checked" : "" %> onchange="document.getElementById('filterForm').submit()">
                        <i class="fas fa-utensils" style="color:var(--color-terracotta);"></i> Food Included
                    </label>
                    <label style="display:flex;align-items:center;gap:6px;cursor:pointer;">
                        <input type="checkbox" name="wifi" value="true" <%= Boolean.TRUE.equals(selectedWifi) ? "checked" : "" %> onchange="document.getElementById('filterForm').submit()">
                        <i class="fas fa-wifi" style="color:var(--color-olive);"></i> High-Speed Wi-Fi
                    </label>
                    <label style="display:flex;align-items:center;gap:6px;cursor:pointer;">
                        <input type="checkbox" name="bath" value="true" <%= Boolean.TRUE.equals(selectedBath) ? "checked" : "" %> onchange="document.getElementById('filterForm').submit()">
                        <i class="fas fa-bath" style="color:var(--color-olive);"></i> Attached Bath
                    </label>
                </div>

                <button type="submit" class="btn btn-terracotta btn-sm">
                    <i class="fas fa-filter"></i> Apply Filters
                </button>
            </div>
        </form>
    </div>

    <!-- PROPERTIES GRID -->
    <div class="properties-grid">
        <%
            if (properties != null && !properties.isEmpty()) {
                for (Property p : properties) {
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

                    <form action="<%= request.getContextPath() %>/wishlist/toggle" method="post" style="display:inline;">
                        <input type="hidden" name="propertyId" value="<%= p.getId() %>">
                        <input type="hidden" name="redirect" value="<%= request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "") %>">
                        <button type="submit" class="card-wishlist-btn" title="Save to Wishlist">
                            <i class="fas fa-heart"></i>
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
                        <span class="feature-tag"><i class="fas fa-bed"></i> <%= p.getRoomSharing() %></span>
                        <span class="feature-tag"><i class="fas fa-utensils"></i> <%= p.getFoodAvailability().split(" ")[0] %></span>
                        <% if (p.isAcAvailable()) { %>
                            <span class="feature-tag"><i class="fas fa-snowflake"></i> AC</span>
                        <% } %>
                        <% if (p.hasWifi()) { %>
                            <span class="feature-tag"><i class="fas fa-wifi"></i> Wi-Fi</span>
                        <% } %>
                    </div>

                    <!-- Live Room Availability Indicator -->
                    <div style="margin-bottom:14px;">
                        <% if (p.getAvailableRooms() > 2) { %>
                            <span class="card-availability text-success">
                                <span class="avail-dot"></span> <%= p.getAvailableRooms() %> Rooms Available
                            </span>
                        <% } else if (p.getAvailableRooms() > 0) { %>
                            <span class="card-availability" style="color:#F57F17;">
                                <span class="avail-dot few"></span> Only <%= p.getAvailableRooms() %> Rooms Left!
                            </span>
                        <% } else { %>
                            <span class="card-availability text-danger">
                                <span class="avail-dot full"></span> Currently Fully Occupied
                            </span>
                        <% } %>
                    </div>

                    <div class="card-footer">
                        <div class="card-price">
                            <span class="amount">₹<%= String.format("%,.0f", p.getMonthlyRent()) %></span>
                            <span class="period">/ month onwards</span>
                        </div>

                        <div style="display:flex;align-items:center;gap:8px;">
                            <form action="<%= request.getContextPath() %>/compare/add" method="post" style="display:inline;">
                                <input type="hidden" name="propertyId" value="<%= p.getId() %>">
                                <input type="hidden" name="redirect" value="<%= request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "") %>">
                                <button type="submit" class="btn btn-secondary btn-sm" title="Add to Compare">
                                    <i class="fas fa-columns"></i>
                                </button>
                            </form>

                            <a href="<%= request.getContextPath() %>/property?id=<%= p.getId() %>" class="btn btn-primary btn-sm">
                                View Details
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        <%
                }
            } else {
        %>
            <div style="grid-column:1/-1;background:var(--color-white);padding:60px 20px;text-align:center;border-radius:var(--radius-md);border:1px dashed var(--border-color);">
                <i class="fas fa-search" style="font-size:40px;color:var(--color-sage);margin-bottom:16px;"></i>
                <h3 style="font-size:22px;color:var(--color-olive);margin-bottom:8px;">No stays matched your exact filters</h3>
                <p style="color:var(--color-charcoal-muted);margin-bottom:20px;">Try relaxing your budget, selecting "All Cities", or unchecking specific amenities.</p>
                <a href="<%= request.getContextPath() %>/stays" class="btn btn-primary">Clear All Filters</a>
            </div>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

