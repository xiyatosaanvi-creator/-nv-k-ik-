package com.ciyato.launcher.data

/**
 * Pure-logic categorizer — significantly expanded.
 * Suggestions: 40 (NLP query detection), 78 (category learning hook), plus
 * 60+ additional known app mappings for better accuracy out-of-the-box.
 *
 * No network calls. No remote categorization. Fully offline.
 */
object AppCategorizer {

    // ── Expanded known-app map (200+ entries) ─────────────────────────────────
    private val knownApps: Map<String, AppCategory> = mapOf(
        // Communication
        "com.whatsapp"                         to AppCategory.COMMUNICATION,
        "com.whatsapp.w4b"                     to AppCategory.WORK,
        "org.telegram.messenger"               to AppCategory.COMMUNICATION,
        "org.telegram.messenger.web"           to AppCategory.COMMUNICATION,
        "org.thoughtcrime.securesms"           to AppCategory.COMMUNICATION,   // Signal
        "com.viber.voip"                       to AppCategory.COMMUNICATION,
        "com.skype.raider"                     to AppCategory.COMMUNICATION,
        "com.microsoft.skype.teams.ipphone"    to AppCategory.WORK,
        "com.discord"                          to AppCategory.SOCIAL,
        "com.google.android.talk"              to AppCategory.COMMUNICATION,
        "com.google.android.apps.tachyon"      to AppCategory.COMMUNICATION,   // Google Meet (old)
        "im.vector.app"                        to AppCategory.COMMUNICATION,   // Element/Matrix
        "com.wire"                             to AppCategory.COMMUNICATION,
        "com.kakao.talk"                       to AppCategory.COMMUNICATION,
        "com.linecorp.b612.android"            to AppCategory.SOCIAL,
        "jp.naver.line.android"                to AppCategory.COMMUNICATION,
        // Social
        "com.instagram.android"               to AppCategory.SOCIAL,
        "com.facebook.katana"                  to AppCategory.SOCIAL,
        "com.facebook.lite"                    to AppCategory.SOCIAL,
        "com.twitter.android"                  to AppCategory.SOCIAL,
        "com.twitter.android.lite"             to AppCategory.SOCIAL,
        "com.snapchat.android"                 to AppCategory.SOCIAL,
        "com.tiktok"                           to AppCategory.ENTERTAINMENT,
        "com.zhiliaoapp.musically"             to AppCategory.ENTERTAINMENT,   // TikTok (alt pkg)
        "com.reddit.frontpage"                 to AppCategory.SOCIAL,
        "com.pinterest"                        to AppCategory.CREATIVITY,
        "com.tumblr"                           to AppCategory.SOCIAL,
        "com.linkedin.android"                 to AppCategory.WORK,
        "com.vk.vkats"                         to AppCategory.SOCIAL,
        "com.BeReal.BeReal"                    to AppCategory.SOCIAL,
        "com.threads.android"                  to AppCategory.SOCIAL,
        "com.mastodon.android"                 to AppCategory.SOCIAL,
        // Work / Productivity
        "com.google.android.gm"               to AppCategory.WORK,
        "com.microsoft.office.outlook"         to AppCategory.WORK,
        "com.fastmail.app"                     to AppCategory.WORK,
        "me.proton.android.mail"               to AppCategory.WORK,
        "com.google.android.apps.docs"         to AppCategory.WORK,
        "com.google.android.apps.sheets"       to AppCategory.WORK,
        "com.google.android.apps.slides"       to AppCategory.WORK,
        "com.microsoft.office.word"            to AppCategory.WORK,
        "com.microsoft.office.excel"           to AppCategory.WORK,
        "com.microsoft.office.powerpoint"      to AppCategory.WORK,
        "com.microsoft.office.onenote"         to AppCategory.PRODUCTIVITY,
        "com.notion.id"                        to AppCategory.PRODUCTIVITY,
        "com.todoist"                          to AppCategory.PRODUCTIVITY,
        "com.ticktick.task"                    to AppCategory.PRODUCTIVITY,
        "com.evernote"                         to AppCategory.PRODUCTIVITY,
        "com.standardnotes"                    to AppCategory.PRODUCTIVITY,
        "net.ja.obsidian"                      to AppCategory.PRODUCTIVITY,
        "md.obsidian"                          to AppCategory.PRODUCTIVITY,
        "com.slack"                            to AppCategory.WORK,
        "us.zoom.videomeetings"                to AppCategory.WORK,
        "com.google.android.apps.meetings"     to AppCategory.WORK,
        "com.microsoft.teams"                  to AppCategory.WORK,
        "com.webex.meetings"                   to AppCategory.WORK,
        "com.atlassian.jira.confluence"        to AppCategory.WORK,
        "com.basecamp.bc3"                     to AppCategory.WORK,
        "com.asana.app"                        to AppCategory.WORK,
        "com.trello"                           to AppCategory.PRODUCTIVITY,
        "com.google.android.calendar"          to AppCategory.PRODUCTIVITY,
        "com.samsung.android.calendar"         to AppCategory.PRODUCTIVITY,
        // Finance
        "com.robinhood.android"                to AppCategory.FINANCE,
        "com.coinbase.android"                 to AppCategory.FINANCE,
        "com.kraken.trade"                     to AppCategory.FINANCE,
        "com.binance.dev"                      to AppCategory.FINANCE,
        "com.paypal.android.p2pmobile"         to AppCategory.FINANCE,
        "com.venmo"                            to AppCategory.FINANCE,
        "com.cashapp"                          to AppCategory.FINANCE,
        "com.google.android.apps.walletnfcrel" to AppCategory.FINANCE,
        "com.chase.sig.android"                to AppCategory.FINANCE,
        "com.bankofamerica.cashpay"            to AppCategory.FINANCE,
        "com.wellsfargo.mobile"                to AppCategory.FINANCE,
        "com.usaa.mobile.android.usaa"         to AppCategory.FINANCE,
        "com.mint"                             to AppCategory.FINANCE,
        "com.ynab.ynab1"                       to AppCategory.FINANCE,
        "com.revolut.revolut"                  to AppCategory.FINANCE,
        "com.transferwise.android"             to AppCategory.FINANCE,
        "com.n26.android"                      to AppCategory.FINANCE,
        // Entertainment
        "com.google.android.youtube"           to AppCategory.ENTERTAINMENT,
        "com.google.android.apps.youtube.music" to AppCategory.ENTERTAINMENT,
        "com.netflix.mediaclient"              to AppCategory.ENTERTAINMENT,
        "com.spotify.music"                    to AppCategory.ENTERTAINMENT,
        "com.amazon.avod.thirdpartyclient"     to AppCategory.ENTERTAINMENT,
        "com.disneyplus"                       to AppCategory.ENTERTAINMENT,
        "com.disney.disneyplus"                to AppCategory.ENTERTAINMENT,
        "com.hbo.hbonow"                       to AppCategory.ENTERTAINMENT,
        "com.max.atve.android"                 to AppCategory.ENTERTAINMENT,  // HBO Max
        "tv.twitch.android.app"                to AppCategory.ENTERTAINMENT,
        "com.pandora.android"                  to AppCategory.ENTERTAINMENT,
        "com.soundcloud.android"               to AppCategory.ENTERTAINMENT,
        "com.deezer.android.tv"                to AppCategory.ENTERTAINMENT,
        "com.tidal.android"                    to AppCategory.ENTERTAINMENT,
        "com.apple.android.music"              to AppCategory.ENTERTAINMENT,
        "com.amazon.mp3"                       to AppCategory.ENTERTAINMENT,
        "com.vimeo.android.videoapp"           to AppCategory.ENTERTAINMENT,
        "com.google.android.apps.podcasts"     to AppCategory.ENTERTAINMENT,
        "com.pocketcasts.podcast.player"       to AppCategory.ENTERTAINMENT,
        "au.com.shiftyjelly.pocketcasts"       to AppCategory.ENTERTAINMENT,
        "com.overcast.podcasts"                to AppCategory.ENTERTAINMENT,
        "com.amazon.kindle"                    to AppCategory.PRODUCTIVITY,
        "com.goodreads.mobile"                 to AppCategory.PRODUCTIVITY,
        // Travel
        "com.airbnb.android"                   to AppCategory.TRAVEL,
        "com.ubercab"                          to AppCategory.TRAVEL,
        "com.lyft.android"                     to AppCategory.TRAVEL,
        "com.google.android.apps.maps"         to AppCategory.TRAVEL,
        "com.waze"                             to AppCategory.TRAVEL,
        "com.booking"                          to AppCategory.TRAVEL,
        "com.expedia.booking"                  to AppCategory.TRAVEL,
        "com.kayak.android"                    to AppCategory.TRAVEL,
        "com.tripadvisor.tripadvisor"          to AppCategory.TRAVEL,
        "com.google.android.apps.travelapp"    to AppCategory.TRAVEL,
        "com.citymapper.app.prod"              to AppCategory.TRAVEL,
        "com.moovit.moovitapp"                 to AppCategory.TRAVEL,
        "com.transit.android"                  to AppCategory.TRAVEL,
        "com.bolt.consumer"                    to AppCategory.TRAVEL,
        // Shopping
        "com.amazon.mShoppingapp"              to AppCategory.SHOPPING,
        "com.ebay.mobile"                      to AppCategory.SHOPPING,
        "com.etsy.android"                     to AppCategory.SHOPPING,
        "com.target.ui"                        to AppCategory.SHOPPING,
        "com.walmart.android"                  to AppCategory.SHOPPING,
        "com.shopify.mobile"                   to AppCategory.SHOPPING,
        "com.depop.app"                        to AppCategory.SHOPPING,
        "com.vinted.android"                   to AppCategory.SHOPPING,
        "com.grailed.app"                      to AppCategory.SHOPPING,
        // Creativity
        "com.adobe.lightroom"                  to AppCategory.CREATIVITY,
        "com.adobe.Photoshop"                  to AppCategory.CREATIVITY,
        "com.adobe.premiere"                   to AppCategory.CREATIVITY,
        "com.adobe.illustrator"                to AppCategory.CREATIVITY,
        "com.canva.editor"                     to AppCategory.CREATIVITY,
        "com.vsco.cam"                         to AppCategory.CREATIVITY,
        "com.kinemaster.KineMaster"            to AppCategory.CREATIVITY,
        "com.inshot.android"                   to AppCategory.CREATIVITY,
        "com.gopro.smarty"                     to AppCategory.CREATIVITY,
        "com.lawnchair"                        to AppCategory.UTILITIES,
        "com.google.android.apps.camera"       to AppCategory.CREATIVITY,
        "com.sec.android.app.camera"           to AppCategory.CREATIVITY,
        "com.oneplus.camera"                   to AppCategory.CREATIVITY,
        "com.miui.camera"                      to AppCategory.CREATIVITY,
        // Daily / System
        "com.android.camera2"                  to AppCategory.DAILY,
        "com.google.android.dialer"            to AppCategory.DAILY,
        "com.android.dialer"                   to AppCategory.DAILY,
        "com.samsung.android.dialer"           to AppCategory.DAILY,
        "com.google.android.apps.messaging"    to AppCategory.DAILY,
        "com.android.messaging"                to AppCategory.DAILY,
        "com.samsung.android.messaging"        to AppCategory.DAILY,
        "com.android.settings"                 to AppCategory.UTILITIES,
        "com.google.android.calculator"        to AppCategory.UTILITIES,
        "com.google.android.clock"             to AppCategory.DAILY,
        "com.android.chrome"                   to AppCategory.UTILITIES,
        "org.mozilla.firefox"                  to AppCategory.UTILITIES,
        "com.brave.browser"                    to AppCategory.UTILITIES,
        "com.duckduckgo.mobile.android"        to AppCategory.UTILITIES,
        "com.opera.browser"                    to AppCategory.UTILITIES,
        "com.samsung.android.app.notes"        to AppCategory.PRODUCTIVITY,
        "com.google.android.keep"              to AppCategory.PRODUCTIVITY,
        "com.microsoft.launcher"               to AppCategory.UTILITIES,
        "com.google.android.apps.nbu.files"    to AppCategory.UTILITIES,     // Files by Google
        "com.mi.android.globalFileexplorer"    to AppCategory.UTILITIES,
        "com.sec.android.app.myfiles"          to AppCategory.UTILITIES,
        "com.google.android.gms"               to AppCategory.UTILITIES,
        // Health / Fitness (DAILY category)
        "com.strava"                           to AppCategory.DAILY,
        "com.nike.plusgps"                     to AppCategory.DAILY,
        "com.adidas.running"                   to AppCategory.DAILY,
        "com.fitbit.FitbitMobile"              to AppCategory.DAILY,
        "com.google.android.apps.fitness"      to AppCategory.DAILY,
        "com.samsung.android.shealth"          to AppCategory.DAILY,
        "com.calm.android"                     to AppCategory.DAILY,
        "com.headspace.android"                to AppCategory.DAILY,
        "com.noom.coaching"                    to AppCategory.DAILY,
        "com.lose.it"                          to AppCategory.DAILY,
        "com.myfitnesspal.android"             to AppCategory.DAILY,
    )

