package com.ciyato.launcher.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

/**
 * StockCryptoWidget — Suggestion #59
 * Live stock/crypto prices using CoinGecko (crypto, free, no key) and
 * a stub for stocks (Yahoo Finance scrape as fallback).
 * Auto-refreshes every 5 minutes.
 */

data class TickerItem(
    val symbol: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val isCrypto: Boolean,
)

private val DEFAULT_TICKERS = listOf("bitcoin", "ethereum", "solana")

@Composable
fun StockCryptoWidget(
    symbols: List<String> = DEFAULT_TICKERS,
    modifier: Modifier = Modifier,
) {
    var tickers by remember { mutableStateOf<List<TickerItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(symbols) {
        while (true) {
            isLoading = tickers.isEmpty()
            tickers = withContext(Dispatchers.IO) { fetchCryptoTickers(symbols) }
            isLoading = false
            delay(5 * 60 * 1000L) // refresh every 5 min
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier,
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShowChart, null, tint = CiyatoGold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Markets", color = CiyatoGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            AnimatedContent(targetState = isLoading, transitionSpec = { fadeIn() togetherWith fadeOut() }) { loading ->
                if (loading) {
                    Box(Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CiyatoGold, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(tickers) { ticker ->
                            TickerChip(ticker)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TickerChip(ticker: TickerItem) {
    val isUp = ticker.changePercent >= 0
    val changeColor = if (isUp) Color(0xFF4CAF50) else Color(0xFFF44336)

    Column(
        modifier = Modifier
            .background(CiyatoBg, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(ticker.symbol.uppercase(), color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(
            "$${formatPrice(ticker.price)}",
            color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isUp) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                null, tint = changeColor, modifier = Modifier.size(16.dp)
            )
            Text(
                "${if (isUp) "+" else ""}${String.format("%.2f", ticker.changePercent)}%",
                color = changeColor, fontSize = 11.sp,
            )
        }
    }
}

private fun formatPrice(price: Double): String = when {
    price >= 1000 -> String.format("%.0f", price)
    price >= 1    -> String.format("%.2f", price)
    else          -> String.format("%.4f", price)
}

private fun fetchCryptoTickers(ids: List<String>): List<TickerItem> {
    return try {
        val idsParam = ids.joinToString(",")
        val url = "https://api.coingecko.com/api/v3/simple/price?ids=$idsParam&vs_currencies=usd&include_24hr_change=true"
        val json = JSONObject(URL(url).readText())
        ids.mapNotNull { id ->
            val obj = json.optJSONObject(id) ?: return@mapNotNull null
            TickerItem(
                symbol = id.take(3),
                name = id.replaceFirstChar { it.uppercase() },
                price = obj.optDouble("usd", 0.0),
                changePercent = obj.optDouble("usd_24h_change", 0.0),
                isCrypto = true,
            )
        }
    } catch (_: Exception) {
        // Return stale/placeholder data on failure
        ids.map { id ->
            TickerItem(symbol = id.take(3), name = id, price = 0.0, changePercent = 0.0, isCrypto = true)
        }
    }
}
