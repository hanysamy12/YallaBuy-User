# 🛍️ **YallaBuy – M-Commerce Android App**

_A modern mobile shopping experience built with Jetpack Compose, Firebase, and Shopify._

---
## 📱 **Screenshots**

| Home | Product Details | Cart | Checkout |
|------|------------------|------|----------|
| ![Home](https://github.com/user-attachments/assets/f97b8a80-75ec-41c8-8fb8-f9df31c5edc1) | ![Details](https://github.com/user-attachments/assets/7a52450c-24e4-45f6-9e68-8aedf02c873d) | ![Cart](https://github.com/user-attachments/assets/51acf4f6-b243-4d39-8186-0b8bb5eb8b6b) | ![Checkout](https://github.com/user-attachments/assets/04b7c675-2ebb-4b8a-8fa1-b699eb11bb9d) |

---

## 🚀 **Features**

- 🔐 **Account Management** Firebase Authentication with Email Verification
- 🔎 **Browse & Search** for Shopify-integrated products  
- ❤️ **Add to Wishlist**  
- 🛒 **Add to Cart**  
- 📍 **Add Address** via Google Maps or Manual Input  
- 💳 **Secure Checkout & Payment** ( Google Pay)  
- 🧾 **View Order History**  
- 🧰 **Filter Products** by price  
- 🌐 **Multi-currency Support**

  ---

## 📐 **Architecture**

The app is built using a clean and scalable **MVVM architecture**:

- **Jetpack Compose** for UI
- **ViewModel + StateFlow** for reactive state management
- **Firebase** for Authentication
- **Shopify REST API** via Retrofit
- **Google Maps SDK** for address selection
- **SharedPreferences** for storing user data locally

---
## 🧰 **Technologies & Tools**

| Category           | Tools & Frameworks                         |
|--------------------|---------------------------------------------|
| 🧠 Programming      | Kotlin, Coroutines, Jetpack Compose         |
| 🏗 Architecture     | MVVM, Koin (Dependency Injection), StateFlow |
| 🔥 Backend          | Firebase (Auth, Email Verification)         |
| 📦 E-commerce API   | Shopify REST API + Retrofit                 |
| 🗺 Location Services| Google Maps SDK                             |
| 💳 Payment          | Google Pay                                  |
| 🧪 Testing          | JUnit, MockK                                 |
| 🔧 Preferences      | SharedPreferences                           |
| 💻 DevOps           | GitHub, Git                                 |

---
## ⚙️ **Getting Started**

### 🧾 _Prerequisites_
- Android Studio Hedgehog or later
- Firebase Project (for Auth & DB)
- Shopify store & access token
- Maps API key

### 🛠 _Setup Instructions_

1. **Clone the repository**
   ```bash
   git clone https://github.com/hanysamy12/YallaBuy-User.git
   cd YallaBuy