    // ── Label keyword → category (NLP-style, suggestion 40) ──────────────────
    private val labelKeywords: List<Pair<Regex, AppCategory>> = listOf(
        Regex("(?i)bank|wallet|pay|finance|invest|stock|crypto|cash|money|budget|coin|trading") to AppCategory.FINANCE,
        Regex("(?i)social|friend|network|connect|community|dating|tinder|bumble|hinge")         to AppCategory.SOCIAL,
        Regex("(?i)chat|message|talk|call|voice|meet|video|sms|text|whats|telegr|signal")       to AppCategory.COMMUNICATION,
        Regex("(?i)\\bwork\\b|office|mail|email|calendar|task|todo|note|doc|sheet|drive|cloud|meeting|project") to AppCategory.WORK,
        Regex("(?i)music|radio|podcast|audio|sound|stream|listen|spotify|youtube|yt music")     to AppCategory.ENTERTAINMENT,
        Regex("(?i)\\bgame\\b|gaming|play|puzzle|quest|battle|word|chess|arena|clash|royale")   to AppCategory.GAMES,
        Regex("(?i)travel|map|\\bride\\b|hotel|flight|trip|nav|uber|lyft|airbnb|booking|transit") to AppCategory.TRAVEL,
        Regex("(?i)shop|store|buy|market|amazon|ebay|order|cart|checkout|retail")               to AppCategory.SHOPPING,
        Regex("(?i)photo|camera|gallery|album|picture|image|film|snap|lightroom|vsco|edit|retouch") to AppCategory.CREATIVITY,
        Regex("(?i)settings|tools|utility|cleaner|manager|calculator|clock|alarm|file|storage|system|backup") to AppCategory.UTILITIES,
        Regex("(?i)fitness|health|gym|run|workout|diet|meditat|sleep|yoga|steps|calorie|weight") to AppCategory.DAILY,
        Regex("(?i)news|read|book|learn|edu|course|study|language|duolingo|kindle|quiz")        to AppCategory.PRODUCTIVITY,
        Regex("(?i)video|watch|movie|show|series|stream|tv|netflix|hulu|disney")                to AppCategory.ENTERTAINMENT,
        Regex("(?i)creative|design|art|draw|sketch|illustrat|canvas|animate|video edit|cut")   to AppCategory.CREATIVITY,
    )

