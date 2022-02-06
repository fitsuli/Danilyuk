@file:Suppress("unused")

package ru.fitsuli.developerslifeviewer.utils

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.fitsuli.developerslifeviewer.R
import java.io.File

class Utils {
    companion object {
        object Pages {
            const val Latest = 0
            const val Random = 1
            const val Top = 2
            const val Hot = 3
        }
    }
}

suspend fun Context.downloadJsonStr(url: String): String? = withContext(Dispatchers.IO) {
    val client = OkHttpClient.Builder()
        .cache(
            Cache(
                directory = File(cacheDir, "http_cache"),
                maxSize = 50L * 1024L * 1024L
            )
        )
        .build()
    val request = Request.Builder().url(url).build()

    return@withContext runCatching {
        val response = client.newCall(request).execute()
        response.body?.string()
    }.getOrNull()
}

fun Context.shareLink(link: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            link
        )
        type = "text/plain"
    }
    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_to)))
}