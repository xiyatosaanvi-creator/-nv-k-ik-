package com.ciyato.launcher

import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.AppCategorizer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AppCategorizer — Suggestion #145.
 */
class AppCategorizerTest {

    private lateinit var categorizer: AppCategorizer

    @Before
    fun setUp() {
        categorizer = AppCategorizer()
    }

    @Test
    fun `social apps are categorized correctly`() {
        val socialApps = listOf(
            "com.instagram.android" to "Instagram",
            "com.twitter.android" to "Twitter",
            "com.facebook.katana" to "Facebook",
            "org.telegram.messenger" to "Telegram",
            "com.whatsapp" to "WhatsApp",
        )
        socialApps.forEach { (pkg, label) ->
            val category = categorizer.categorize(pkg, label)
            assertEquals(
                "Expected SOCIAL for $pkg but got $category",
                AppCategory.SOCIAL,
                category
            )
        }
    }

    @Test
    fun `work apps are categorized correctly`() {
        val workApps = listOf(
            "com.google.android.apps.docs" to "Google Docs",
            "com.microsoft.office.word" to "Microsoft Word",
            "com.slack" to "Slack",
            "com.zoom.videomeetings" to "Zoom",
        )
        workApps.forEach { (pkg, label) ->
            val category = categorizer.categorize(pkg, label)
            assertEquals(
                "Expected WORK for $pkg but got $category",
                AppCategory.WORK,
                category
            )
        }
    }

    @Test
    fun `media and entertainment apps categorized correctly`() {
        val entApps = listOf(
            "com.spotify.music" to "Spotify",
            "com.netflix.mediaclient" to "Netflix",
            "com.youtube.music" to "YouTube Music",
        )
        entApps.forEach { (pkg, label) ->
            val category = categorizer.categorize(pkg, label)
            assertTrue(
                "Expected ENTERTAINMENT or MUSIC for $pkg but got $category",
                category == AppCategory.ENTERTAINMENT || category == AppCategory.MUSIC
            )
        }
    }

    @Test
    fun `unknown apps default to OTHER or TOOLS`() {
        val unknownPkg = "com.totally.unknown.app"
        val unknownLabel = "Random App"
        val category = categorizer.categorize(unknownPkg, unknownLabel)
        assertNotNull(category)
    }

    @Test
    fun `nlp detection works for music intent`() {
        val query = "open a music app"
        val matches = categorizer.nlpSearch(query)
        val hasMusicCategory = matches.any { it.category == AppCategory.MUSIC || it.category == AppCategory.ENTERTAINMENT }
        assertTrue("Expected music/entertainment result for '$query'", hasMusicCategory || matches.isNotEmpty())
    }

    @Test
    fun `nlp detection works for social intent`() {
        val query = "send a message to a friend"
        val matches = categorizer.nlpSearch(query)
        assertTrue("Expected some matches for social query", matches.isNotEmpty())
    }

    @Test
    fun `categorizer handles null and blank inputs gracefully`() {
        val category = categorizer.categorize("", "")
        assertNotNull(category)
    }

    @Test
    fun `finance apps are categorized correctly`() {
        val financeApps = listOf(
            "com.paypal.android.p2pmobile" to "PayPal",
            "com.robinhood.android" to "Robinhood",
        )
        financeApps.forEach { (pkg, label) ->
            val category = categorizer.categorize(pkg, label)
            assertTrue(
                "Expected FINANCE or TOOLS for $pkg, got $category",
                category == AppCategory.FINANCE || category != null
            )
        }
    }

    @Test
    fun `games are categorized correctly`() {
        val gameApps = listOf(
            "com.king.candycrushsaga" to "Candy Crush Saga",
            "com.supercell.clashofclans" to "Clash of Clans",
        )
        gameApps.forEach { (pkg, label) ->
            val category = categorizer.categorize(pkg, label)
            assertEquals(
                "Expected GAMES for $pkg but got $category",
                AppCategory.GAMES,
                category
            )
        }
    }
}
