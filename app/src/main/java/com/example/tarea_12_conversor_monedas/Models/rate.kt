package com.example.tarea_12_conversor_monedas.Models

data class Rate(
    val id: Int = 0,
    val fromCode: String,
    val toCode: String,
    val rate: Double
)