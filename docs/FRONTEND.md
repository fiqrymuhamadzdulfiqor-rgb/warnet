# NexaNet Cafe Frontend System

A modern, server-side rendered (SSR) web interface for a cybercafe management system. Built with **Thymeleaf**, **HTML5**, and **CSS3 Vanilla**, featuring a responsive **Glassmorphism** design, dark mode aesthetics, and dynamic data binding with Spring Boot.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Architecture](#architecture)
5. [Project Structure](#project-structure)
6. [UI/UX Design System](#uiux-design-system)
7. [Setup & Installation](#setup--installation)
8. [Page Routes & Views](#page-routes--views)
9. [Data Binding Flow](#data-binding-flow)
10. [Interactive Components](#interactive-components)
11. [Responsiveness](#responsiveness)

---

## Overview

The NexaNet Cafe Frontend is a dynamic, server-rendered user interface designed to provide a seamless experience for both warnet guests and registered members. It utilizes Thymeleaf to tightly integrate frontend views with backend Java logic, ensuring secure and blazing-fast data rendering without the need for heavy client-side JavaScript frameworks.

**Key Highlights:**
- ✅ Server-Side Rendering (SSR) for fast initial load and SEO compatibility
- ✅ Premium Glassmorphism UI with Dark Mode aesthetics
- ✅ Pure CSS3 animations and interactive hover states
- ✅ Zero-dependency frontend (No NPM, Webpack, or Node.js required)
- ✅ Vanilla JavaScript for lightweight DOM manipulation (Modals/Pop-ups)
- ✅ Seamless integration with Spring Security and Session management

---

## Features

### Core Functionality
- **Member Dashboard**: Personalized hub displaying active PC billing, points, and transaction history.
- **Digital Canteen (F&B)**: Interactive catalog for ordering food and beverages with virtual cart visualization.
- **Reward System**: Dedicated page for exchanging points with gaming hardware or PC vouchers.
- **Thermal Receipt Simulation**: Custom-designed digital receipt for anonymous guests.

### Visual & Interactivity
- Interactive "Live" status indicators for active billing.
- Animated background glows (CSS keyframes).
- Custom pop-up modals for transaction confirmations.
- Responsive CSS Grid layouts adapting to mobile and desktop screens.

---

## Tech Stack

| Component          | Technology                  | Description |
| ------------------ | --------------------------- | ------- |
| **Template Engine**| Thymeleaf                   | Server-side Java template engine |
| **Markup**         | HTML5                       | Semantic page structure |
| **Styling**        | CSS3 (Vanilla)              | Custom styling, Flexbox, CSS Grid |
| **Scripting**      | JavaScript (ES6)            | Lightweight modal & DOM control |
| **Iconography**    | Bootstrap Icons             | Vector icons via CDN |
| **Typography**     | Google Fonts                | Inter (UI) & JetBrains Mono (Receipt) |

---

## Architecture

### Server-Side Rendering (SSR) Flow

```text
┌─────────────────────────────────────────────────────────┐
│                        Client Browser                   │
│               (Renders final HTML/CSS/JS)               │
└────────────────────▲────────────────────────────────────┘
                     │ 4. Returns Compiled HTML
┌────────────────────▼─────────────────────────────────────┐
│                      Spring Controller                   │
│   (Receives HTTP Request, fetches data from Service)     │
└────────────────────┬─────────────────────────────────────┘
                     │ 1. Passes Model Data
┌────────────────────▼────────────────────────────────────┐
│                    Thymeleaf Engine                     │
│  (Binds Java objects: ${user.nama}, ${riwayatMakan})    │
└────────────────────┬────────────────────────────────────┘
                     │ 2. Injects Data into Templates
┌────────────────────▼─────────────────────────────────────┐
│                    HTML Templates (.html)                │
│    (member-dashboard.html, tukar-poin.html, etc.)        │
└────────────────────┬─────────────────────────────────────┘
                     │ 3. Processes layout & logic
┌────────────────────▼────────────────────────────────────┐
│                   Final Output Generation               │
│         (Resolves conditionals: th:if, th:each)         │
└─────────────────────────────────────────────────────────┘

Project StructurePlaintextsrc/main/resources/
│
├── static/                               # Static assets served directly to client
│   ├── css/                              # (Optional) External CSS files
│   ├── js/                               # (Optional) External JS files
│   └── images/                           # Local images and logos
│
└── templates/                            # Thymeleaf dynamic HTML views
    │
    ├── admin/                            # Admin protected views
    │   ├── admin-menu-fb.html            # F&B catalog management
    │   └── admin-pesanan-fb.html         # Incoming order management
    │
    ├── member/                           # User protected views
    │   ├── member-dashboard.html         # Main user portal & history
    │   ├── pesan-makan.html              # F&B ordering interface
    │   └── tukar-poin.html               # Point redemption catalog
    │
    ├── public/                           # Publicly accessible views
    │   ├── index.html                    # Landing page / Login
    │   └── nota-makanan.html             # Digital receipt for guests
    │
    └── error/                            # Custom error pages
        ├── 403.html                      # Access denied view
        └── 500.html                      # Server error view

UI/UX Design SystemColor PaletteUsageHex CodePreviewApplicationBackground#0f172aSlate NavyMain body backgroundPrimary Accent#38bdf8Neon CyanButtons, highlights, iconsSecondary/Points#facc15Gold YellowPoints badges, "Live" statusSuccess#4ade80Neon GreenCompleted status, success modalsDanger/Error#f87171Soft RedError messages, failed alertsTypographyPrimary Font: Inter (Weights: 400, 600, 800, 900) - Clean, highly legible UI font.Monospace Font: JetBrains Mono - Used exclusively in nota-makanan.html to simulate thermal printer receipts.Glassmorphism ImplementationUsed to create depth and a futuristic gaming cafe vibe.CSS.glass-card {
    background: rgba(30, 41, 59, 0.6);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255,255,255,0.05);
    box-shadow: 0 20px 40px rgba(0,0,0,0.3);
}
Page Routes & Views1. Member DashboardRoute: GET /member-dashboardView: member-dashboard.htmlDescription: A CSS Grid layout displaying the user's profile, total points, active PC billing widget, and historical tables for both PC sessions and F&B orders.2. Point RedemptionRoute: GET /tukar-poinView: tukar-poin.htmlDescription: Catalog of redeemable hardware (Headsets, Mousepads, Keyboards). Implements a JavaScript modal for success validation.3. F&B OrderingRoute: GET /pesan-makanView: pesan-makan.htmlDescription: Digital canteen interface. Features logic to distinguish between Cash and Point payment methods.4. Digital Receipt (Guest)Route: GET /nota-makanan/{id}View: nota-makanan.htmlDescription: Read-only view for anonymous guests. Features a custom CSS linear-gradient zig-zag border to mimic torn paper.Data Binding FlowThymeleaf Directives UsedDirectiveUsage in NexaNetExampleth:textRenders plain text from Java Model<h2 th:text="${user.nama}">Name</h2>th:eachIterates over Lists/Collections<tr th:each="trx : ${riwayatMakan}">th:ifConditional rendering (True)<div th:if="${transaksiAktif != null}">th:unlessConditional rendering (False)<span th:unless="${method == 'Poin'}">#numbersFormats currency/decimals${#numbers.formatDecimal(harga, 0, 'COMMA', 0, 'POINT')}Interactive ComponentsPop-up Modal Flow (Point Redemption)Plaintext

1. User Clicks "Tukar Sekarang"
   ├─ Form submits POST request to /proses-tukar-poin
   └─ Controller processes data in Backend

2. Controller Redirection
   ├─ On Success: Redirects to GET /tukar-poin?sukses
   └─ On Error: Returns view with Model attribute ${error}

3. View Rendering (tukar-poin.html)
   ├─ Thymeleaf detects URL parameter: <div th:if="${param.sukses}">
   ├─ Modal HTML is rendered in the DOM
   └─ CSS animations (FadeIn, PopUp) execute automatically

4. User Interaction
   ├─ User shows screen to Cashier
   ├─ Clicks "Barang Sudah Saya Terima"
   └─ JS triggers window.location.href = '/member-dashboard'

Responsiveness

The application implements a mobile-first philosophy using native CSS Media Queries to ensure compatibility across all devices (Mobiles, Tablets, and Desktop PCs).CSS/* Example: Transforming 2-column layout to 1-column on smaller screens */
.dashboard-grid {
    display: grid;
    grid-template-columns: 350px 1fr; /* Desktop: Sidebar + Content */
}

@media (max-width: 992px) {
    .dashboard-grid {
        grid-template-columns: 1fr; /* Mobile: Stacked blocks */
    }
}
All data tables are wrapped in .table-responsive { overflow-x: auto; } to prevent layout breaks on horizontal data overflow.