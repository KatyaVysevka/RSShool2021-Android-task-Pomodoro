package com.example.pomodoro

data class Timer(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var startMs: Long,
    var forDifference: Long)
