// Confirm before delete
function confirmDelete() {
    return confirm("Are you sure you want to delete this?");
}

// Auto hide alerts after 3 seconds
setTimeout(function() {
    var alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        alert.style.display = 'none';
    });
}, 3000);

// ✅ Open payment receipt or course material PDF in a new tab
//    Usage in HTML: onclick="viewFile('/payments/receipt/PAY123')"
//                   onclick="viewFile('/materials/download/MAT001')"
function viewFile(url) {
    window.open(url, '_blank');
}

// ✅ Force download a course material file
//    Usage in HTML: onclick="downloadFile('/materials/download/MAT001', 'filename.pdf')"
function downloadFile(url, filename) {
    var a = document.createElement('a');
    a.href = url;
    a.download = filename || 'download';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}