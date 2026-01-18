# 🏢 Property Management System

A robust, full-stack management console designed to automate property operations. This system streamlines tenant management, utility tracking, and financial reporting with a focus on professional billing and high-fidelity data exports.



## 🌟 Key Features

### 📑 Intelligent Billing & Invoicing
* **Bulk Generation**: Generate invoices for all active tenants for a specific month with a single click.
* **Utility Splitting**: Automatically calculates electricity charges by combining individual tenant meter consumption with shared common area units.
* **Professional PDF Invoices**: 
    * **Click-to-Pay**: Integrated UPI deep links (`upi://`) that open payment apps directly on mobile.
    * **Dynamic QR Codes**: Auto-generated QR codes based on real-time bill totals for easy scanning from any device.

### ⚙️ Administrative Dashboard
* **Tenant Management**: Full CRUD operations for tenant registration, including Aadhar, phone, and joining date tracking.
* **Meter Reading Console**: Efficient recording of monthly readings with automatic fetching of previous data.
* **Dynamic Pricing Control**: Update room rent and unit electricity rates globally via a secure settings panel.

---

## 🛠️ Tech Stack

### Backend (Java / Spring Boot)
- **Spring Data JPA**: For reliable relational data management.
- **PostgreSQL**: Enterprise-grade persistence.
- **Apache POI**: High-performance Excel generation.
- **OpenPDF**: Custom PDF document rendering.
- **ZXing**: Barcode and QR code generation.
- **Flyway**: Database schema versioning.

### Frontend (React / Vite)
- **Tailwind CSS**: Modern, responsive dashboard design.
- **Lucide React**: Clean and consistent iconography.
- **Axios**: Efficient API handling.

---

## 📂 Project Structure

```text
root/
├── backend/            # Spring Boot application
│   ├── src/main/java/  # Business logic, entities, and export services
│   └── pom.xml         # Maven configuration
├── frontend/           # React + Vite application
│   ├── src/App.jsx     # Main Dashboard UI & state management
│   └── tailwind.config # Design system configuration
└── README.md           # Documentation
