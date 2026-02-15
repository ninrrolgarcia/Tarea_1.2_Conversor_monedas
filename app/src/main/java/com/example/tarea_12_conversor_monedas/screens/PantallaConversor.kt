package com.example.tarea_12_conversor_monedas.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tarea_12_conversor_monedas.Configuration.Transactions

@Composable
fun PantallaConversor(navController: NavController) {
    val context = LocalContext.current
    val db = remember { Transactions(context) }

    var tasas by remember { mutableStateOf(db.getAllRates()) }
    val listaMonedas = if (tasas.isEmpty()) {
        listOf("HNL", "CRC", "NIC", "GTQ", "BZD")
    } else {
        tasas.map { it.fromCode }
    }
    var montoInput by remember { mutableStateOf("") }
    var monedaOrigen by remember { mutableStateOf("HNL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Conversor de Moneda", style = MaterialTheme.typography.headlineMedium)

        Text("Seleccione Moneda de Origen:", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            listaMonedas.forEach { moneda ->
                FilterChip(
                    selected = (monedaOrigen == moneda),
                    onClick = { monedaOrigen = moneda },
                    label = { Text(moneda) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        OutlinedTextField(
            value = montoInput,
            onValueChange = { montoInput = it },
            label = { Text("Monto en $monedaOrigen") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val monto = montoInput.toDoubleOrNull() ?: 0.0
                if (monto > 0) {
                    val tasa = db.getRate(monedaOrigen)
                    val factor = if (tasa > 0) tasa else when(monedaOrigen) {
                        "HNL" -> 0.038
                        "CRC" -> 0.0021
                        "NIC" -> 0.027
                        "GTQ" -> 0.13
                        "BZD" -> 0.50
                        else -> 1.0
                    }
                    val calculo = String.format("%.2f", monto * factor)

                    db.insertConversion(monedaOrigen, "USD", monto, monto * factor)

                    navController.navigate("resultado/$monto/$monedaOrigen/$calculo")
                }
            }
        ) {
            Text("Realizar Conversión")
        }

        OutlinedButton(
            onClick = { navController.navigate("configuracion") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Configurar Tasa", style = MaterialTheme.typography.labelSmall)
        }

        OutlinedButton(
            onClick = { navController.navigate("historial") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Historial Completo")
        }
    }
}