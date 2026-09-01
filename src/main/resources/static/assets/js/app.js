/* ==========================================================================
   ShopSphere shared frontend utilities.
   Pure presentation layer — talks only to the existing REST API, unchanged.
   ========================================================================== */
(function (global) {
  "use strict";

  /* ------------------------------------------------------------ icons */
  const ICON = {
    search:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>',
    bag: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>',
    heart:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z"/></svg>',
    user: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-7 8-7s8 3 8 7"/></svg>',
    menu: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>',
    close:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>',
    star: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 21 12 17.77 5.82 21 7 14.14l-5-4.87 6.91-1.01L12 2Z"/></svg>',
    trash:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>',
    check:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>',
    alert:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="13"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>',
    info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="11"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
    logout:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>',
    shield:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2 4 5v6c0 5 3.4 9 8 11 4.6-2 8-6 8-11V5l-8-3Z"/></svg>',
    box: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 8 12 3 3 8l9 5 9-5Z"/><path d="M3 8v8l9 5 9-5V8"/><path d="M12 13v8"/></svg>',
    mapPin:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 6-9 12-9 12s-9-6-9-12a9 9 0 0 1 18 0Z"/><circle cx="12" cy="10" r="3"/></svg>',
    settings:
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.6 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.6a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1Z"/></svg>',
  };

  /* ------------------------------------------------------------ core helpers */
  function escapeHtml(str) {
    if (str === null || str === undefined) return "";
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function money(v) {
    const n = Number(v || 0);
    return "₹" + n.toLocaleString("en-IN", { maximumFractionDigits: 2 });
  }

  function stars(rating) {
    const r = Math.round(Number(rating || 0));
    return (
      ICON.star.repeat(0) +
      Array.from(
        { length: 5 },
        (_, i) =>
          `<span style="opacity:${i < r ? 1 : 0.25}">${ICON.star}</span>`,
      ).join("")
    );
  }

  async function api(path, options) {
    options = options || {};
    const opts = Object.assign({ credentials: "same-origin" }, options);
    if (opts.body && typeof opts.body !== "string") {
      opts.headers = Object.assign(
        { "Content-Type": "application/json" },
        opts.headers || {},
      );
      opts.body = JSON.stringify(opts.body);
    }
    let res;
    try {
      res = await fetch(path, opts);
    } catch (e) {
      throw new Error("Unable to connect to server.");
    }
    let data = null;
    try {
      data = await res.json();
    } catch (e) {
      /* empty body */
    }
    if (!res.ok) {
      const msg =
        (data && (data.message || data.error)) || "Something went wrong.";
      throw new Error(msg);
    }
    return data;
  }

  /* ------------------------------------------------------------ toasts */
  function ensureToastStack() {
    let stack = document.querySelector(".toast-stack");
    if (!stack) {
      stack = document.createElement("div");
      stack.className = "toast-stack";
      document.body.appendChild(stack);
    }
    return stack;
  }

  function toast(message, type) {
    type = type || "info";
    const stack = ensureToastStack();
    const el = document.createElement("div");
    el.className = "toast " + type;
    const icon =
      type === "success"
        ? ICON.check
        : type === "error"
          ? ICON.alert
          : ICON.info;
    el.innerHTML = icon + `<span>${escapeHtml(message)}</span>`;
    stack.appendChild(el);
    setTimeout(() => {
      el.style.animation = "toast-out 180ms ease forwards";
      setTimeout(() => el.remove(), 180);
    }, 3200);
  }

  /* ------------------------------------------------------------ auth state cache */
  let _meCache = null;
  async function getMe(force) {
    if (_meCache && !force) return _meCache;
    try {
      const d = await api("/api/auth/me");
      _meCache = d.user || null;
    } catch (e) {
      _meCache = null;
    }
    return _meCache;
  }

  async function requireAuth(redirectTo) {
    const user = await getMe();
    if (!user) {
      window.location.href =
        "/login.html" +
        (redirectTo ? "?next=" + encodeURIComponent(redirectTo) : "");
      return null;
    }
    return user;
  }

  async function logout() {
    try {
      await api("/api/auth/logout", { method: "POST" });
    } catch (e) {
      /* ignore */
    }
    _meCache = null;
    window.location.href = "/login.html";
  }

  /* ------------------------------------------------------------ header / nav */
  const NAV_LINKS = [
    { href: "/home.html", label: "Home", key: "home" },
    { href: "/home.html#shop", label: "Shop", key: "shop" },
    { href: "/orders.html", label: "Orders", key: "orders" },
  ];

  function navLinkHtml(link, active) {
    return `<a href="${link.href}" class="${active === link.key ? "active" : ""}">${link.label}</a>`;
  }

  async function renderHeader(activeKey) {
    const mount = document.getElementById("site-header");
    if (!mount) return;

    mount.innerHTML = `
      <header class="site-header">
        <div class="container nav-inner">
          <a href="/home.html" class="brand"><span>🛍️</span> Shop<span class="dot">Sphere</span></a>
          <nav class="nav-links" aria-label="Primary">
            ${NAV_LINKS.map((l) => navLinkHtml(l, activeKey)).join("")}
          </nav>
          <form class="nav-search" id="navSearchForm" role="search">
            <span aria-hidden="true">${ICON.search}</span>
            <input type="search" id="navSearchInput" placeholder="Search products…" aria-label="Search products">
          </form>
          <div class="nav-actions">
            <a href="/wishlist.html" class="icon-btn" aria-label="Wishlist" title="Wishlist">${ICON.heart}</a>
            <a href="/cart.html" class="icon-btn" aria-label="Cart" title="Cart">
              ${ICON.bag}<span class="badge-count" id="cartBadge" hidden>0</span>
            </a>
            <span id="navAuthArea"></span>
            <button class="hamburger" id="hamburgerBtn" aria-label="Open menu" aria-expanded="false">${ICON.menu}</button>
          </div>
        </div>
      </header>
      <div class="mobile-drawer" id="mobileDrawer">
        ${NAV_LINKS.map((l) => `<a href="${l.href}">${l.label}</a>`).join("")}
        <a href="/wishlist.html">Wishlist</a>
        <a href="/cart.html">Cart</a>
        <a href="/profile.html">Profile</a>
        <span id="mobileAuthArea"></span>
      </div>`;

    // search
    const form = document.getElementById("navSearchForm");
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      const q = document.getElementById("navSearchInput").value.trim();
      window.location.href =
        "/home.html" + (q ? "?q=" + encodeURIComponent(q) : "") + "#shop";
    });

    // mobile drawer
    const drawer = document.getElementById("mobileDrawer");
    const hamburger = document.getElementById("hamburgerBtn");
    hamburger.addEventListener("click", () => {
      const open = drawer.classList.toggle("open");
      hamburger.setAttribute("aria-expanded", String(open));
      hamburger.innerHTML = open ? ICON.close : ICON.menu;
    });

    // auth-dependent UI
    const user = await getMe();
    const authArea = document.getElementById("navAuthArea");
    const mobileAuthArea = document.getElementById("mobileAuthArea");
    if (user) {
      const initial = (user.name || "?").trim().charAt(0).toUpperCase();
      authArea.innerHTML = `
        <a href="/profile.html" class="user-chip" title="${escapeHtml(user.name)}">
          <span class="avatar">${initial}</span><span>${escapeHtml(user.name.split(" ")[0])}</span>
        </a>
        ${user.role === "ADMIN" ? `<a href="/admin.html" class="icon-btn" title="Admin dashboard" aria-label="Admin">${ICON.shield}</a>` : ""}
        <button class="icon-btn" id="logoutBtn" title="Log out" aria-label="Log out">${ICON.logout}</button>`;
      mobileAuthArea.innerHTML = `
        <a href="/profile.html">My profile</a>
        ${user.role === "ADMIN" ? `<a href="/admin.html">Admin dashboard</a>` : ""}
        <a href="#" id="mobileLogoutBtn">Log out</a>`;
      document.getElementById("logoutBtn").addEventListener("click", logout);
      document
        .getElementById("mobileLogoutBtn")
        .addEventListener("click", (e) => {
          e.preventDefault();
          logout();
        });

      // cart badge
      try {
        const items = await api("/api/cart");
        const count = (items || []).reduce((s, i) => s + (i.quantity || 0), 0);
        const badge = document.getElementById("cartBadge");
        if (count > 0) {
          badge.hidden = false;
          badge.textContent = count > 99 ? "99+" : count;
        }
      } catch (e) {
        /* not logged in / ignore */
      }
    } else {
      authArea.innerHTML = `
        <a href="/login.html" class="btn btn-ghost btn-sm">Log in</a>
        <a href="/signup.html" class="btn btn-primary btn-sm">Sign up</a>`;
      mobileAuthArea.innerHTML = `
        <a href="/login.html">Log in</a>
        <a href="/signup.html">Sign up</a>`;
    }
  }

  /* ------------------------------------------------------------ footer */
  function renderFooter() {
    const mount = document.getElementById("site-footer");
    if (!mount) return;
    const year = new Date().getFullYear();
    mount.innerHTML = `
      <footer class="site-footer">
        <div class="container">
          <div class="footer-grid">
            <div>
              <a href="/home.html" class="brand"><span>🛍️</span> Shop<span class="dot">Sphere</span></a>
              <p class="text-muted u-mt-4" style="max-width:32ch;font-size:var(--fs-sm)">A modern shopping destination for quality products, curated categories, and a smooth checkout experience.</p>
            </div>
            <div>
              <h4>Shop</h4>
              <a href="/home.html">All products</a>
              <a href="/home.html#shop">Categories</a>
              <a href="/wishlist.html">Wishlist</a>
            </div>
            <div>
              <h4>Account</h4>
              <a href="/profile.html">My profile</a>
              <a href="/orders.html">Order history</a>
              <a href="/cart.html">Cart</a>
            </div>
            <div>
              <h4>Support</h4>
              <a href="#">Help center</a>
              <a href="#">Shipping info</a>
              <a href="#">Returns</a>
            </div>
          </div>
          <div class="footer-bottom">
            <span>© ${year} ShopSphere owned by SIMSON. -All rights reserved.</span>
            <span>Built with care.</span>
          </div>
        </div>
      </footer>`;
  }

  /* ------------------------------------------------------------ confirm modal */
  function confirmDialog({ title, body, confirmLabel, danger }) {
    return new Promise((resolve) => {
      const overlay = document.createElement("div");
      overlay.className = "modal-overlay";
      overlay.innerHTML = `
        <div class="modal-box" role="dialog" aria-modal="true">
          <h3>${escapeHtml(title || "Are you sure?")}</h3>
          <p class="text-muted u-mt-2" style="font-size:var(--fs-sm)">${escapeHtml(body || "")}</p>
          <div class="modal-actions">
            <button class="btn btn-secondary" data-act="cancel">Cancel</button>
            <button class="btn ${danger ? "btn-danger" : "btn-primary"}" data-act="confirm">${escapeHtml(confirmLabel || "Confirm")}</button>
          </div>
        </div>`;
      document.body.appendChild(overlay);
      requestAnimationFrame(() => overlay.classList.add("open"));
      function close(result) {
        overlay.classList.remove("open");
        setTimeout(() => overlay.remove(), 200);
        resolve(result);
      }
      overlay.addEventListener("click", (e) => {
        if (e.target === overlay) close(false);
        const act = e.target.closest("[data-act]");
        if (act) close(act.dataset.act === "confirm");
      });
    });
  }

  /* ------------------------------------------------------------ skeleton grid */
  function skeletonCards(n) {
    return Array.from(
      { length: n || 8 },
      () => `<div class="skeleton skel-card"></div>`,
    ).join("");
  }

  /* ------------------------------------------------------------ product card */
  function productCardHtml(p, wishlisted) {
    const img = p.imageUrl
      ? `<img src="${escapeHtml(p.imageUrl)}" alt="${escapeHtml(p.name)}" loading="lazy" onerror="this.parentElement.innerHTML='<div class=ph>🛍️</div>'">`
      : `<div class="ph">🛍️</div>`;
    const outOfStock = Number(p.stock) <= 0;
    return `
      <div class="product-card" data-product-id="${p.id}">
        <a href="/product.html?id=${p.id}" class="product-media" aria-label="${escapeHtml(p.name)}">
          ${img}
          ${outOfStock ? `<span class="stock-tag">Out of stock</span>` : ""}
        </a>
        <button class="wishlist-fab ${wishlisted ? "active" : ""}" data-wishlist-btn data-id="${p.id}" aria-label="Toggle wishlist">${ICON.heart}</button>
        <div class="product-body">
          <span class="product-category">${escapeHtml((p.category && p.category.name) || "General")}</span>
          <a href="/product.html?id=${p.id}"><h3 class="product-name">${escapeHtml(p.name)}</h3></a>
          <div class="rating-row"><span class="stars">${"★".repeat(Math.round(p.rating || 0))}${"☆".repeat(5 - Math.round(p.rating || 0))}</span><span>${(p.rating || 0).toFixed ? p.rating.toFixed(1) : p.rating}</span></div>
          <div class="price-row"><span class="price">${money(p.price)}</span></div>
        </div>
        <div class="product-actions">
          <button class="btn btn-primary btn-sm" data-cart-btn data-id="${p.id}" ${outOfStock ? "disabled" : ""}>${ICON.bag} Add to cart</button>
        </div>
      </div>`;
  }

  async function toggleWishlist(id, isActive, btn) {
    const user = await getMe();
    if (!user) {
      window.location.href = "/login.html";
      return;
    }
    try {
      if (isActive) {
        await api("/api/wishlist/" + id, { method: "DELETE" });
        toast("Removed from wishlist", "info");
        if (btn) btn.classList.remove("active");
      } else {
        await api("/api/wishlist/" + id, { method: "POST" });
        toast("Added to wishlist", "success");
        if (btn) btn.classList.add("active");
      }
    } catch (e) {
      toast(e.message, "error");
    }
  }

  async function addToCart(id, qty) {
    try {
      await api("/api/cart/" + id + "?quantity=" + (qty || 1), {
        method: "POST",
      });
      toast("Added to cart", "success");
      refreshCartBadge();
    } catch (e) {
      toast(
        e.message === "Login required"
          ? "Please log in to add items to your cart."
          : e.message,
        "error",
      );
      if (e.message === "Login required")
        setTimeout(() => (window.location.href = "/login.html"), 700);
    }
  }

  async function refreshCartBadge() {
    const badge = document.getElementById("cartBadge");
    if (!badge) return;
    try {
      const items = await api("/api/cart");
      const count = (items || []).reduce((s, i) => s + (i.quantity || 0), 0);
      if (count > 0) {
        badge.hidden = false;
        badge.textContent = count > 99 ? "99+" : count;
      } else {
        badge.hidden = true;
      }
    } catch (e) {
      /* not logged in */
    }
  }

  function wireProductGrid(container) {
    container.addEventListener("click", (e) => {
      const cartBtn = e.target.closest("[data-cart-btn]");
      if (cartBtn) {
        e.preventDefault();
        addToCart(cartBtn.dataset.id);
        return;
      }
      const wishBtn = e.target.closest("[data-wishlist-btn]");
      if (wishBtn) {
        e.preventDefault();
        toggleWishlist(
          wishBtn.dataset.id,
          wishBtn.classList.contains("active"),
          wishBtn,
        );
      }
    });
  }

  global.SS = {
    ICON,
    escapeHtml,
    money,
    stars,
    api,
    toast,
    getMe,
    requireAuth,
    logout,
    renderHeader,
    renderFooter,
    confirmDialog,
    skeletonCards,
    productCardHtml,
    toggleWishlist,
    addToCart,
    refreshCartBadge,
    wireProductGrid,
  };
})(window);
