package com.khodehydro.faimess.data

import com.khodehydro.faimess.model.LyricLine
import com.khodehydro.faimess.model.Track

/**
 * The bilingual lyric sheets the website ships (`src/data/lyrics.ts`): the original line, its
 * Persian translation and the second it starts on. Every artist in the roster is fictional, so
 * these are original demo lyrics written for FAIMESS, not transcriptions of real songs. The site
 * serves them from `/api/tracks/{id}`; "Afterimage" (nt7) has no sheet yet, like on the site.
 */
object DemoLyrics {
    // NOVAE — Afterglow
    private val nt1 = listOf(
        LyricLine(0, "해가 진 뒤에도 남아 있는 빛", "نوری که بعد از غروب هم می‌مونه"),
        LyricLine(12, "We don’t need the sun tonight", "امشب به خورشید نیازی نداریم"),
        LyricLine(24, "손끝에서 번지는 노을", "غروبی که از نوک انگشت‌ها پخش می‌شه"),
        LyricLine(38, "Hold it, hold it, don’t let go", "نگهش دار، نگهش دار، رهاش نکن"),
        LyricLine(54, "우리는 천천히 빛나", "ما آرام‌آرام می‌درخشیم"),
        LyricLine(72, "Afterglow, afterglow — 아직 뜨거워", "اَفتِرگلو، اَفتِرگلو — هنوز داغه"),
        LyricLine(96, "Stay until the morning comes", "بمون تا صبح از راه برسه"),
        LyricLine(128, "이 밤이 다 지나가도", "حتی وقتی این شب کاملاً تموم بشه"),
        LyricLine(158, "너의 빛은 남아 있어", "نورِ تو باقی می‌مونه"),
    )
    // AXION — Midnight Seoul
    private val nt2 = listOf(
        LyricLine(0, "자정의 도시, 불빛이 쏟아져", "شهرِ نیمه‌شب، چراغ‌ها سرازیر می‌شن"),
        LyricLine(14, "Midnight Seoul, we run the night", "سئولِ نیمه‌شب، شب رو ما می‌چرخونیم"),
        LyricLine(30, "빗속의 네온, 발밑에 흐르는 별", "نئون توی بارون، ستاره‌هایی که زیر پامون روانه"),
        LyricLine(48, "No sleep, no brake, only us", "نه خواب، نه ترمز، فقط ما"),
        LyricLine(70, "새벽 세 시, 아직 깨어 있어", "ساعت سهٔ بامداد، هنوز بیداریم"),
        LyricLine(100, "Turn the city up, we’re alive", "شهر رو بلند کن، ما زنده‌ایم"),
        LyricLine(140, "이 밤을 멈추지 마", "این شب رو متوقف نکن"),
    )
    // SEORA — Paper Heart
    private val nt3 = listOf(
        LyricLine(0, "종이로 접은 내 마음", "قلبم رو از کاغذ ساختم"),
        LyricLine(11, "하나, 둘 — 조심히 펼쳐 봐", "یک، دو — آروم بازش کن"),
        LyricLine(26, "Paper heart, don’t tear it apart", "قلب کاغذی، پاره‌اش نکن"),
        LyricLine(42, "네 손끝에 구겨져도", "حتی وقتی زیر انگشت‌هات چین می‌خوره"),
        LyricLine(62, "I’ll fold it back for you", "برای تو از نو تاش می‌کنم"),
        LyricLine(92, "바람이 불어도, 나는 여기 있어", "حتی وقتی باد بلند می‌شه، من همین‌جام"),
        LyricLine(132, "조용히 너를 안아 줄게", "آروم بغلت می‌کنم"),
    )
    // LUNEX — Neon Bloom
    private val nt4 = listOf(
        LyricLine(0, "어둠 속에서 피어난", "توی تاریکی شکوفه داد"),
        LyricLine(13, "Neon bloom, neon bloom", "نئون‌بلوم، نئون‌بلوم"),
        LyricLine(28, "핑크빛 연기, 숨을 쉬어", "دودِ صورتی، نفس می‌کشم"),
        LyricLine(45, "Every color on my skin", "هر رنگی روی پوستِ من"),
        LyricLine(66, "꺼지지 마, 아직 피어나", "خاموش نشو، هنوز شکوفه می‌دی"),
        LyricLine(100, "Bloom until the morning light", "شکوفه بده تا نورِ صبح"),
        LyricLine(140, "이 밤은 우리의 무대", "این شب صحنهٔ ماست"),
    )
    // PRISM9 — Halo Drive
    private val nt5 = listOf(
        LyricLine(0, "시동을 걸어, 밤을 달려", "ماشین رو روشن کن، توی شب برون"),
        LyricLine(15, "Halo drive, halo drive", "هالو درایو، هالو درایو"),
        LyricLine(32, "헤드라이트에 별이 부서져", "ستاره‌ها روی چراغِ جلو می‌شکنن"),
        LyricLine(52, "Let the bassline take the wheel", "بذار خطِ باس فرمان رو بگیره"),
        LyricLine(78, "속도를 올려, 심장이 뛰어", "سرعت رو بالا ببر، قلبم می‌تپه"),
        LyricLine(112, "We don’t stop till the sunrise", "تا طلوع آفتاب وای نمی‌ستیم"),
        LyricLine(156, "창밖의 도시가 흐른다", "شهر از پشت پنجره جاری می‌شه"),
    )
    // HANEUL — Silver Hour
    private val nt6 = listOf(
        LyricLine(0, "은빛으로 물든 창가", "پنجره‌ای که نقره‌ای شده"),
        LyricLine(16, "조용히 너를 기다려", "آروم منتظرت می‌مونم"),
        LyricLine(34, "Silver hour, stay a little longer", "ساعتِ نقره‌ای، یه‌کم بیشتر بمون"),
        LyricLine(58, "말하지 않아도 알아", "بدون حرف هم می‌دونم"),
        LyricLine(88, "시간이 멈춘 것 같아", "انگار زمان ایستاده"),
        LyricLine(126, "그 빛이 사라지기 전에", "قبل از اینکه اون نور محو بشه"),
        LyricLine(176, "내 손을 잡아 줘", "دستم رو بگیر"),
    )
    // PRISM9 — Cherry Static
    private val tr3 = listOf(
        LyricLine(0, "체리빛 정전기, 손끝이 찌릿", "الکتریسیتهٔ گیلاسی، سرِ انگشت‌ها گزگز می‌کنه"),
        LyricLine(14, "Cherry static on my lips", "استاتیکِ گیلاسی روی لب‌هام"),
        LyricLine(30, "깨진 라디오처럼 지직지직", "مثل رادیوی خراب خش‌خش می‌کنه"),
        LyricLine(50, "Turn the noise into a song", "این نویز رو تبدیل کن به آهنگ"),
        LyricLine(74, "심장 소리가 비트가 돼", "صدای قلبم شده بیت"),
        LyricLine(108, "멈추지 마, 계속 돌려", "نگه ندار، همین‌طور بچرخونش"),
    )
    // VELVET MOON — Gravity
    private val tr5 = listOf(
        LyricLine(0, "달빛이 우리를 당겨", "مهتاب ما رو به سمت خودش می‌کشه"),
        LyricLine(15, "Gravity, gravity, you and me", "گرانش، گرانش، تو و من"),
        LyricLine(34, "도시의 불빛 아래서", "زیر چراغ‌های شهر"),
        LyricLine(56, "떠 있는 마음, 둥둥", "قلبِ شناور، آروم می‌چرخه"),
        LyricLine(86, "Fall into the night with me", "با من توی شب بیفت"),
        LyricLine(122, "아침이 오면 놓아 줄게", "وقتی صبح بیاد، رهات می‌کنم"),
    )
    // KAIROS — Blue Signal
    private val tr6 = listOf(
        LyricLine(0, "파란 신호가 깜빡여", "سیگنالِ آبی چشمک می‌زنه"),
        LyricLine(12, "Blue signal, can you hear me now?", "سیگنالِ آبی، الان صدای منو می‌شنوی؟"),
        LyricLine(28, "주파수 사이로 너를 찾아", "بین فرکانس‌ها دنبالت می‌گردم"),
        LyricLine(46, "Don’t lose the line tonight", "امشب این خط رو گم نکن"),
        LyricLine(70, "대답해, 아주 작게라도", "جواب بده، حتی خیلی آروم"),
        LyricLine(104, "이 신호는 너에게 가는 중", "این سیگنال داره به سمت تو می‌ره"),
    )

    private val sheets: Map<String, List<LyricLine>> = mapOf(
        "nt1" to nt1, "nt2" to nt2, "nt3" to nt3, "nt4" to nt4, "nt5" to nt5, "nt6" to nt6, "tr3" to tr3, "tr5" to tr5, "tr6" to tr6,
    )

    /** The sheet for [track], or null when nobody has submitted one yet. */
    fun forTrack(track: Track?): List<LyricLine>? = track?.let { sheets[it.id] }

    val trackIdsWithLyrics: Set<String> get() = sheets.keys
}
