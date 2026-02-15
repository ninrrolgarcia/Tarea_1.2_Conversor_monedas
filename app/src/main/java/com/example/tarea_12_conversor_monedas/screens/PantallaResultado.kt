package com.example.tarea_12_conversor_monedas.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaResultado(navController: NavController, monto: String, moneda: String, calculo: String) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Detalle de Conversión", style = MaterialTheme.typography.titleLarge)
                Text("Monto original: $monto $moneda")
                Text("Resultado: $calculo USD", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        Button(onClick = { navController.popBackStack() }) {
            Text("Volver a convertir")
        }
    }
}