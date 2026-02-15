package com.example.tarea_12_conversor_monedas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tarea_12_conversor_monedas.screens.* // Importas todas tus pantallas
import com.example.tarea_12_conversor_monedas.ui.theme.Tarea_12_Conversor_monedasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tarea_12_Conversor_monedasTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "conversor"
                ) {
                    composable("conversor") {
                        PantallaConversor(navController)
                    }

                    composable("historial") {
                        PantallaHistorial(navController)
                    }
                    composable("configuracion")
                    { PantallaConfiguracion(navController) }

                    composable("resultado/{monto}/{moneda}/{calculo}") { backStackEntry ->
                        val monto = backStackEntry.arguments?.getString("monto") ?: "0"
                        val moneda = backStackEntry.arguments?.getString("moneda") ?: ""
                        val calculo = backStackEntry.arguments?.getString("calculo") ?: "0.00"

                        PantallaResultado(navController, monto, moneda, calculo)
                    }
                }
            }
        }
    }
}