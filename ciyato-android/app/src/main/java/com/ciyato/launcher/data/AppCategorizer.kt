package com.ciyato.launcher.data

/**
 * Pure-logic categorizer.
 * Uses package-name keywords + known app mappings.
 * No network calls. No remote categorization.
 */
object AppCategorizer {

    // Known package → primary category
    private val knownApps: Map<String, AppCategory> = mapOf(
        // Communication / Social
        "com.whatsapp"                    to AppCategory.COMMUNICATION,
        "com.whatsapp.w4b"                to AppCategory.WORK,
        "org.telegram.messenger"          to AppCategory.COMMUNICATION,
        "com.viber.voip"                  to AppCategory.COMMUNICATION,
        "com.skype.raider"                to AppCategory.COMMUNICATION,
        "com.discord"                     to AppCategory.SOCIAL,
        "com.instagram.android"           to AppCategory.SOCIAL,
        "com.facebook.katana"             to AppCategory.SOCIAL,
        "com.twitter.android"             to AppCategory.SOCIAL,
        "com.linkedin.android"            to AppCategory.WORK,
        "com.snapchat.android"            to AppCategory.SOCIAL,
        "com.tiktok"                      to AppCategory.ENTERTAINMENT,
        "com.reddit.frontpage"            to AppCategory.SOCIAL,
        "com.pinterest"                   to AppCategory.CREATIVITY,
        // Productivity / Work
        "com.google.android.gm"           to AppCategory.WORK,
        "com.microsoft.office.outlook"    to AppCategory.WORK,
        "com.google.android.apps.docs"    to AppCategory.WORK,
        "com.google.android.apps.sheets"  to AppCategory.WORK,
        "com.google.android.apps.slides"  to AppCategory.WORK,
        "com.microsoft.office.word"       to AppCategory.WORK,
        "com.microsoft.office.excel"      to AppCategory.WORK,
        "com.microsoft.office.powerpoint" to AppCategory.WORK,
        "com.notion.id"                   to AppCategory.PRODUCTIVITY,
        "com.todoist"                     to AppCategory.PRODUCTIVITY,
        "com.ticktick.task"               to AppCategory.PRODUCTIVITY,
        "com.evernote"                    to AppCategory.PRODUCTIVITY,
        "com.microsoft.launcher"          to AppCategory.PRODUCTIVITY,
        "com.slack"                       to AppCategory.WORK,
        "us.zoom.videomeetings"           to AppCategory.WORK,
        "com.google.android.apps.meetings" to AppCategory.WORK,
        "com.microsoft.teams"             to AppCategory.WORK,
        // Finance
        "com.robinhood.android"           to AppCategory.FINANCE,
        "com.coinbase.android"            to AppCategory.FINANCE,
        "com.paypal.android.p2pmobile"    to AppCategory.FINANCE,
        "com.venmo"                       to AppCategory.FINANCE,
        "com.cashapp"                     to AppCategory.FINANCE,
        "com.google.android.apps.walletnfcrel" to AppCategory.FINANCE,
        "com.chase.sig.android"           to AppCategory.FINANCE,
        "com.bankofamerica.cashpay"       to AppCategory.FINANCE,
        // Entertainment
        "com.google.android.youtube"      to AppCategory.ENTERTAINMENT,
        "com.netflix.mediaclient"         to AppCategory.ENTERTAINMENT,
        "com.spotify.music"               to AppCategory.ENTERTAINMENT,
        "com.amazon.avod.thirdpartyclient" to AppCategory.ENTERTAINMENT,
        "com.disney.disneyplus"           to AppCategory.ENTERTAINMENT,
        "com.hbo.hbonow"                  to AppCategory.ENTERTAINMENT,
        "tv.twitch.android.app"           to AppCategory.ENTERTAINMENT,
        "com.pandora.android"             to AppCategory.ENTERTAINMENT,
        "com.soundcloud.android"          to AppCategory.ENTERTAINMENT,
        // Travel
        "com.airbnb.android"              to AppCategory.TRAVEL,
        "com.ubercab"                     to AppCategory.TRAVEL,
        "com.lyft.android"                to AppCategory.TRAVEL,
        "com.google.android.apps.maps"    to AppCategory.TRAVEL,
        "com.waze"                        to AppCategory.TRAVEL,
        "com.booking"                     to AppCategory.TRAVEL,
        "com.expedia.booking"             to AppCategory.TRAVEL,
        // Shopping
        "com.amazon.mShoppingapp"         to AppCategory.SHOPPING,
        "com.ebay.mobile"                 to AppCategory.SHOPPING,
        "com.etsy.android"                to AppCategory.SHOPPING,
        // Creativity
        "com.adobe.lightroom"             to AppCategory.CREATIVITY,
        "com.adobe.Photoshop"             to AppCategory.CREATIVITY,
        "com.canva.editor"                to AppCategory.CREATIVITY,
        "com.vsco.cam"                    to AppCategory.CREATIVITY,
        // Daily / Utilities
        "com.android.camera2"             to AppCategory.DAILY,
        "com.google.android.dialer"       to AppCategory.DAILY,
        "com.google.android.apps.messaging" to AppCategory.DAILY,
        "com.android.settings"            to AppCategory.UTILITIES,
        "com.google.android.calculator"   to AppCategory.UTILITIES,
        "com.google.android.clock"        to AppCategory.DAILY,
        "com.android.chrome"              to AppCategory.UTILITIES,
        "org.mozilla.firefox"             to AppCategory.UTILITIES,
    )