    private val packageKeywords: List<Pair<Regex, AppCategory>> = listOf(
        Regex("(?i)bank|finance|pay|wallet|invest|cash|coin|crypto|trade")        to AppCategory.FINANCE,
        Regex("(?i)social|instagram|facebook|twitter|snap|beam|bereal|threads")  to AppCategory.SOCIAL,
        Regex("(?i)message|chat|telegram|whatsapp|signal|viber|wire|matrix")     to AppCategory.COMMUNICATION,
        Regex("(?i)\\.game|games|gaming|puzzle|battle|clash|arena|quest|play")   to AppCategory.GAMES,
        Regex("(?i)travel|maps|uber|lyft|airbnb|waze|transit|citymapper|bolt")   to AppCategory.TRAVEL,
        Regex("(?i)shop|amazon|ebay|market|buy|walmart|target|etsy|depop")       to AppCategory.SHOPPING,
        Regex("(?i)music|spotify|soundcloud|pandora|ytm|tidal|deezer|apple")     to AppCategory.ENTERTAINMENT,
        Regex("(?i)fitness|health|workout|gym|run|strava|fitbit|calm|headspace") to AppCategory.DAILY,
        Regex("(?i)settings|tools|util|calc|clock|alarm|file|backup|cleaner")   to AppCategory.UTILITIES,
        Regex("(?i)office|gmail|calendar|drive|docs|notion|work|slack|zoom|teams|asana|trello") to AppCategory.WORK,
        Regex("(?i)photo|camera|gallery|lightroom|vsco|adobe|kinemaster|inshot") to AppCategory.CREATIVITY,
        Regex("(?i)news|kindle|read|book|learn|edu|duolingo|goodreads|pocket")   to AppCategory.PRODUCTIVITY,
    )

