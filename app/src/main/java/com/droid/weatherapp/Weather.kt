package com.droid.weatherapp

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)