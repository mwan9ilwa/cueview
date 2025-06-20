package com.example.cueview

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform