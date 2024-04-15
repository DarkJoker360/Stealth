package com.esposito.stealth

object UrlUtils {

    // TODO: Improve and add more search engines
    fun sanitize(url: String): String {
        return if(url.startsWith("https://") || url.startsWith("http://"))
            url
        else
            "https://www.google.com/search?q=$url"
    }
}