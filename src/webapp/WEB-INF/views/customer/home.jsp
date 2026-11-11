<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.findmystay.model.Property" %>
<%
    request.setAttribute("pageTitle", "Find a Place That Feels Like Home – FindMyStay");
%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />
<jsp:include page="/WEB-INF/views/fragments/navbar.jsp" />

<main>
    <!-- HERO SECTION -->
    <section class="hero-section">
        <div class="container">
            <jsp:include page="/WEB-INF/views/fragments/alerts.jsp" />

            <div class="hero-content">
                <span class="hero-tag">
                    <i class="fas fa-check-circle text-success"></i> 100% Verified PGs &amp; Hostels
                </span>
                <h1 class="hero-title">Find a Place That Feels Like Home</h1>
                <p class="hero-subtitle">
                    Discover comfortable PGs, hostels and rooms that match your budget and lifestyle.
                </p>
            </div>

            <!-- SEARCH COMPONENT -->
            <div class="search-card">
                <form action="<%= request.getContextPath() %>/stays" method="get" class="search-form">
                    <div class="form-group">
                        <label class="form-label"><i class="fas fa-map-marker-alt" style="color:var(--color-terracotta);"></i> City / Location</label>
                        <select name="city" class="form-select">
                            <option value="">All Cities</option>
                            <option value="Chennai">Chennai</option>
                            <option value="Coimbatore">Coimbatore</option>
                            <option value="Salem">Salem</option>
                            <option value="Bangalore">Bangalore</option>
                            <option value="Hyderabad">Hyderabad</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label"><i class="fas fa-building" style="color:var(--color-olive);"></i> Accommodation Type</label>
                        <select name="type" class="form-select">
                            <option value="">All Types</option>
                            <option value="PG">Paying Guest (PG)</option>
                            <option value="HOSTEL">Hostel</option>
                            <option value="ROOM">Rental Room</option>
                            <option value="FLAT">Shared Flat</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label"><i class="fas fa-rupee-sign" style="color:var(--color-terracotta);"></i> Min Budget</label>
                        <input type="number" name="minRent" class="form-control" placeholder="₹4,000" step="500">
                    </div>

                    <div class="form-group">
                        <label class="form-label"><i class="fas fa-rupee-sign" style="color:var(--color-terracotta);"></i> Max Budget</label>
                        <input type="number" name="maxRent" class="form-control" placeholder="₹20,000" step="500">
                    </div>

                    <button type="submit" class="btn btn-terracotta btn-lg" style="height:48px;">
                        <i class="fas fa-search"></i> Search Stays
                    </button>
                </form>
            </div>
        </div>
    </section>

    <!-- POPULAR CITIES -->
    <section class="section" style="background:#FAF8F5;">
        <div class="container">
            <div class="section-header">
                <div>
                    <h2 class="section-title">Explore by City</h2>
                    <p class="section-desc">Handpicked accommodations in top education &amp; technology hubs</p>
                </div>
            </div>

            <div class="cities-grid">
                <a href="<%= request.getContextPath() %>/stays?city=Chennai" class="city-card">
                    <div class="city-icon"><i class="fas fa-landmark"></i></div>
                    <div class="city-name">Chennai</div>
                    <div class="city-count">OMR, Velachery, Anna Nagar</div>
                </a>
                <a href="<%= request.getContextPath() %>/stays?city=Coimbatore" class="city-card">
                    <div class="city-icon"><i class="fas fa-mountain"></i></div>
                    <div class="city-name">Coimbatore</div>
                    <div class="city-count">Peelamedu, RS Puram, Gandhipuram</div>
                </a>
                <a href="<%= request.getContextPath() %>/stays?city=Salem" class="city-card">
                    <div class="city-icon"><i class="fas fa-tree"></i></div>
                    <div class="city-name">Salem</div>
                    <div class="city-count">Fairlands, Alagapuram, Hasthampatti</div>
                </a>
                <a href="<%= request.getContextPath() %>/stays?city=Bangalore" class="city-card">
                    <div class="city-icon"><i class="fas fa-laptop-code"></i></div>
                    <div class="city-name">Bangalore</div>
                    <div class="city-count">Koramangala, HSR, Indiranagar</div>
                </a>
                <a href="<%= request.getContextPath() %>/stays?city=Hyderabad" class="city-card">
                    <div class="city-icon"><i class="fas fa-monument"></i></div>
                    <div class="city-name">Hyderabad</div>
                    <div class="city-count">Hitec City, Gachibowli, Kondapur</div>
                </a>
            </div>
        </div>
    </section>

    <!-- FEATURED PROPERTIES -->
    <section class="section">
        <div class="container">
            <div class="section-header">
                <div>
                    <h2 class="section-title">Featured Verified Stays</h2>
                    <p class="section-desc">Top rated stays verified for safety, amenities, and comfort</p>
                </div>
                <a href="<%= request.getContextPath() %>/stays" class="btn btn-outline btn-sm">
                    View All Stays <i class="fas fa-arrow-right"></i>
                </a>
            </div>

            <div class="properties-grid">
                <%
                    List<Property> featured = (List<Property>) request.getAttribute("featuredStays");
                    if (featured != null && !featured.isEmpty()) {
                        for (Property p : featured) {
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
                                <input type="hidden" name="redirect" value="<%= request.getRequestURI() %>">
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

                            <div class="card-footer">
                                <div class="card-price">
                                    <span class="amount">₹<%= String.format("%,.0f", p.getMonthlyRent()) %></span>
                                    <span class="period">/ month onwards</span>
                                </div>

                                <div style="display:flex;align-items:center;gap:8px;">
                                    <a href="<%= request.getContextPath() %>/compare/add?propertyId=<%= p.getId() %>&redirect=<%= request.getRequestURI() %>" 
                                       class="btn btn-secondary btn-sm" title="Add to Compare">
                                        <i class="fas fa-columns"></i>
                                    </a>
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
                    <div style="grid-column:1/-1;text-align:center;padding:40px;">
                        <p>No featured properties found. Please initialize the database with sample data.</p>
                    </div>
                <% } %>
            </div>
        </div>
    </section>

    <!-- WHY CHOOSE FINDMYSTAY -->
    <section class="section" style="background:#FAF8F5;">
        <div class="container">
            <div class="section-header" style="text-align:center;display:block;margin-bottom:50px;">
                <h2 class="section-title">Why Choose FindMyStay?</h2>
                <p class="section-desc">We bridge the gap between discerning tenants and trusted property owners</p>
            </div>

            <div class="features-grid">
                <div class="highlight-box">
                    <div class="highlight-icon"><i class="fas fa-shield-alt"></i></div>
                    <h3 class="highlight-title">100% Verified Stays</h3>
                    <p class="highlight-text">
                        Every verified listing is physically inspected for hygiene, safety measures, accurate photographs, and reliable amenities.
                    </p>
                </div>

                <div class="highlight-box">
                    <div class="highlight-icon"><i class="fas fa-hand-holding-usd"></i></div>
                    <h3 class="highlight-title">Zero Brokerage</h3>
                    <p class="highlight-text">
                        Connect directly with genuine property owners. No middleman commissions or hidden brokerage fees.
                    </p>
                </div>

                <div class="highlight-box">
                    <div class="highlight-icon"><i class="fas fa-sliders-h"></i></div>
                    <h3 class="highlight-title">Smart Comparison</h3>
                    <p class="highlight-text">
                        Compare up to 3 stays side by side on rent, security deposit, food, air conditioning, and live room availability.
                    </p>
                </div>

                <div class="highlight-box">
                    <div class="highlight-icon"><i class="fas fa-door-open"></i></div>
                    <h3 class="highlight-title">Live Availability</h3>
                    <p class="highlight-text">
                        Real-time indicators showing available beds and rooms, so you never waste time visiting fully booked properties.
                    </p>
                </div>
            </div>
        </div>
    </section>

    <!-- HOW IT WORKS -->
    <section class="section">
        <div class="container">
            <div class="section-header" style="text-align:center;display:block;margin-bottom:50px;">
                <h2 class="section-title">How It Works</h2>
                <p class="section-desc">Find and secure your next home in three simple steps</p>
            </div>

            <div class="features-grid">
                <div class="highlight-box" style="text-align:center;">
                    <div class="highlight-icon" style="margin:0 auto 20px;"><i class="fas fa-search-location"></i></div>
                    <h3 class="highlight-title">1. Search &amp; Filter</h3>
                    <p class="highlight-text">Filter by city, budget, sharing type, gender preference, and facilities like food and Wi-Fi.</p>
                </div>

                <div class="highlight-box" style="text-align:center;">
                    <div class="highlight-icon" style="margin:0 auto 20px;"><i class="fas fa-balance-scale"></i></div>
                    <h3 class="highlight-title">2. Compare &amp; Enquire</h3>
                    <p class="highlight-text">Compare top options side-by-side or send direct questions to the owner with one click.</p>
                </div>

                <div class="highlight-box" style="text-align:center;">
                    <div class="highlight-icon" style="margin:0 auto 20px;"><i class="fas fa-key"></i></div>
                    <h3 class="highlight-title">3. Book Your Stay</h3>
                    <p class="highlight-text">Submit your move-in request online and receive instant owner confirmation with a unique booking reference.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- TESTIMONIALS -->
    <section class="section" style="background:#FAF8F5;">
        <div class="container">
            <div class="section-header" style="text-align:center;display:block;margin-bottom:50px;">
                <h2 class="section-title">What Our Residents Say</h2>
                <p class="section-desc">Real stories from students and young professionals across South India</p>
            </div>

            <div class="testimonials-grid">
                <div class="testimonial-card">
                    <div class="testimonial-stars">
                        <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i>
                    </div>
                    <p class="testimonial-quote">
                        "Moving to Chennai for my first IT job at OMR was stressful until I found Greenleaf Luxury PG through FindMyStay. The verified photos matched the actual room 100%!"
                    </p>
                    <div class="testimonial-author">
                        <div class="author-avatar">KR</div>
                        <div class="author-info">
                            <h5>Karthik Raja</h5>
                            <p>Software Engineer, Chennai</p>
                        </div>
                    </div>
                </div>

                <div class="testimonial-card">
                    <div class="testimonial-stars">
                        <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i>
                    </div>
                    <p class="testimonial-quote">
                        "As a female student preparing for CA in Coimbatore, security and nutritious food were my top priorities. Serene Haven Girls Hostel exceeded all expectations."
                    </p>
                    <div class="testimonial-author">
                        <div class="author-avatar">PS</div>
                        <div class="author-info">
                            <h5>Priya Soundar</h5>
                            <p>CA Aspirant, Coimbatore</p>
                        </div>
                    </div>
                </div>

                <div class="testimonial-card">
                    <div class="testimonial-stars">
                        <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i>
                    </div>
                    <p class="testimonial-quote">
                        "I manage two hostels in Salem. FindMyStay gave me direct genuine tenants without brokerage calls. Managing bookings and availability has never been simpler."
                    </p>
                    <div class="testimonial-author">
                        <div class="author-avatar">SM</div>
                        <div class="author-info">
                            <h5>Senthil Murugan</h5>
                            <p>Hostel Owner, Salem</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

