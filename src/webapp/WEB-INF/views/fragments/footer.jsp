<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="footer">
    <div class="container">
        <div class="footer-grid">
            <div>
                <div class="footer-brand">
                    <i class="fas fa-home" style="color:var(--color-terracotta);"></i> Find<span>MyStay</span>
                </div>
                <p class="footer-desc">
                    FindMyStay is the smart PG, room, and hostel discovery platform designed for students, working professionals, and urban migrants across India. Verified stays, transparent pricing, and zero hassle.
                </p>
                <div style="display:flex;gap:12px;color:var(--color-sage);">
                    <a href="#" style="color:var(--color-sage);font-size:18px;"><i class="fab fa-facebook"></i></a>
                    <a href="#" style="color:var(--color-sage);font-size:18px;"><i class="fab fa-instagram"></i></a>
                    <a href="#" style="color:var(--color-sage);font-size:18px;"><i class="fab fa-linkedin"></i></a>
                    <a href="#" style="color:var(--color-sage);font-size:18px;"><i class="fab fa-twitter"></i></a>
                </div>
            </div>

            <div>
                <h4 class="footer-title">Popular Cities</h4>
                <ul class="footer-links">
                    <li><a href="<%= request.getContextPath() %>/stays?city=Chennai">Chennai Stays</a></li>
                    <li><a href="<%= request.getContextPath() %>/stays?city=Bangalore">Bangalore PGs</a></li>
                    <li><a href="<%= request.getContextPath() %>/stays?city=Coimbatore">Coimbatore Hostels</a></li>
                    <li><a href="<%= request.getContextPath() %>/stays?city=Hyderabad">Hyderabad Rooms</a></li>
                    <li><a href="<%= request.getContextPath() %>/stays?city=Salem">Salem Accommodations</a></li>
                </ul>
            </div>

            <div>
                <h4 class="footer-title">Quick Links</h4>
                <ul class="footer-links">
                    <li><a href="<%= request.getContextPath() %>/stays">Browse All Stays</a></li>
                    <li><a href="<%= request.getContextPath() %>/compare">Compare Stays</a></li>
                    <li><a href="<%= request.getContextPath() %>/login">Owner Login</a></li>
                    <li><a href="<%= request.getContextPath() %>/register">List Your Property</a></li>
                    <li><a href="<%= request.getContextPath() %>/customer/dashboard">My Dashboard</a></li>
                </ul>
            </div>

            <div>
                <h4 class="footer-title">Safe & Verified</h4>
                <p style="color:#A0A5A2;font-size:13px;line-height:1.6;margin-bottom:14px;">
                    Every accommodation with the <strong style="color:#FFF;">Verified Badge</strong> undergoes on-site inspection for security, safety, and hygiene standards.
                </p>
                <div style="background:#3E4440;padding:12px;border-radius:8px;font-size:12px;color:#A0A5A2;">
                    <i class="fas fa-headset text-success"></i> 24/7 Support: support@findmystay.com
                </div>
            </div>
        </div>

        <div class="footer-bottom">
            <div>&copy; 2026 FindMyStay Technologies Inc. All rights reserved.</div>
            <div>Crafted with Core Java, Servlets, JSP, JDBC, &amp; MySQL</div>
        </div>
    </div>
</footer>

<script src="<%= request.getContextPath() %>/assets/js/main.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/booking.js"></script>
</body>
</html>

