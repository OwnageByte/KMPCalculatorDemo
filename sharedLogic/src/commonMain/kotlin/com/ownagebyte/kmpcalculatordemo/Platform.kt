package com.ownagebyte.kmpcalculatordemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform