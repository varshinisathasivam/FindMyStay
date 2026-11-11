<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.Property" %>
<%@ page import="com.findmystay.model.Amenity" %>
<%
    Property property = (Property) request.getAttribute("property");
    List<Amenity> allAmenities = (List<Amenity>) request.getAttribute("allAmenities");
    if (property != null) {
        request.setAttribute("pageTitle", "Edit " + property.getTitle() + " – FindMyStay");
    }
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<div class="container" style="padding-top:40px;padding-bottom:80px;">
    <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

    <% if (property != null) { %>
        <div style="max-width:860px;margin:0 auto;">
            <div style="margin-bottom:28px;">
                <a href="<%= request.getContextPath() %>/owner/dashboard" style="color:var(--color-charcoal-muted);font-size:14px;">
                    &larr; Back to Owner Dashboard
                </a>
                <h1 style="font-size:30px;color:var(--color-olive);margin-top:6px;">Edit Accommodation Listing</h1>
                <p style="color:var(--color-charcoal-muted);font-size:14px;">Update details for <%= property.getTitle() %></p>
            </div>

            <div style="background:var(--color-white);padding:36px;border-radius:var(--radius-lg);border:1px solid var(--border-color);box-shadow:var(--card-shadow);">
                <form action="<%= request.getContextPath() %>/owner/property/edit" method="post">
                    <input type="hidden" name="id" value="<%= property.getId() %>">

                    <!-- Basic Info -->
                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Property Title / Name</label>
                        <input type="text" name="title" class="form-control" value="<%= property.getTitle() %>" required>
                    </div>

                    <div class="form-grid-2">
                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Accommodation Type</label>
                            <select name="propertyType" class="form-select" required>
                                <option value="PG" <%= property.getPropertyType() == Property.PropertyType.PG ? "selected" : "" %>>PG (Paying Guest)</option>
                                <option value="HOSTEL" <%= property.getPropertyType() == Property.PropertyType.HOSTEL ? "selected" : "" %>>Hostel</option>
                                <option value="ROOM" <%= property.getPropertyType() == Property.PropertyType.ROOM ? "selected" : "" %>>Rental Room</option>
                                <option value="FLAT" <%= property.getPropertyType() == Property.PropertyType.FLAT ? "selected" : "" %>>Shared Flat</option>
                            </select>
                        </div>

                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Gender Preference</label>
                            <select name="genderPreference" class="form-select" required>
                                <option value="BOYS" <%= property.getGenderPreference() == Property.GenderPreference.BOYS ? "selected" : "" %>>Boys Only</option>
                                <option value="GIRLS" <%= property.getGenderPreference() == Property.GenderPreference.GIRLS ? "selected" : "" %>>Girls Only</option>
                                <option value="CO_LIVING" <%= property.getGenderPreference() == Property.GenderPreference.CO_LIVING ? "selected" : "" %>>Co-Living / Unisex</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Description</label>
                        <textarea name="description" class="form-control" rows="3"><%= property.getDescription() %></textarea>
                    </div>

                    <!-- Location & Pricing -->
                    <div class="form-grid-2">
                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">City</label>
                            <select name="city" class="form-select" required>
                                <option value="Chennai" <%= "Chennai".equalsIgnoreCase(property.getCity()) ? "selected" : "" %>>Chennai</option>
                                <option value="Coimbatore" <%= "Coimbatore".equalsIgnoreCase(property.getCity()) ? "selected" : "" %>>Coimbatore</option>
                                <option value="Salem" <%= "Salem".equalsIgnoreCase(property.getCity()) ? "selected" : "" %>>Salem</option>
                                <option value="Bangalore" <%= "Bangalore".equalsIgnoreCase(property.getCity()) ? "selected" : "" %>>Bangalore</option>
                                <option value="Hyderabad" <%= "Hyderabad".equalsIgnoreCase(property.getCity()) ? "selected" : "" %>>Hyderabad</option>
                            </select>
                        </div>

                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Address / Landmark</label>
                            <input type="text" name="address" class="form-control" value="<%= property.getAddress() %>" required>
                        </div>
                    </div>

                    <div class="form-grid-2">
                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Monthly Rent (₹)</label>
                            <input type="number" name="monthlyRent" class="form-control" value="<%= property.getMonthlyRent() %>" required>
                        </div>

                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Security Deposit (₹)</label>
                            <input type="number" name="securityDeposit" class="form-control" value="<%= property.getSecurityDeposit() %>">
                        </div>
                    </div>

                    <!-- Capacity -->
                    <div class="form-grid-2">
                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Room Sharing Types</label>
                            <input type="text" name="roomSharing" class="form-control" value="<%= property.getRoomSharing() %>" required>
                        </div>

                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Food Details</label>
                            <input type="text" name="foodAvailability" class="form-control" value="<%= property.getFoodAvailability() %>" required>
                        </div>
                    </div>

                    <div class="form-grid-2">
                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Total Rooms</label>
                            <input type="number" name="totalRooms" class="form-control" value="<%= property.getTotalRooms() %>">
                        </div>

                        <div class="form-group" style="margin-bottom:14px;">
                            <label class="form-label">Available Rooms</label>
                            <input type="number" name="availableRooms" class="form-control" value="<%= property.getAvailableRooms() %>">
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">House Rules</label>
                        <input type="text" name="houseRules" class="form-control" value="<%= property.getHouseRules() %>">
                    </div>

                    <div class="form-group" style="margin-bottom:14px;">
                        <label class="form-label">Safety Measures</label>
                        <input type="text" name="safetyInfo" class="form-control" value="<%= property.getSafetyInfo() %>">
                    </div>

                    <div class="form-group" style="margin-bottom:20px;">
                        <label class="form-label">Nearby Landmarks</label>
                        <input type="text" name="nearbyPlaces" class="form-control" value="<%= property.getNearbyPlaces() %>">
                    </div>

                    <button type="submit" class="btn btn-terracotta btn-lg" style="width:100%;">
                        <i class="fas fa-save"></i> Save Changes
                    </button>
                </form>
            </div>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

