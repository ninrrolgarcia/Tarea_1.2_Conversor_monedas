package com.example.tarea_12_conversor_monedas.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tarea_12_conversor_monedas.Configuration.Transactions
import com.example.tarea_12_conversor_monedas.Models.Conversion

@Composable
fun PantallaHistorial(navController: NavController) {
    val context = LocalContext.current
    val db = remember { Transactions(context) }

    // Usamos un estado para que la lista se refresque al marcar favoritos
    var historial by remember { mutableStateOf(db.getAllConversions()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Historial de Conversiones",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(historial.reversed()) { item ->
                // Llamamos al componente que definimos abajo
                ItemHistorial(
                    conversion = item,
                    onFavoriteClick = {
                        db.toggleFavorite(item.id, item.isFavorite)
                        historial = db.getAllConversions() // Refrescar lista
                    }
                )
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Regresar al Conversor")
        }
    }
}

// --- ESTE ES EL COMPONENTE QUE TE PEDÍA EL ERROR ---
@Composable
fun ItemHistorial(conversion: Conversion, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${conversion.amount} ${conversion.fromCode} -> ${conversion.result} USD",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = conversion.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            // Icono de Favorito
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