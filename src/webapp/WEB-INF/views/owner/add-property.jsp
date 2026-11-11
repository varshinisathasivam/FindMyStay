<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.Amenity" %>
<%
    request.setAttribute("pageTitle", "List a New Stay – Owner Portal – FindMyStay");
    List<Amenity> allAmenities = (List<Amenity>) request.getAttribute("allAmenities");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container" style="padding-top:40px;padding-bottom:80px;">
    <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

    <div style="max-width:860px;margin:0 auto;">
        <div style="margin-bottom:28px;">
            <a href="<%= request.getContextPath() %>/owner/dashboard" style="color:var(--color-charcoal-muted);font-size:14px;">
                &larr; Back to Owner Dashboard
            </a>
            <h1 style="font-size:30px;color:var(--color-olive);margin-top:6px;">Add New Accommodation Listing</h1>
            <p style="color:var(--color-charcoal-muted);font-size:14px;">
                Enter verified details to reach thousands of students and working professionals.
            </p>
        </div>

        <div style="background:var(--color-white);padding:36px;border-radius:var(--radius-lg);border:1px solid var(--border-color);box-shadow:var(--card-shadow);">
            <form action="<%= request.getContextPath() %>/owner/property/add" method="post">
                <!-- Basic Information -->
                <h3 style="font-size:18px;color:var(--color-olive);margin-bottom:16px;border-bottom:1px solid var(--border-color);padding-bottom:8px;">
                    1. Basic Property Information
                </h3>

                <div class="form-group" style="margin-bottom:14px;">
                    <label class="form-label">Property Title / Name *</label>
                    <input type="text" name="title" class="form-control" placeholder="e.g. Royal Palm Luxury Boys PG" required>
                </div>

                <div class="form-grid-2">
                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Accommodation Type *</label>
                        <select name="propertyType" class="form-select" required>
                            <option value="PG">Paying Guest (PG)</option>
                            <option value="HOSTEL">Hostel</option>
                            <option value="ROOM">Rental Room</option>
                            <option value="FLAT">Shared Flat</option>
                        </select>
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Gender Preference *</label>
                        <select name="genderPreference" class="form-select" required>
                            <option value="BOYS">Boys Only</option>
                            <option value="GIRLS">Girls Only</option>
                            <option value="CO_LIVING">Co-Living / Unisex</option>
                        </select>
                    </div>
                </div>

                <div class="form-group" style="margin-bottom:14px;">
                    <label class="form-label">Detailed Description</label>
                    <textarea name="description" class="form-control" rows="3" placeholder="Describe the vibe, room furnishings, study environment, and facilities..."></textarea>
                </div>

                <!-- Location & Rates -->
                <h3 style="font-size:18px;color:var(--color-olive);margin:24px 0 16px;border-bottom:1px solid var(--border-color);padding-bottom:8px;">
                    2. Location &amp; Pricing
                </h3>

                <div class="form-grid-2">
                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">City *</label>
                        <select name="city" class="form-select" required>
                            <option value="Chennai">Chennai</option>
                            <option value="Coimbatore">Coimbatore</option>
                            <option value="Salem">Salem</option>
                            <option value="Bangalore">Bangalore</option>
                            <option value="Hyderabad">Hyderabad</option>
                        </select>
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Address / Landmark *</label>
                        <input type="text" name="address" class="form-control" placeholder="Door No, Street Name, Area" required>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Monthly Rent (₹ Starting From) *</label>
                        <input type="number" name="monthlyRent" class="form-control" placeholder="e.g. 7500" required step="100">
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Security Deposit (₹ Refundable)</label>
                        <input type="number" name="securityDeposit" class="form-control" placeholder="e.g. 10000" step="100">
                    </div>
                </div>

                <!-- Capacity & Sharing -->
                <h3 style="font-size:18px;color:var(--color-olive);margin:24px 0 16px;border-bottom:1px solid var(--border-color);padding-bottom:8px;">
                    3. Capacity &amp; Room Details
                </h3>

                <div class="form-grid-2">
                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Room Sharing Types Offered *</label>
                        <input type="text" name="roomSharing" class="form-control" placeholder="e.g. Single, Double, Triple" value="Single, Double" required>
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Food Availability &amp; Dining *</label>
                        <input type="text" name="foodAvailability" class="form-control" placeholder="e.g. 3 Meals Included (Veg & Non-Veg)" value="3 Meals Included (Veg & Non-Veg)" required>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Total Rooms in Property</label>
                        <input type="number" name="totalRooms" class="form-control" value="10" min="1">
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Currently Available Rooms</label>
                        <input type="number" name="availableRooms" class="form-control" value="3" min="0">
                    </div>
                </div>

                <!-- Amenities Checkboxes -->
                <h3 style="font-size:18px;color:var(--color-olive);margin:24px 0 16px;border-bottom:1px solid var(--border-color);padding-bottom:8px;">
                    4. Amenities &amp; Facilities
                </h3>

                <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(200px, 1fr));gap:12px;margin-bottom:20px;">
                    <% if (allAmenities != null) {
                        for (Amenity a : allAmenities) {
                    %>
                        <label style="display:flex;align-items:center;gap:8px;font-size:14px;cursor:pointer;background:var(--bg-cream);padding:8px 12px;border-radius:var(--radius-sm);border:1px solid var(--border-color);">
                            <input type="checkbox" name="amenityIds" value="<%= a.getId() %>" checked>
                            <i class="<%= a.getIconClass() %>" style="color:var(--color-olive);"></i>
                            <span><%= a.getName() %></span>
                        </label>
                    <% } } %>
                </div>

                <!-- Rules, Safety & Nearby Places -->
                <h3 style="font-size:18px;color:var(--color-olive);margin:24px 0 16px;border-bottom:1px solid var(--border-color);padding-bottom:8px;">
                    5. Safety, Rules &amp; Local Landmarks
                </h3>

                <div class="form-group" style="margin-bottom:14px;">
                    <label class="form-label">Safety Measures</label>
                    <input type="text" name="safetyInfo" class="form-control" value="Biometric entry, 24/7 CCTV surveillance, fire extinguishers on all floors.">
                </div>

                <div class="form-group" style="margin-bottom:14px;">
                    <label class="form-label">House Rules &amp; Curfew Timings</label>
                    <input type="text" name="houseRules" class="form-control" value="Gate closes at 10:30 PM. No smoking inside rooms. Visitors allowed in lounge.">
                </div>

                <div class="form-group" style="margin-bottom:20px;">
                    <label class="form-label">Nearby Colleges, IT Parks, Metro &amp; Hospitals</label>
                    <input type="text" name="nearbyPlaces" class="form-control" placeholder="e.g. Walking distance from IT Park, Metro 500m, Hospital 1km">
                </div>

                <div class="form-group" style="margin-bottom:24px;">
                    <label class="form-label">Property Featured Image Path</label>
                    <select name="featuredImage" class="form-select">
                        <option value="assets/images/stays/chennai-pg1.svg">Modern Green Oasis (Default)</option>
                        <option value="assets/images/stays/chennai-pg2.svg">Serene Haven Residency</option>
                        <option value="assets/images/stays/chennai-pg3.svg">Executive Urban Studio</option>
                        <option value="assets/images/stays/blr-pg1.svg">Silicon Towers Complex</option>
                        <option value="assets/images/stays/hyd-pg1.svg">Cyber Heights Building</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-terracotta btn-lg" style="width:100%;">
                    <i class="fas fa-check-circle"></i> Save &amp; Submit Property for Verification
                </button>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

