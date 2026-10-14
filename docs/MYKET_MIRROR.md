# دریافت وابستگی‌ها از میرور مایکت

## تنظیمات پروژه

- در هر دو بخش `pluginManagement.repositories` و `dependencyResolutionManagement.repositories` فایل `settings.gradle.kts`، مخزن `https://maven.myket.ir/` اولویت اول است.
- مخزن‌های رسمی بعد از مایکت باقی مانده‌اند تا اگر یک artifact در میرور پیدا نشد، امکان جستجو در آن‌ها وجود داشته باشد. این ترتیب تضمین عبور خودکار از خطای شبکه، timeout یا خطای سرور میرور نیست.
- Gradle Wrapper نسخهٔ **8.11.1** از آدرس زیر دانلود می‌شود:

  ```text
  https://maven.myket.ir/gradle/distributions/gradle-8.11.1-bin.zip
  ```

- SHA-256 رسمی همان فایل در `distributionSha256Sum` ثبت شده است؛ در صورت اختلاف، Wrapper دانلود را نمی‌پذیرد. بررسی TLS و `validateDistributionUrl` غیرفعال نشده‌اند.
- مهلت شبکهٔ Wrapper از ۱۰ به ۶۰ ثانیه افزایش یافته است.
- نسخهٔ AGP همچنان **8.9.2**، Kotlin همچنان **2.1.20** و همهٔ نسخه‌های کتابخانه‌ها بدون تغییر هستند.

این تنظیمات، نصب Android SDK، JDK یا پراکسی شخصی سیستم را تغییر نمی‌دهند. JDK 17 و Android SDK Platform 35 همچنان لازم‌اند. فایل‌های تنظیمات محلی و اطلاعات اتصال شخصی نباید به Git اضافه شوند.

## دریافت در Android Studio / PowerShell

در ترمینال پوشهٔ پروژه اجرا کنید:

```powershell
git switch arena/ce2383fc-faimess-android
git pull --ff-only origin arena/ce2383fc-faimess-android
.\gradlew.bat --stop
.\gradlew.bat help --refresh-dependencies --info
```

سپس **File → Sync Project with Gradle Files** را بزنید. حالت Offline در Gradle باید خاموش باشد. تغییر آدرس Wrapper ممکن است باعث دانلود مجدد Gradle شود؛ نیازی به حذف کل cache یا کلون مجدد نیست.

تا پیش از Sync موفق، نبودن نمای Android یا تنظیم اجرای `app` طبیعی است؛ ایجاد Configuration دستی، مشکل دانلود پلاگین را رفع نمی‌کند.

## منابع تنظیمات

- [راهنمای رسمی Maven مایکت](https://maven.myket.ir/services/maven.html)
- [راهنمای رسمی Gradle Wrapper مایکت](https://maven.myket.ir/services/gradle-wrapper.html)
- [Checksum رسمی Gradle 8.11.1](https://gradle.org/release-checksums/#8.11.1)

در زمان این تغییر، فایل‌های POM مربوط به plugin markerهای AGP 8.9.2، Kotlin Android 2.1.20 و Compose Compiler 2.1.20 از مایکت قابل دریافت بودند. این بررسی به معنی تأیید دانلود تمام وابستگی‌ها یا موفقیت build کامل از طریق میرور نیست.
