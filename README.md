
# Xtreme IPTV - Android Only

مشغل IPTV قانوني، لا يحتوي على أي قنوات. المستخدم يدخل مصادره القانونية فقط.

## الميزات
- M3U / M3U8 parser مع tvg-id, tvg-logo, group-title
- Xtream Codes API: get_live_categories + get_live_streams + بناء روابط m3u8
- EPG: XMLTV parser (xmltv.php) وربط بـ epgId
- Categories: تجميع تلقائي + FilterChip
- Persist: Room DB لحفظ القوائم والمفضلة + DataStore للإعدادات
- Admin: شاشة إدارة القوائم (إضافة/حذف) محليا، لا تحتاج سيرفر
- Player: ExoPlayer يدعم HLS

## التشغيل من هاتف Android مباشرة (بدون PC)
1. ثبّت Android Studio أو AIDE على هاتفك؟ الأفضل: استخدم Android Studio على PC ثم Build APK. لكن إذا أردت من الهاتف فقط:
   - ثبّت Termux + تثبيت Gradle أو استخدم تطبيق "Spck Code Editor" أو "Acode" لفتح المشروع.
   - أو استخدم https://appetize.io أو GitHub Codespace لبناء APK.
2. افتح المشروع، Sync Gradle.
3. شغّل Build > Build APK.
4. ثبّت APK على هاتفك. أعطِ إذن INTERNET.
5. عند الفتح: اضغط + Playlist
   - لو عندك ملف m3u قانوني (قنواتك الخاصة أو IPTV مجاني بترخيص Creative Commons) الصق الرابط.
   - لو عندك سيرفر Xtream خاص بك وقانوني: أدخل host + username + password.
6. حقوق البث: التطبيق لا يفك تشفير ولا يتجاوز حماية. استخدم فقط محتوى تملك حقوقه أو مجاني مرخص. في الاتحاد الأوروبي وتونس، إعادة بث قنوات بدون ترخيص مخالفة.

## هيكل Admin المحلي
- SavedPlaylist يحفظ كل القوائم
- FavoriteChannel لحفظ المفضلة
- يمكنك إضافة شاشة AdminActivity لحذف القوائم: db.playlistDao().delete(id)

## تطوير EPG المتقدم
استخدم EpgParser.parse(xmlString) بعد تحميل xmltv.php. ثم اعرض events حسب الوقت الحالي.

## ملاحظة قانونية
هذا المشروع مشغل فقط (مثل VLC). لا يوفّر ولا يروّج لمصادر مقرصنة. أنت مسؤول عن التأكد أن M3U/Xtream الذي تدخله مرخص.
