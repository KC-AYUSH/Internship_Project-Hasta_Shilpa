# Hasta-Shilpa: Design Bridge for Artisans

Hasta-Shilpa is a modern, real-time ecosystem designed specifically for bamboo and cane artisans. It bridges the gap between traditional craftsmanship and modern design, providing artisans with technical tools, design inspiration, and a global marketplace.

## 🚀 Features

### 1. Real-time Marketplace
*   **Artisan Uploads:** Artisans can list their products instantly using their phone's **Camera** or **Gallery**.
*   **Cloud Sync:** Product data and high-resolution images are synced across all devices via Firebase.
*   **Artisan Guard:** Built-in logic prevents artisans from buying their own products while allowing them to manage their listings.

### 2. Modern Design Trends & Technical Blueprints
*   **Curated Inspiration:** A feed of modern, Scandinavian, and contemporary bamboo/cane design ideas.
*   **Technical Prototypes:** Every design features a dedicated blueprint view with **architectural measurement lines** (Width, Height, Depth).
*   **Focused Workspace:** A clean, dark-mode technical canvas for artisans to use in the workshop.

### 3. Smart Shopping System
*   **Real-time Cart:** Add items, view live total price calculations, and remove items with instant cloud feedback.
*   **Secure Checkout:** Seamlessly transition items from the cart to a permanent transaction history.
*   **Activity Tracking:** A dedicated page to track **Bought** and **Sold** items, helping artisans manage their business.

### 4. Artisan Tools
*   **Material Tracker:** Log bamboo poles used and hours worked per batch, stored securely in the cloud.
*   **Price Suggester:** A professional calculator to help artisans determine fair selling prices based on materials, labor, and desired profit margins.

### 5. Profile Management
*   **Digital Identity:** Real-time profile updates including Name, Phone, and Role.
*   **Profile Picture:** Integrated cloud-based profile photo management using Firebase Storage.
*   **Cross-Device Auth:** Secure login and session persistence using Firebase Authentication and Google Sign-In.

## 🛠 Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Modern Declarative UI)
*   **Backend:** Firebase (Authentication, Firestore, Storage)
*   **Architecture:** MVVM (Model-View-ViewModel) for clean data flow
*   **Navigation:** Compose Navigation with a custom **Scrollable Bottom Bar**
*   **Image Loading:** Coil (Image loading and caching)

## 📱 Getting Started

1.  **Clone the project.**
2.  **Add `google-services.json`** to the `app/` directory.
3.  **Sync Gradle** to download dependencies.
4.  **Run on Device:** Ensure a physical device or emulator is connected.

## 📐 Technical Specifications

The app uses a customized **Earthy Theme** representing the natural materials used in the craft:
*   **Primary Green (#4B663C):** Represents bamboo and nature.
*   **Secondary Cream (#F9F5EB):** Represents natural cane and workspace surfaces.
*   **Technical Blue (#0D2137):** Used for architectural blueprinting.

---
*Built for the Artisan community.*