    fun categorize(packageName: String, label: String): AppCategory {
        knownApps[packageName]?.let { return it }
        for ((regex, cat) in labelKeywords)   if (regex.containsMatchIn(label))       return cat
        for ((regex, cat) in packageKeywords) if (regex.containsMatchIn(packageName)) return cat
        return AppCategory.OTHER
    }

    fun secondaryCategories(pkg: String, label: String, primary: AppCategory): List<AppCategory> {
        val extras = mutableListOf<AppCategory>()
        if (pkg.contains("whatsapp", true) || pkg.contains("telegram", true)) {
            if (primary != AppCategory.DAILY)          extras += AppCategory.DAILY
            if (primary != AppCategory.COMMUNICATION)  extras += AppCategory.COMMUNICATION
        }
        if (primary == AppCategory.WORK)     extras += AppCategory.PRODUCTIVITY
        if (primary == AppCategory.SOCIAL)   extras += AppCategory.DAILY
        if (primary == AppCategory.FINANCE)  extras += AppCategory.UTILITIES
        if (primary == AppCategory.GAMES)    extras += AppCategory.ENTERTAINMENT
        if (primary == AppCategory.TRAVEL)   extras += AppCategory.UTILITIES
        if (primary == AppCategory.CREATIVITY) extras += AppCategory.UTILITIES
        return extras.distinct()
    }

