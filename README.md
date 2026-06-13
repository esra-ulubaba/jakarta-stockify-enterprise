# 📦 Jakarta Stockify Enterprise: Inventory & Movement Tracking System

<p align="center">
  <a href="#-türkçe">🇹🇷 Türkçe Açıklama</a> | 
  <a href="#-english">🇺🇸 English Description</a>
</p>

---

## 🖥️ Ekran Görüntüleri / Screenshots

#### 📊 System Dashboard / Genel Özet Paneli
<img width="2160" height="1440" alt="dashboard" src="https://github.com/user-attachments/assets/427cb7a1-fc1b-4f90-9eee-032d20816e7d" />

#### 📦 Product Management & Excel Reporting / Ürün Yönetimi ve Excel Raporlama
<img width="2160" height="1440" alt="product management" src="https://github.com/user-attachments/assets/696bc359-d510-4229-96e7-ea56df3679fd" />

#### 🔄 Stock Movements Tracking / Stok Giriş-Çıkış Hareketleri
<img width="2160" height="1440" alt="stock movement" src="https://github.com/user-attachments/assets/1cd8e056-9061-4b32-987b-f6501dd8ccab" />

#### 📜 Enterprise Audit Trail (System Logs) / Kurumsal Sistem Geçmişi ve İlk Başlatma Logu
<img width="2160" height="1440" alt="system history" src="https://github.com/user-attachments/assets/64d583b3-caf8-4f1c-b916-1b9b9a76bc50" />

#### 👤 User Management / Kullanıcı Yönetimi
<img width="2160" height="1440" alt="user management" src="https://github.com/user-attachments/assets/23e9fc87-5f28-478d-a956-60f2c301ed5c" />

## 🇹🇷 Türkçe

Stockify Enterprise, kurumsal yazılım mimarilerine (Enterprise Architecture) uygun olarak katmanlı yapıda sıfırdan geliştirilmiş, web tabanlı bir Envanter ve Stok Yönetim Çekirdeğidir (Core System). 

Bu sistem; endüstriyel işletmelerin stok giriş-çıkış süreçlerini optimize etmek, kritik ürün seviyelerini anlık izlemek ve tüm bu yönetimsel operasyonları rol tabanlı yetkilendirme (RBAC) ile güvenli bir şekilde denetim izi (Audit Trail) altında tutmak amacıyla endüstriyel standartlarda tasarlanmıştır.

### 🛠️ Teknolojik Altyapı

* **Backend (Arka Plan):** Java 25 LTS, Jakarta EE (EJB 4.0, JPA 3.0 / EclipseLink)
* **Database (Veritabanı):** PostgreSQL
* **Frontend (Arayüz):** Jakarta Server Faces (JSF), Bootstrap 5, Bootstrap Icons
* **Uygulama Sunucusu:** GlassFish 7

### 🚀 Mimari Tasarım ve Geliştirme Süreci

1. **Veritabanı Modellemesi ve Normalizasyon (DB Design):** Sistem; User (Kullanıcı), Product (Ürün), StockMovement (Stok Hareketi) ve SystemLog (Sistem Geçmişi / Audit) olmak üzere 4 temel ana modülden oluşmaktadır. Veri tutarlılığı `@ManyToOne` ilişkileriyle optimize edilmiştir.
2. **Katmanlı Mimari ve Tasarım Kalıpları (Facade Pattern):** İş mantığının sunum katmanından izole edilmesi amacıyla Facade tasarım kalıbı uygulanmıştır. `@Local` arabirimler ve `@Stateless` EJB yapıları vasıtasıyla `EntityManager` transaction süreçleri sızdırmaz hale getirilmiştir.
3. **İleri Düzey İş Mantığı ve Kararlılık Çalışmaları (Refactoring):** Silme işlemlerinde `ConstraintViolationException` hatalarını önlemek adına *On Delete Set Null* mantığı el ile kurulmuştur. Sunucu tarafı validasyonlar ve yerel dil (`Locale`) kaynaklı `I` -> `ı` karakter uyuşmazlıkları backend seviyesinde çözülmüştür.
4. **Hafif ve Bağımsız Raporlama Motoru (Native Excel/CSV Export):** Üçüncü parti kütüphanelere bağımlı kalmadan, saf Jakarta `ExternalContext` response akışları yönetilerek **UTF-8 BOM (\uFEFF)** damgasıyla entegre Türkçe Excel (CSV) raporlama motoru inşa edilmiştir.

### 💻 Kurulum ve Çalıştırma Talimatları

1. PostgreSQL sunucunuz üzerinde `envanter` isminde boş bir veritabanı oluşturun.
2. Uygulama sunucunuzun yönetim panelinden `jdbc/envanterPSQL` adında bir Connection Pool ve JTA Data Source tanımlayın.
3. Projeyi IDE üzerinde açıp `Clean and Build` komutunu çalıştırın.
4. İlk çalıştırmada DDL generation tabloları otomatik oluşturacak ve `DataInitializer` sınıfı sisteme kurucu admin hesabını otomatik yükleyecektir.

---

## 🇺🇸 English

Stockify Enterprise is a web-based Inventory and Stock Management Core System developed from scratch in a layered structure compliant with Enterprise Architecture standards.

The system is designed to optimize stock inbound/outbound processes for enterprises, monitor critical stock levels in real-time, and securely log all management operations under an Audit Trail via Role-Based Access Control (RBAC).

### 🛠️ Tech Stack

* **Backend:** Java 25 LTS, Jakarta EE (EJB 4.0, JPA 3.0 / EclipseLink)
* **Database:** PostgreSQL
* **Frontend:** Jakarta Server Faces (JSF), Bootstrap 5, Bootstrap Icons
* **Application Server:** Payara Server / GlassFish 7

### 🚀 Architectural Design & Development Process

1. **Database Modeling and Normalization (DB Design):** The core architecture consists of 4 main modules: User, Product, StockMovement, and SystemLog (Audit Module). Data consistency is optimized using `@ManyToOne` relationships.
2. **Layered Architecture & Design Patterns (Facade Pattern):** The Facade Design Pattern is implemented to decouple business logic from the presentation layer. Through `@Local` interfaces and `@Stateless` EJBs, `EntityManager` transaction flows are kept thread-safe and isolated.
3. **Advanced Business Logic & Robustness (Refactoring):** To prevent `ConstraintViolationException` during delete operations, a manual *On Delete Set Null* logic was established. Server-side validations and OS-level local language (`Locale`) `I` -> `ı` character mismatches were completely resolved at the backend level.
4. **Lightweight & Independent Reporting Engine (Native Excel/CSV Export):** Instead of relying on heavy third-party dependencies, a custom reporting engine was built by directly managing native Jakarta `ExternalContext` response streams, embedding a **UTF-8 BOM (\uFEFF)** prefix for flawless spreadsheet character rendering.

### 💻 Installation and Deployment Instructions

1. Create an empty database named `envanter` on your PostgreSQL server.
2. Define a Connection Pool and JTA Data Source named `jdbc/envanterPSQL` via your application server admin panel.
3. Open the project in your IDE and execute the `Clean and Build` command.
4. Upon the first launch, the DDL generation strategy will automatically deploy the schema, and the `DataInitializer` bean will bootstrap the default system administrator account.
