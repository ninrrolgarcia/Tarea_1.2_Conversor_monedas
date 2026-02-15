package com.example.tarea_12_conversor_monedas.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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

    // Estados para la interfaz
    var montoInput by remember { mutableStateOf("") }
    var monedaOrigen by remember { mutableStateOf("HNL") }
    val listaMonedas = listOf("HNL", "CRC", "NIC", "GTQ", "BZD")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Conversor de Moneda", style = MaterialTheme.typography.headlineMedium)

        // --- 1. SELECCIÓN DE MONEDA ---
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

        // --- 2. INGRESO DE MONTO ---
        OutlinedTextField(
            value = montoInput,
            onValueChange = { montoInput = it },
            label = { Text("Monto en $monedaOrigen") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. BOTÓN CONVERTIR (Y NAVEGAR) ---
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val monto = montoInput.toDoubleOrNull() ?: 0.0
                if (monto > 0) {
                    val tasa = db.getRate(monedaOrigen)
                    // Tasas de respaldo si la DB está vacía
                    val factor = if (tasa > 0) tasa else when(monedaOrigen) {
                        "HNL" -> 0.038
                        "CRC" -> 0.0021
                        "NIC" -> 0.027
                        "GTQ" -> 0.13
                        "BZD" -> 0.50
                        else -> 1.0
                    }
                    val calculo = String.format("%.2f", monto * factor)

                    // Guardamos en la base de datos
                    db.insertConversion(monedaOrigen, "USD", monto, monto * factor)

                    // NAVEGAMOS a la pantalla de resultado pasando los valores
                    navController.navigate("resultado/$monto/$monedaOrigen/$calculo")
                }
            }
        ) {
            Text("Realizar Conversión")
        }

        // --- 4. BOTÓN HISTORIAL ---
        OutlinedButton(
            onClick = { navController.navigate("historial") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Historial Completo")
        }
    }
}