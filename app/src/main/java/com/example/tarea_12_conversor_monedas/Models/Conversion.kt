package com.example.tarea_12_conversor_monedas.Models

data class Conversion(
    val id: Int = 0,
    val fromCode: String,
    val toCode: String,
    val amount: Double,
    val result: Double,
    val date: String,
    val isFavorite: Int = 0
)