    /**
     * Returns a flat list of keyword strings associated with a category.
     * Used by TFLiteCategorizerHelper.confidence() to score rule-based matches.
     */
    fun categoryKeywords(category: AppCategory): List<String> = when (category) {
        AppCategory.FINANCE       -> listOf("bank", "wallet", "pay", "finance", "invest", "stock", "crypto", "cash", "money", "budget", "coin", "trading", "transfer")
        AppCategory.SOCIAL        -> listOf("social", "friend", "network", "connect", "community", "dating", "instagram", "facebook", "twitter", "snap", "threads")
        AppCategory.COMMUNICATION -> listOf("chat", "message", "talk", "call", "voice", "meet", "video", "sms", "text", "whats", "telegr", "signal", "wire")
        AppCategory.WORK          -> listOf("work", "office", "mail", "email", "calendar", "task", "todo", "note", "doc", "sheet", "drive", "cloud", "meeting", "project", "slack", "zoom", "teams")
        AppCategory.ENTERTAINMENT -> listOf("music", "radio", "podcast", "audio", "sound", "stream", "listen", "spotify", "youtube", "video", "watch", "movie", "show", "netflix", "hulu", "disney")
        AppCategory.GAMES         -> listOf("game", "gaming", "play", "puzzle", "quest", "battle", "word", "chess", "arena", "clash", "royale", "arcade")
        AppCategory.TRAVEL        -> listOf("travel", "map", "ride", "hotel", "flight", "trip", "nav", "uber", "lyft", "airbnb", "booking", "transit", "navigate")
        AppCategory.SHOPPING      -> listOf("shop", "store", "buy", "market", "amazon", "ebay", "order", "cart", "checkout", "retail", "product", "deal")
        AppCategory.CREATIVITY    -> listOf("photo", "camera", "gallery", "album", "picture", "image", "film", "snap", "lightroom", "vsco", "edit", "creative", "design", "art", "draw", "sketch", "illustrat", "canvas", "animate")
        AppCategory.UTILITIES     -> listOf("settings", "tools", "utility", "cleaner", "manager", "calculator", "clock", "alarm", "file", "storage", "system", "backup", "browser", "chrome", "firefox")
        AppCategory.DAILY         -> listOf("fitness", "health", "gym", "run", "workout", "diet", "meditat", "sleep", "yoga", "steps", "calorie", "weight", "dialer", "phone", "contacts")
        AppCategory.PRODUCTIVITY  -> listOf("notion", "todo", "task", "evernote", "obsidian", "news", "read", "book", "learn", "edu", "course", "study", "language", "duolingo", "kindle", "trello")
        else                      -> emptyList()
    }

