<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.*" %>
<%
    Property property = (Property) request.getAttribute("property");
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
    List<Property> budgetSuggestions = (List<Property>) request.getAttribute("budgetSuggestions");
    Boolean isWishlisted = (Boolean) request.getAttribute("isWishlisted");
    User currentUser = (User) session.getAttribute("currentUser");

    if (property != null) {
        request.setAttribute("pageTitle", property.getTitle() + " – FindMyStay");
    }
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container" style="padding-top:30px;padding-bottom:80px;">
    <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

    <% if (property != null) { %>
        <!-- BREADCRUMB & TITLE -->
        <div class="prop-header">
            <div style="font-size:13px;color:var(--color-charcoal-muted);margin-bottom:8px;">
                <a href="<%= request.getContextPath() %>/">Home</a> &gt; 
                <a href="<%= request.getContextPath() %>/stays?city=<%= property.getCity() %>"><%= property.getCity() %></a> &gt; 
                <span><%= property.getTitle() %></span>
            </div>

            <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:16px;">
                <div>
                    <h1 class="prop-title"><%= property.getTitle() %></h1>
                    <div class="prop-meta-bar">
                        <% if (property.isVerified()) { %>
                            <span class="badge badge-verified"><i class="fas fa-check-shield"></i> Verified Stay</span>
                        <% } %>
                        <span class="badge <%= property.getGenderBadgeClass() %>"><%= property.getGenderPreference() %></span>
                        <span class="badge badge-neutral"><%= property.getPropertyType() %></span>
                        <span><i class="fas fa-map-marker-alt text-danger"></i> <%= property.getAddress() %>, <strong><%= property.getCity() %></strong></span>
                        <span style="font-weight:700;color:var(--color-charcoal);"><i class="fas fa-star" style="color:#F4B400;"></i> <%= property.getRating() %> (<%= reviews != null ? reviews.size() : 0 %> reviews)</span>
                    </div>
                </div>

                <div style="display:flex;gap:10px;">
                    <form action="<%= request.getContextPath() %>/compare/add" method="post">
                        <input type="hidden" name="propertyId" value="<%= property.getId() %>">
                        <input type="hidden" name="redirect" value="<%= request.getRequestURI() %>?id=<%= property.getId() %>">
                        <button type="submit" class="btn btn-secondary btn-sm">
                            <i class="fas fa-columns"></i> Add to Compare
                        </button>
                    </form>

                    <form action="<%= request.getContextPath() %>/wishlist/toggle" method="post">
                        <input type="hidden" name="propertyId" value="<%= property.getId() %>">
                        <input type="hidden" name="redirect" value="<%= request.getRequestURI() %>?id=<%= property.getId() %>">
                        <button type="submit" class="btn <%= Boolean.TRUE.equals(isWishlisted) ? "btn-outline-terracotta" : "btn-secondary" %> btn-sm">
                            <i class="fas fa-heart <%= Boolean.TRUE.equals(isWishlisted) ? "text-danger" : "" %>"></i> 
                            <%= Boolean.TRUE.equals(isWishlisted) ? "Saved in Wishlist" : "Save to Wishlist" %>
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <!-- IMAGE GALLERY -->
        <div class="gallery-grid">
            <div class="gallery-item gallery-main">
                <img src="<%= request.getContextPath() %>/<%= property.getFeaturedImage() %>" alt="<%= property.getTitle() %>">
            </div>
            <div class="gallery-item">
                <img src="<%= request.getContextPath() %>/<%= property.getFeaturedImage() %>" alt="Room View">
            </div>
            <div class="gallery-item">
                <img src="<%= request.getContextPath() %>/assets/images/stays/chennai-pg2.svg" alt="Living Lounge">
            </div>
        </div>

        <!-- MAIN DETAILS LAYOUT -->
        <div class="property-details-layout">
            <!-- LEFT COLUMN: MAIN CONTENT -->
            <div class="property-main-col">
                <!-- Overview & Description -->
                <div class="prop-section">
                    <h3 class="prop-section-title"><i class="fas fa-align-left"></i> About This Stay</h3>
                    <p style="color:var(--color-charcoal);line-height:1.7;font-size:15px;margin-bottom:18px;">
                        <%= property.getDescription() %>
                    </p>

                    <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(180px, 1fr));gap:16px;background:var(--bg-cream);padding:18px;border-radius:var(--radius-sm);border:1px solid var(--border-color);">
                        <div>
                            <span style="font-size:12px;color:var(--color-charcoal-muted);display:block;">Accommodation Type</span>
                            <strong style="color:var(--color-olive);"><%= property.getPropertyType() %></strong>
                        </div>
                        <div>
                            <span style="font-size:12px;color:var(--color-charcoal-muted);display:block;">Gender Preference</span>
                            <strong style="color:var(--color-olive);"><%= property.getGenderPreference() %></strong>
                        </div>
                        <div>
                            <span style="font-size:12px;color:var(--color-charcoal-muted);display:block;">Room Sharing</span>
                            <strong style="color:var(--color-olive);"><%= property.getRoomSharing() %></strong>
                        </div>
                        <div>
                            <span style="font-size:12px;color:var(--color-charcoal-muted);display:block;">Live Availability</span>
                            <strong style="color:#2E7D32;"><%= property.getAvailableRooms() %> of <%= property.getTotalRooms() %> Rooms Open</strong>
                        </div>
                    </div>
                </div>

                <!-- Room Types & Rates -->
                <div class="prop-section">
                    <h3 class="prop-section-title"><i class="fas fa-bed"></i> Room Types &amp; Pricing</h3>
                    <%
                        List<RoomType> roomTypes = property.getRoomTypes();
                        if (roomTypes != null && !roomTypes.isEmpty()) {
                            for (RoomType rt : roomTypes) {
                    %>
                        <div class="room-type-card">
                            <div class="room-type-info">
                                <h4><%= rt.getName() %></h4>
                                <div class="room-type-features">
                                    <span><i class="fas fa-<%= rt.isHasAc() ? "snowflake text-info" : "fan text-muted" %>"></i> <%= rt.isHasAc() ? "Air Conditioned" : "Non-AC" %></span>
                                    <span>&bull;</span>
                                    <span><i class="fas fa-bath text-primary"></i> <%= rt.isHasAttachedBath() ? "Attached Washroom" : "Common Washroom" %></span>
                                    <span>&bull;</span>
                                    <span style="color:#2E7D32;font-weight:600;"><%= rt.getAvailableCount() > 0 ? rt.getAvailableCount() + " Available" : "Sold Out" %></span>
                                </div>
                            </div>
                            <div class="room-type-price">
                                <div class="price">₹<%= String.format("%,.0f", rt.getRent()) %></div>
                                <span style="font-size:12px;color:var(--color-charcoal-muted);">per person / month</span>
                            </div>
                        </div>
                    <%
                            }
                        } else {
                    %>
                        <div class="room-type-card">
                            <div class="room-type-info">
                                <h4>Standard Room</h4>
                                <div class="room-type-features">
                                    <span><%= property.getRoomSharing() %></span>
                                </div>
                            </div>
                            <div class="room-type-price">
                                <div class="price">₹<%= String.format("%,.0f", property.getMonthlyRent()) %></div>
                                <span style="font-size:12px;color:var(--color-charcoal-muted);">per month</span>
                            </div>
                        </div>
                    <% } %>
                </div>

                <!-- Amenities & Facilities -->
                <div class="prop-section">
                    <h3 class="prop-section-title"><i class="fas fa-concierge-bell"></i> Facilities &amp; Amenities</h3>
                    <div class="amenities-grid">
                        <%
                            List<Amenity> amenities = property.getAmenities();
                            if (amenities != null && !amenities.isEmpty()) {
                                for (Amenity a : amenities) {
                        %>
                            <div class="amenity-item">
                                <i class="<%= a.getIconClass() %>"></i>
                                <span><%= a.getName() %></span>
                            </div>
                        <%
                                }
                            } else {
                        %>
                            <div class="amenity-item"><i class="fas fa-wifi"></i> High-Speed Wi-Fi</div>
                            <div class="amenity-item"><i class="fas fa-utensils"></i> Nutritious Meals</div>
                            <div class="amenity-item"><i class="fas fa-bolt"></i> Power Backup</div>
                            <div class="amenity-item"><i class="fas fa-shield-alt"></i> CCTV Security</div>
                        <% } %>
                    </div>
                </div>

                <!-- Food & Dining -->
                <div class="prop-section">
                    <h3 class="prop-section-title"><i class="fas fa-utensils"></i> Food &amp; Dining Details</h3>
                    <p style="color:var(--color-charcoal);font-size:15px;line-height:1.6;">
                        <strong>Meal Provision:</strong> <%= property.getFoodAvailability() %>
                    </p>
                    <p style="color:var(--color-charcoal-muted);font-size:14px;margin-top:6px;">
                        Hygienic home-style food cooked daily with fresh ingredients. Filtered RO drinking water available 24/7 on every floor.
                    </p>
                </div>

                <!-- Safety Information & House Rules -->
                <div class="prop-section">
                    <h3 class="prop-section-title"><i class="fas fa-shield-alt"></i> Property Safety &amp; House Rules</h3>
                    <div style="margin-bottom:16px;">
                        <h5 style="color:var(--color-olive);font-size:15px;margin-bottom:4px;">Safety Measures:</h5>
                        <p style="color:var(--color-charcoal);font-size:14px;"><%= property.getSafetyInfo() %></p>
                    </div>
                    <div>
                        <h5 style="color:var(--color-olive);font-size:15px;margin-bottom:4px;">House Rules &amp; Timings:</h5>
                        <p style="color:var(--color-charcoal);font-size:14px;"><%= property.getHouseRules() %></p>
                    </div>
                </div>

                <!-- Nearby Colleges & Hospitals -->
                <div class="prop-section">
                    <h3 class="prop-section-title"><i class="fas fa-map-marked-alt"></i> Nearby Landmarks, Colleges &amp; Hospitals</h3>
                    <p style="color:var(--color-charcoal);font-size:14px;line-height:1.6;">
                        <%= property.getNearbyPlaces() %>
                    </p>
                </div>

                <!-- Customer Reviews Section -->
                <div class="prop-section">
                    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px;">
                        <h3 class="prop-section-title" style="margin-bottom:0;"><i class="fas fa-comments"></i> Resident Reviews (<%= reviews != null ? reviews.size() : 0 %>)</h3>
                        <div style="font-size:18px;font-weight:700;color:var(--color-charcoal);">
                            <i class="fas fa-star" style="color:#F4B400;"></i> <%= property.getRating() %> / 5.0
                        </div>
                    </div>

                    <!-- Reviews List -->
                    <%
                        if (reviews != null && !reviews.isEmpty()) {
                            for (Review r : reviews) {
                    %>
                        <div class="review-item">
                            <div class="review-author">
                                <span><%= r.getUserName() %></span>
                                <span class="review-date"><%= r.getCreatedAt() != null ? r.getCreatedAt().toString().substring(0, 10) : "" %></span>
                            </div>
                            <div style="color:#F4B400;font-size:12px;margin-bottom:6px;">
                                <% for (int i = 0; i < r.getRating(); i++) { %><i class="fas fa-star"></i><% } %>
                            </div>
                            <p style="color:var(--color-charcoal);font-size:14px;line-height:1.5;"><%= r.getComment() %></p>
                        </div>
                    <%
                            }
                        } else {
                    %>
                        <p style="color:var(--color-charcoal-muted);font-size:14px;padding:10px 0;">No resident reviews yet. Be the first to share your experience!</p>
                    <% } %>

                    <!-- Add Review Form -->
                    <div style="background:var(--bg-cream);padding:22px;border-radius:var(--radius-sm);margin-top:24px;border:1px solid var(--border-color);">
                        <h4 style="font-size:16px;color:var(--color-olive);margin-bottom:12px;">Leave a Review</h4>
                        <% if (currentUser != null) { %>
                            <form action="<%= request.getContextPath() %>/review/add" method="post">
                                <input type="hidden" name="propertyId" value="<%= property.getId() %>">
                                <div style="display:flex;gap:16px;align-items:center;margin-bottom:12px;">
                                    <label class="form-label" style="margin-bottom:0;">Rating:</label>
                                    <select name="rating" class="form-select" style="width:140px;">
                                        <option value="5">5 Stars - Excellent</option>
                                        <option value="4">4 Stars - Very Good</option>
                                        <option value="3">3 Stars - Average</option>
                                        <option value="2">2 Stars - Poor</option>
                                        <option value="1">1 Star - Terrible</option>
                                    </select>
                                </div>
                                <div class="form-group" style="margin-bottom:14px;">
                                    <textarea name="comment" class="form-control" rows="3" placeholder="Describe the cleanliness, food quality, safety, and staff behaviour..." required></textarea>
                                </div>
                                <button type="submit" class="btn btn-terracotta btn-sm">Submit Review</button>
                            </form>
                        <% } else { %>
                            <p style="font-size:13px;color:var(--color-charcoal-muted);">
                                Please <a href="<%= request.getContextPath() %>/login?redirect=<%= request.getContextPath() %>/property?id=<%= property.getId() %>" style="color:var(--color-terracotta);font-weight:600;">login</a> to submit your review.
                            </p>
                        <% } %>
                    </div>
                </div>
            </div>

            <!-- RIGHT COLUMN: STICKY BOOKING CARD & OWNER INFO -->
            <div class="property-sidebar-col">
                <div class="sticky-sidebar">
                    <div class="booking-box">
                        <div class="booking-box-header">
                            <div>
                                <span style="font-size:13px;color:var(--color-charcoal-muted);display:block;">Starting From</span>
                                <span class="booking-box-price">₹<%= String.format("%,.0f", property.getMonthlyRent()) %></span>
                                <span style="font-size:13px;color:var(--color-charcoal-muted);">/ month</span>
                            </div>
                            <div style="text-align:right;">
                                <span style="font-size:12px;color:var(--color-charcoal-muted);display:block;">Security Deposit</span>
                                <strong style="color:var(--color-olive);">₹<%= String.format("%,.0f", property.getSecurityDeposit()) %></strong>
                            </div>
                        </div>

                        <!-- Live Availability Pill -->
                        <div style="background:var(--bg-cream);padding:12px;border-radius:var(--radius-sm);margin-bottom:20px;text-align:center;">
                            <% if (property.getAvailableRooms() > 2) { %>
                                <span style="color:#2E7D32;font-weight:700;font-size:14px;">
                                    <i class="fas fa-check-circle"></i> <%= property.getAvailableRooms() %> Rooms Ready for Move-In
                                </span>
                            <% } else if (property.getAvailableRooms() > 0) { %>
                                <span style="color:#F57F17;font-weight:700;font-size:14px;">
                                    <i class="fas fa-exclamation-triangle"></i> Fast Filling: Only <%= property.getAvailableRooms() %> Rooms Left!
                                </span>
                            <% } else { %>
                                <span style="color:#C62828;font-weight:700;font-size:14px;">
                                    <i class="fas fa-times-circle"></i> Currently Fully Booked
                                </span>
                            <% } %>
                        </div>

                        <!-- Action Buttons -->
                        <a href="<%= request.getContextPath() %>/book?propertyId=<%= property.getId() %>" 
                           class="btn btn-terracotta btn-lg" style="width:100%;margin-bottom:12px;">
                            <i class="fas fa-calendar-check"></i> Request Booking Now
                        </a>

                        <button type="button" class="btn btn-outline btn-lg" style="width:100%;" 
                                onclick="document.getElementById('enquiryModal').style.display='block'">
                            <i class="fas fa-envelope"></i> Send Enquiry to Owner
                        </button>

                        <div style="display:flex;align-items:center;justify-content:center;gap:6px;margin-top:16px;font-size:12px;color:var(--color-charcoal-muted);">
                            <i class="fas fa-shield-alt text-success"></i> Free Cancellation within 24 hours of booking
                        </div>
                    </div>

                    <!-- Owner Profile Card -->
                    <div class="owner-contact-card">
                        <h5><i class="fas fa-user-tie"></i> Hosted by <%= property.getOwnerName() %></h5>
                        <p style="color:var(--color-charcoal-muted);font-size:13px;margin-bottom:8px;">
                            Registered property owner on FindMyStay
                        </p>
                        <div style="font-size:13px;color:var(--color-charcoal);">
                            <div><i class="fas fa-phone-alt" style="color:var(--color-olive);"></i> <%= property.getOwnerPhone() %></div>
                            <div><i class="fas fa-envelope" style="color:var(--color-olive);"></i> <%= property.getOwnerEmail() %></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- SMART BUDGET SUGGESTIONS -->
        <% if (budgetSuggestions != null && !budgetSuggestions.isEmpty()) { %>
            <div style="margin-top:60px;padding-top:40px;border-top:1.5px solid var(--border-color);">
                <div class="section-header">
                    <div>
                        <h2 class="section-title"><i class="fas fa-calculator" style="color:var(--color-terracotta);"></i> Smart Budget Suggestions</h2>
                        <p class="section-desc">Similar verified stays within ±20% of this budget range</p>
                    </div>
                </div>

                <div class="properties-grid">
                    <% for (Property bp : budgetSuggestions) { %>
                        <div class="property-card">
                            <div class="card-image-wrap">
                                <img src="<%= request.getContextPath() %>/<%= bp.getFeaturedImage() %>" alt="<%= bp.getTitle() %>">
                                <div class="card-badges">
                                    <span class="badge <%= bp.getGenderBadgeClass() %>"><%= bp.getGenderPreference() %></span>
                                </div>
                            </div>
                            <div class="card-body">
                                <h3 class="card-title">
                                    <a href="<%= request.getContextPath() %>/property?id=<%= bp.getId() %>"><%= bp.getTitle() %></a>
                                </h3>
                                <div class="card-location"><i class="fas fa-map-marker-alt"></i> <%= bp.getCity() %></div>
                                <div class="card-footer">
                                    <div class="card-price">
                                        <span class="amount">₹<%= String.format("%,.0f", bp.getMonthlyRent()) %></span>
                                        <span class="period">/ month</span>
                                    </div>
                                    <a href="<%= request.getContextPath() %>/property?id=<%= bp.getId() %>" class="btn btn-primary btn-sm">View</a>
                                </div>
                            </div>
                        </div>
                    <% } %>
                </div>
            </div>
        <% } %>

        <!-- ENQUIRY MODAL -->
        <div id="enquiryModal" style="display:none;position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,0.5);z-index:2000;align-items:center;justify-content:center;padding:20px;">
            <div style="background:var(--color-white);max-width:500px;margin:80px auto;border-radius:var(--radius-lg);padding:32px;box-shadow:var(--card-shadow-hover);position:relative;">
                <button type="button" onclick="document.getElementById('enquiryModal').style.display='none'" 
                        style="position:absolute;top:16px;right:16px;border:none;background:none;font-size:20px;cursor:pointer;">&times;</button>
                <h3 style="font-size:20px;color:var(--color-olive);margin-bottom:8px;">Send Enquiry to Owner</h3>
                <p style="font-size:13px;color:var(--color-charcoal-muted);margin-bottom:18px;">Ask about room availability, advance visit, food customizations, or rules.</p>

                <form action="<%= request.getContextPath() %>/enquiry/submit" method="post">
                    <input type="hidden" name="propertyId" value="<%= property.getId() %>">
                    <div class="form-group" style="margin-bottom:12px;">
                        <label class="form-label">Your Name</label>
                        <input type="text" name="name" class="form-control" value="<%= currentUser != null ? currentUser.getFullName() : "" %>" required>
                    </div>
                    <div class="form-group" style="margin-bottom:12px;">
                        <label class="form-label">Email Address</label>
                        <input type="email" name="email" class="form-control" value="<%= currentUser != null ? currentUser.getEmail() : "" %>" required>
                    </div>
                    <div class="form-group" style="margin-bottom:12px;">
                        <label class="form-label">Phone Number</label>
                        <input type="text" name="phone" class="form-control" value="<%= currentUser != null ? currentUser.getPhone() : "" %>" required>
                    </div>
                    <div class="form-group" style="margin-bottom:18px;">
                        <label class="form-label">Your Question / Message</label>
                        <textarea name="message" class="form-control" rows="3" placeholder="Hello, I am interested in this stay. Is it possible to schedule a visit this weekend?" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-terracotta btn-lg" style="width:100%;">Send Message</button>
                </form>
            </div>
        </div>

    <% } else { %>
        <div style="text-align:center;padding:80px 20px;">
            <h2>Property Not Found</h2>
            <p>The requested accommodation listing does not exist or has been deactivated.</p>
            <a href="<%= request.getContextPath() %>/stays" class="btn btn-primary" style="margin-top:16px;">Browse Available Stays</a>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