    // Keyword → category for unknown apps
    private val labelKeywords: List<Pair<Regex, AppCategory>> = listOf(
        Regex("(?i)bank|wallet|pay|finance|invest|stock|crypto|cash|money") to AppCategory.FINANCE,
        Regex("(?i)social|friend|network|connect|community")                to AppCategory.SOCIAL,
        Regex("(?i)chat|message|talk|call|voice|meet|video")                to AppCategory.COMMUNICATION,
        Regex("(?i)work|office|mail|email|calendar|task|todo|note|doc|sheet|drive|cloud") to AppCategory.WORK,
        Regex("(?i)music|radio|podcast|audio|sound|spotify|youtube")       to AppCategory.ENTERTAINMENT,
        Regex("(?i)game|play|puzzle|quest|battle|word|chess")               to AppCategory.GAMES,
        Regex("(?i)travel|map|ride|uber|lyft|hotel|flight|trip|nav")       to AppCategory.TRAVEL,
        Regex("(?i)shop|store|buy|market|amazon|ebay|order|ecommerce")     to AppCategory.SHOPPING,
        Regex("(?i)photo|camera|gallery|album|picture|image|film|snap|lightroom|vsco") to AppCategory.CREATIVITY,
        Regex("(?i)settings|tools|utility|cleaner|manager|calculator|clock|alarm|file|storage") to AppCategory.UTILITIES,
        Regex("(?i)fitness|health|gym|run|workout|diet|meditat|sleep|yoga") to AppCategory.DAILY,
        Regex("(?i)news|read|book|learn|edu|course|study|language")        to AppCategory.PRODUCTIVITY,
    )

    private val packageKeywords: List<Pair<Regex, AppCategory>> = listOf(
        Regex("(?i)bank|finance|pay|wallet|invest|cash")    to AppCategory.FINANCE,
        Regex("(?i)social|instagram|facebook|twitter|snap") to AppCategory.SOCIAL,
        Regex("(?i)message|chat|telegram|whatsapp|signal")  to AppCategory.COMMUNICATION,
        Regex("(?i)game|games|gaming|puzzle|battle")        to AppCategory.GAMES,
        Regex("(?i)travel|maps|uber|lyft|airbnb|waze")     to AppCategory.TRAVEL,
        Regex("(?i)shop|amazon|ebay|market|buy")            to AppCategory.SHOPPING,
        Regex("(?i)music|spotify|soundcloud|pandora|ytm")  to AppCategory.ENTERTAINMENT,
        Regex("(?i)fitness|health|workout|gym|run")         to AppCategory.DAILY,
        Regex("(?i)settings|tools|util|calc|clock|alarm")  to AppCategory.UTILITIES,
        Regex("(?i)office|gmail|calendar|drive|docs|notion|work|slack|zoom|teams") to AppCategory.WORK,
        Regex("(?i)photo|camera|gallery|lightroom|vsco|snap") to AppCategory.CREATIVITY,
        Regex("(?i)news|kindle|read|book|learn|edu|duolingo") to AppCategory.PRODUCTIVITY,
    )

    fun categorize(packageName: String, label: String): AppCategory {
        // 1. Check exact known mapping
        knownApps[packageName]?.let { return it }
        // 2. Label keyword scan
        for ((regex, cat) in labelKeywords) {
            if (regex.containsMatchIn(label)) return cat
        }
        // 3. Package name keyword scan
        for ((regex, cat) in packageKeywords) {
            if (regex.containsMatchIn(packageName)) return cat
        }
        return AppCategory.OTHER
    }

    fun secondaryCategories(pkg: String, label: String, primary: AppCategory): List<AppCategory> {
        val extras = mutableListOf<AppCategory>()
        // WhatsApp / Telegram get Daily + Communication
        if (pkg.contains("whatsapp", true) || pkg.contains("telegram", true)) {
            if (primary != AppCategory.DAILY) extras += AppCategory.DAILY
            if (primary != AppCategory.COMMUNICATION) extras += AppCategory.COMMUNICATION
        }
        // Work apps get Productivity too
        if (primary == AppCategory.WORK) extras += AppCategory.PRODUCTIVITY
        // Social apps get Daily too
        if (primary == AppCategory.SOCIAL) extras += AppCategory.DAILY
        // Finance apps get Utilities too
        if (primary == AppCategory.FINANCE) extras += AppCategory.UTILITIES
        return extras.distinct()
    }
}
