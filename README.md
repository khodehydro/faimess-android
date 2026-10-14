# Faimess Android · Frindle UI

[![Android CI](https://github.com/khodehydro/faimess-android/actions/workflows/android.yml/badge.svg?branch=arena%2Fce2383fc-faimess-android)](https://github.com/khodehydro/faimess-android/actions/workflows/android.yml)

نسخهٔ اولیهٔ اپ اندروید با **Kotlin و Jetpack Compose**، با الگوبرداری از تصویر مرجع Frindle. نام و متن‌های انگلیسی، تم سفید/سبز لیمویی و ساختار صفحه فعلاً مطابق مرجع نگه داشته شده‌اند تا در مراحل بعد بر اساس نیازهای محصول تغییر کنند.

> **شاخهٔ این نسخه:** `arena/ce2383fc-faimess-android`
> تغییرات این مرحله روی این شاخه هستند، نه `main`.

## آنچه ساخته شده

- هدر Frindle، لوگوی سبز، دکمهٔ انتشار و اعلان‌ها.
- ردیف افقی استوری با حلقه‌های سبز، نشان افزودن و برچسب LIVE نمایشی.
- فید اسکرول‌شونده با کارت‌های گرد، تصویر بزرگ، پیش‌نمایش ویدئو، متن بازشونده، لایک، کامنت و اشتراک‌گذاری.
- نوار ناوبری شناور نیمه‌شفاف پنج‌دکمه‌ای با حالت انتخاب سبز.
- صفحات سبک Discover، Calendar، Profile و Search برای متصل بودن دکمه‌ها؛ تقویم عمداً وضعیت خالی دارد.
- استوری قابل مرور، ذخیرهٔ پست، کامنت محلی، انتشار پست/استوری نمونه و اشتراک متن با Share Sheet اندروید.
- تصاویر و فونت داخل APK؛ اجرای رابط بدون اینترنت و بدون درخواست مجوز.
- پشتیبانی از edge-to-edge، safe insets، تغییر اندازهٔ صفحه، حالت افقی و بازسازی Activity.

**محدودهٔ نسخه:** این یک نمونهٔ رابط کاربری است، نه شبکهٔ اجتماعی آمادهٔ انتشار. سرور، حساب کاربری، آپلود عکس، اعلان زنده، پخش ویدئو، پخش زنده، دیتابیس دائمی و تقویم واقعی هنوز وجود ندارند. تصاویر نمونهٔ مشابه جایگزین عکس‌های داخل مرجع شده‌اند؛ بازتولید پیکسلی همان عکس‌ها ادعا نمی‌شود. قاب آیفون و Dynamic Island بخشی از اپ اندروید نیستند.

## دریافت مستقیم از GitHub — بدون داشتن پوشهٔ پروژه

### روش پیشنهادی از صفحهٔ شروع Android Studio

1. **Get from VCS / Clone Repository** را انتخاب کنید (نام گزینه به نسخهٔ Studio بستگی دارد).
2. این URL را وارد کنید:

   ```text
   https://github.com/khodehydro/faimess-android.git
   ```

3. **Clone** را بزنید؛ Android Studio پوشه را خودش می‌سازد.
4. در پنجرهٔ پروژه، از **View → Tool Windows → Terminal** اجرا کنید:

   ```bash
   git fetch origin
   git switch --track origin/arena/ce2383fc-faimess-android
   ```

   اگر شاخه از قبل محلی است، فقط `git switch arena/ce2383fc-faimess-android` را اجرا کنید.
5. **Sync Project with Gradle Files** را بزنید و یک دستگاه/امولاتور Android 8.0 یا جدیدتر انتخاب کنید.

### روش دستوری: کلون مستقیم شاخهٔ آماده

این دستورها هیچ پوشهٔ پروژهٔ از قبل موجودی لازم ندارند؛ `git clone` پوشه را ایجاد می‌کند. اگر هنوز در صفحهٔ Welcome هستید و ترمینال داخلی ندارید، همین دستورها را در PowerShell یا Terminal سیستم اجرا کنید.

```bash
cd ~
git clone --branch arena/ce2383fc-faimess-android --single-branch https://github.com/khodehydro/faimess-android.git
cd faimess-android
```

سپس پوشه را مستقیماً در Android Studio باز کنید:

**Windows / PowerShell**، در مسیر نصب معمول:

```powershell
& "$env:ProgramFiles\Android\Android Studio\bin\studio64.exe" "$PWD"
```

**macOS:**

```bash
open -a "Android Studio" .
```

**Linux**، اگر launcher در PATH است:

```bash
studio .
```

اگر launcher در دسترس نبود، در Android Studio گزینهٔ **Open** را بزنید و پوشهٔ `faimess-android` ساخته‌شده توسط Git را انتخاب کنید؛ نیازی به ساخت پروژهٔ خالی یا کپی کردن فایل‌ها نیست.

## پیش‌نیاز و اولین اجرا

| ابزار | نسخهٔ پین‌شده |
| --- | --- |
| Android Studio | Meerkat 2024.3.1 یا نسخهٔ جدیدترِ سازگار با AGP 8.9 |
| JDK / Gradle JDK | 17 |
| Android Gradle Plugin | 8.9.2 |
| Gradle Wrapper | 8.11.1، همراه پروژه |
| Kotlin / Compose compiler plugin | 2.1.20 |
| Compose BOM | 2025.04.00 |
| Compile / Target SDK | 35 / Android 15 |
| Minimum SDK | 26 / Android 8.0 |
| Application ID | `com.khodehydro.faimess` |

در **SDK Manager** پلتفرم Android 15 / API 35 و Build Tools پیشنهادی را نصب کنید. در **Settings → Build, Execution, Deployment → Build Tools → Gradle**، Gradle JDK را روی **JDK 17** قرار دهید. Wrapper با پروژه آمده است؛ Gradle جداگانه لازم نیست. اولین Sync برای دریافت Gradle و وابستگی‌ها به اینترنت نیاز دارد؛ میرور مایکت اولویت اول مخزن‌های پلاگین و کتابخانه است و Wrapper نیز از مایکت دانلود می‌شود. منابع رسمی برای artifactهای پیدا‌نشده بعد از میرور باقی مانده‌اند. خود اپ در زمان اجرا آفلاین است. جزئیات و دستورات دریافت تغییرات در [راهنمای مایکت](docs/MYKET_MIRROR.md) آمده است.

`local.properties` و مسیر SDK مختص سیستم شما هستند و عمداً در Git نیستند؛ Android Studio آن‌ها را تنظیم می‌کند.

**Windows، از ترمینال پروژه:**

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

**macOS / Linux:**

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

برای `installDebug` باید دستگاه یا امولاتور روشن و متصل باشد. ساده‌تر: تنظیم اجرای **app** را انتخاب کنید و دکمهٔ **Run ▶** را بزنید.

APK دیباگ در این مسیر ساخته می‌شود:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## دریافت تغییرات بعدی

پس از اولین کلون، در ترمینال همان پروژه اجرا کنید:

```bash
git switch arena/ce2383fc-faimess-android
git pull --ff-only origin arena/ce2383fc-faimess-android
```

بعد Gradle Sync را اجرا کنید. اگر تغییر محلی دارید، قبل از دریافت آن را commit یا stash کنید؛ از دستورهای حذف‌کننده برای همگام‌سازی استفاده نکنید.

## بررسی و تاریخچه

- [CHANGELOG.md](CHANGELOG.md): فهرست نسخه‌ها و آخرین تغییرات.
- [docs/DEVELOPMENT_LOG.md](docs/DEVELOPMENT_LOG.md): گزارش این مرحله، شاخه و وضعیت بررسی.
- [docs/DESIGN_REFERENCE.md](docs/DESIGN_REFERENCE.md): مشخصات طراحی و تفاوت‌های عمدی با مرجع.
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): راهنمای محل تغییر هر قسمت.
- [docs/TESTING.md](docs/TESTING.md): دستورات تست و دریافت APK از Actions.
- [docs/ASSETS.md](docs/ASSETS.md): منشأ تصاویر و مجوز فونت.
- [GitHub Actions](https://github.com/khodehydro/faimess-android/actions/workflows/android.yml): ساخت APK، تست واحد، lint و تست Compose روی امولاتور.

برای مشاهدهٔ تاریخچه در ترمینال:

```bash
git log --oneline --decorate -15
git show --stat HEAD
```
