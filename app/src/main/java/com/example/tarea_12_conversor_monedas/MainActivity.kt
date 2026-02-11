package com.example.tarea_12_conversor_monedas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tarea_12_conversor_monedas.Configuration.Transactions
import com.example.tarea_12_conversor_monedas.Models.Conversion
import com.example.tarea_12_conversor_monedas.ui.theme.Tarea_12_Conversor_monedasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tarea_12_Conversor_monedasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaPrincipal()
                }
            }
        }
    }
}

@Composable
fun PantallaPrincipal() {
    val context = LocalContext.current
    val db = remember { Transactions(context) }

    // Estados para la UI
    var montoInput by remember { mutableStateOf("") }
    var resultadoTexto by remember { mutableStateOf("0.00") }
    // Estado para la lista (se actualiza al convertir)
    var historial by remember { mutableStateOf(db.getAllConversions()) }

    var monedaOrigen by remember { mutableStateOf("HNL") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Seleccione Moneda de Origen:", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val listaMonedas = listOf("HNL", "CRC", "NIC", "GTQ")
            listaMonedas.forEach { moneda ->
                FilterChip(
                    selected = monedaOrigen == moneda,
                    onClick = { monedaOrigen = moneda },
                    label = { Text(moneda) }
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val monto = montoInput.toDoubleOrNull() ?: 0.0
                if (monto > 0) {
                    val tasa = db.getRate(monedaOrigen)

                    val factor = if (tasa > 0) tasa else when(monedaOrigen) {
                        "HNL" -> 0.038
                        "CRC" -> 0.0019
                        "NIC" -> 0.027
                        "GTQ" -> 0.13
                        else -> 0.040
                    }

                    val calculo = monto * factor
                    resultadoTexto = String.format("%.2f", calculo)

                    // Guardamos la conversión real en la DB
                    db.insertConversion(monedaOrigen, "USD", monto, calculo)
                    historial = db.getAllConversions()
                }
            }
        ) {
            Text("Convertir a USD")
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Resultado Estimado:")
                Text("$ $resultadoTexto USD", style = MaterialTheme.typography.displaySmall)
            }
        }

        HorizontalDivider()

        Text(
            text = "Historial Reciente",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        LazyColumn {
            items(historial.reversed()) { registro ->
                ItemHistorial(registro) {
                    // Acción al tocar la estrella
                    db.toggleFavorite(registro.id, registro.isFavorite)
                    // Actualizamos la lista para que cambie el color de la estrella
                    historial = db.getAllConversions()
                }
            }
        }
        }
    }

@Composable
fun ItemHistorial(conversion: Conversion, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${conversion.amount} ${conversion.fromCode} -> ${conversion.result} USD", style = MaterialTheme.typography.bodyLarge)
                Text(text = conversion.date, style = MaterialTheme.typography.labelSmall)
            }

            // Botón de Favorito (Estrella)
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (conversion.isFavorite == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (conversion.isFavorite == 1) Color.Red else Color.Gray
                )
            }
        }
    }
}