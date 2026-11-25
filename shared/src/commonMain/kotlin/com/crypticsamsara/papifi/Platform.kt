package com.crypticsamsara.papifi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform