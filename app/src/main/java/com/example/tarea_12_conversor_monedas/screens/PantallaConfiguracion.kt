package com.example.tarea_12_conversor_monedas.screens

import android.widget.Toast
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
fun PantallaConfiguracion(navController: NavController) {
    val context = LocalContext.current
    val db = remember { Transactions(context) }
    var nuevaMoneda by remember { mutableStateOf("") }
    var valorTasa by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Agregar Tasa Personalizada", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nuevaMoneda,
            onValueChange = { nuevaMoneda = it.uppercase() },
            label = { Text("Código Moneda (Ej: ABC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = valorTasa,
            onValueChange = { valorTasa = it },
            label = { Text("Valor frente al USD") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                val tasa = valorTasa.toDoubleOrNull() ?: 0.0
                if (nuevaMoneda.isNotEmpty() && tasa > 0) {
                    db.insertOrUpdateRate(nuevaMoneda, "USD", tasa)
                    Toast.makeText(context, "Tasa agregada!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Guardar Tasa")
        }
    }
}