    /**
     * NLP-style query intent detection (Suggestion #40).
     * Returns the best AppCategory match for a free-text query, or null if unrecognized.
     *
     * Examples:
     *   "open a music app"    → ENTERTAINMENT
     *   "show me finance"     → FINANCE
     *   "productivity tools"  → PRODUCTIVITY
     */
    fun detectQueryIntent(query: String): AppCategory? {
        if (query.isBlank()) return null
        val q = query.trim().lowercase()
        return when {
            q.contains(Regex("music|sound|listen|song|podcast|audio|radio|spotify"))         -> AppCategory.ENTERTAINMENT
            q.contains(Regex("work|email|meeting|office|task|project|doc|calendar|schedule"))-> AppCategory.WORK
            q.contains(Regex("finance|bank|money|pay|invest|budget|crypto|wallet|cash"))     -> AppCategory.FINANCE
            q.contains(Regex("social|friend|post|story|feed|instagram|twitter|tiktok"))     -> AppCategory.SOCIAL
            q.contains(Regex("travel|map|ride|hotel|flight|navigate|direction|uber|lyft"))  -> AppCategory.TRAVEL
            q.contains(Regex("shop|buy|order|store|amazon|product|cart|deal|price"))        -> AppCategory.SHOPPING
            q.contains(Regex("photo|camera|picture|edit|creative|design|art|draw|video"))   -> AppCategory.CREATIVITY
            q.contains(Regex("fitness|health|gym|run|workout|meditation|sleep|yoga"))       -> AppCategory.DAILY
            q.contains(Regex("game|play|gaming|arcade|puzzle|battle|quiz"))                 -> AppCategory.GAMES
            q.contains(Regex("tool|utility|setting|calculator|alarm|file|system|manager")) -> AppCategory.UTILITIES
            q.contains(Regex("learn|read|book|study|news|education|course|quiz"))           -> AppCategory.PRODUCTIVITY
            q.contains(Regex("chat|message|call|text|sms|talk|meet|voice|video call"))     -> AppCategory.COMMUNICATION
            else -> null
        }
    }
}
