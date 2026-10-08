/* ==============================================================================
   SHOPSPHERE (SHOPSPHERE) - INTERACTIVE JAVASCRIPT & MODERN UTILITIES
   ============================================================================== */

document.addEventListener("DOMContentLoaded", function () {

    // Helper to get CSRF tokens from meta tags
    function getCsrfHeaders() {
        const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';
        const headers = { "Content-Type": "application/json" };
        if (token) {
            headers[header] = token;
        }
        return headers;
    }

    // Theme Switcher (Dark / Light Mode)
    const currentTheme = localStorage.getItem("shopsphere_theme") || "light";
    document.documentElement.setAttribute("data-bs-theme", currentTheme);
    updateThemeIcon(currentTheme);

    const themeToggleBtn = document.getElementById("themeToggleBtn");
    if (themeToggleBtn) {
        themeToggleBtn.addEventListener("click", function () {
            const activeTheme = document.documentElement.getAttribute("data-bs-theme");
            const newTheme = activeTheme === "dark" ? "light" : "dark";
            document.documentElement.setAttribute("data-bs-theme", newTheme);
            localStorage.setItem("shopsphere_theme", newTheme);
            updateThemeIcon(newTheme);
            showToast(`Switched to ${newTheme === 'dark' ? 'Dark' : 'Light'} Mode`, "info");
        });
    }

    function updateThemeIcon(theme) {
        const icon = document.getElementById("themeToggleIcon");
        if (icon) {
            if (theme === "dark") {
                icon.className = "bi bi-sun-fill text-warning";
            } else {
                icon.className = "bi bi-moon-stars";
            }
        }
    }

    // Global Image Error Fallback Handler
    document.addEventListener("error", function (e) {
        if (e.target && e.target.tagName === "IMG") {
            const currentSrc = e.target.getAttribute("src") || "";
            if (!currentSrc.includes("default.svg")) {
                if (currentSrc.includes("categories")) {
                    e.target.src = "/images/categories/default.svg";
                } else {
                    e.target.src = "/images/products/default.svg";
                }
            }
        }
    }, true);

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll(".auto-dismiss");
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Setup global Toast helper
    window.showToast = function (message, type = "success") {
        let container = document.getElementById("toast-container");
        if (!container) {
            container = document.createElement("div");
            container.id = "toast-container";
            document.body.appendChild(container);
        }

        const bgClass = type === "success" ? "bg-success" : (type === "danger" ? "bg-danger" : "bg-primary");
        const toastEl = document.createElement("div");
        toastEl.className = `toast align-items-center text-white ${bgClass} border-0 show mb-2 shadow`;
        toastEl.setAttribute("role", "alert");
        toastEl.setAttribute("aria-live", "assertive");
        toastEl.setAttribute("aria-atomic", "true");

        toastEl.innerHTML = `
            <div class="d-flex">
                <div class="toast-body font-weight-medium">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;

        container.appendChild(toastEl);
        setTimeout(() => {
            toastEl.remove();
        }, 4000);
    };

    // Wishlist Toggle Handler via AJAX
    document.querySelectorAll(".ajax-wishlist-btn").forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            const productId = this.getAttribute("data-product-id");
            if (!productId) return;

            fetch(`/api/wishlist/toggle/${productId}`, {
                method: "POST",
                headers: getCsrfHeaders()
            })
            .then(res => {
                if (res.status === 401 || res.status === 403) {
                    window.location.href = "/login";
                    return;
                }
                return res.json();
            })
            .then(data => {
                if (data && data.success) {
                    const inWishlist = data.data;
                    const icon = this.querySelector("i");
                    if (icon) {
                        if (inWishlist) {
                            icon.classList.remove("bi-heart");
                            icon.classList.add("bi-heart-fill", "text-danger");
                            showToast("Added to your wishlist!", "success");
                        } else {
                            icon.classList.remove("bi-heart-fill", "text-danger");
                            icon.classList.add("bi-heart");
                            showToast("Removed from wishlist.", "info");
                        }
                    }
                    // Update header wishlist badge count
                    fetchWishlistCount();
                }
            })
            .catch(err => {
                console.error("Wishlist toggle error", err);
            });
        });
    });

    // Product Comparison Handler via AJAX
    document.querySelectorAll(".ajax-compare-btn").forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            const productId = this.getAttribute("data-product-id");
            if (!productId) return;

            fetch(`/api/compare/add/${productId}`, {
                method: "POST",
                headers: getCsrfHeaders()
            })
            .then(res => res.json())
            .then(data => {
                if (data && data.success) {
                    showToast(data.message, "success");
                } else {
                    showToast(data.message || "Could not add to compare", "danger");
                }
            })
            .catch(err => console.error("Compare error", err));
        });
    });

    // Helper to refresh header wishlist count
    function fetchWishlistCount() {
        fetch("/api/wishlist/count", { headers: getCsrfHeaders() })
            .then(res => res.json())
            .then(data => {
                if (data && data.success) {
                    const badge = document.getElementById("header-wishlist-badge");
                    if (badge) {
                        badge.textContent = data.data;
                        badge.style.display = data.data > 0 ? "inline-block" : "none";
                    }
                }
            })
            .catch(() => {});
    }

    // Helper to preview uploaded images
    const imageInputs = document.querySelectorAll(".image-preview-input");
    imageInputs.forEach(input => {
        input.addEventListener("change", function () {
            const previewId = this.getAttribute("data-preview-target");
            const previewEl = document.getElementById(previewId);
            if (previewEl && this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewEl.src = e.target.result;
                    previewEl.style.display = "block";
                };
                reader.readAsDataURL(this.files[0]);
            }
        });
    });

    // Live Search Autocomplete
    const searchInput = document.getElementById("headerSearchInput");
    const autocompleteDropdown = document.getElementById("searchAutocompleteDropdown");
    let searchDebounceTimer = null;

    if (searchInput && autocompleteDropdown) {
        searchInput.addEventListener("input", function () {
            const query = this.value.trim();
            clearTimeout(searchDebounceTimer);

            if (query.length < 2) {
                autocompleteDropdown.style.display = "none";
                autocompleteDropdown.innerHTML = "";
                return;
            }

            searchDebounceTimer = setTimeout(() => {
                fetch(`/api/products/autocomplete?q=${encodeURIComponent(query)}&limit=6`)
                    .then(res => res.json())
                    .then(data => {
                        if (data && data.success && data.data && data.data.length > 0) {
                            let html = '<div class="list-group list-group-flush">';
                            data.data.forEach(item => {
                                const imgSrc = item.imageUrl || '/images/default-product.png';
                                const priceFormatted = '₹' + Number(item.discountedPrice).toLocaleString('en-IN');
                                const origFormatted = item.discountPercentage > 0 ? `<span class="text-decoration-line-through text-muted small ms-1">₹${Number(item.price).toLocaleString('en-IN')}</span>` : '';
                                const discountBadge = item.discountPercentage > 0 ? `<span class="badge bg-success-subtle text-success ms-2">${item.discountPercentage}% OFF</span>` : '';

                                html += `
                                    <a href="/products/${item.slug}" class="list-group-item list-group-item-action d-flex align-items-center gap-3 py-2 px-3 border-0">
                                        <img src="${imgSrc}" alt="${item.name}" class="rounded" style="width: 44px; height: 44px; object-fit: cover;">
                                        <div class="flex-grow-1 text-truncate">
                                            <div class="fw-semibold text-dark text-truncate small">${item.name}</div>
                                            <div class="text-muted small">${item.brand ? item.brand + ' &bull; ' : ''}${item.categoryName || ''}</div>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-primary small">${priceFormatted}</div>
                                            ${origFormatted}
                                            ${discountBadge}
                                        </div>
                                    </a>
                                `;
                            });
                            html += `
                                <div class="p-2 text-center bg-light border-top">
                                    <a href="/products?keyword=${encodeURIComponent(query)}" class="small fw-semibold text-decoration-none text-primary">
                                        View all matching products &rarr;
                                    </a>
                                </div>
                            </div>`;
                            autocompleteDropdown.innerHTML = html;
                            autocompleteDropdown.style.display = "block";
                        } else {
                            autocompleteDropdown.innerHTML = `
                                <div class="p-3 text-center text-muted small">
                                    <i class="bi bi-search me-1"></i> No matching products found for "${query}"
                                </div>
                            `;
                            autocompleteDropdown.style.display = "block";
                        }
                    })
                    .catch(() => {
                        autocompleteDropdown.style.display = "none";
                    });
            }, 250);
        });

        // Close autocomplete dropdown on click outside
        document.addEventListener("click", function (e) {
            if (!searchInput.contains(e.target) && !autocompleteDropdown.contains(e.target)) {
                autocompleteDropdown.style.display = "none";
            }
        });
    }

    // Quick fill credentials helper on login page
    const fillAdminBtn = document.getElementById("fill-admin-btn");
    if (fillAdminBtn) {
        fillAdminBtn.addEventListener("click", () => {
            const emailField = document.getElementById("email");
            const passField = document.getElementById("password");
            if (emailField) emailField.value = "admin@shopsphere.com";
            if (passField) passField.value = "Admin@ShopSphere2026!";
        });
    }

    const fillUserBtn = document.getElementById("fill-user-btn");
    if (fillUserBtn) {
        fillUserBtn.addEventListener("click", () => {
            const emailField = document.getElementById("email");
            const passField = document.getElementById("password");
            if (emailField) emailField.value = "rahul@example.com";
            if (passField) passField.value = "User@123";
        });
    }
});
