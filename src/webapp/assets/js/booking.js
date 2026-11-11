// Live Booking Amount Calculation
document.addEventListener('DOMContentLoaded', () => {
    const durationInput = document.getElementById('bookingDuration');
    const monthlyRateElem = document.getElementById('monthlyRentValue');
    const totalDisplay = document.getElementById('bookingTotalAmount');

    function calculateTotal() {
        if (!durationInput || !monthlyRateElem || !totalDisplay) return;
        const duration = parseInt(durationInput.value) || 1;
        const rate = parseFloat(monthlyRateElem.dataset.rent) || 0;
        const total = duration * rate;
        totalDisplay.textContent = '₹' + total.toLocaleString('en-IN');
    }

    if (durationInput) {
        durationInput.addEventListener('input', calculateTotal);
        durationInput.addEventListener('change', calculateTotal);
    }
});

