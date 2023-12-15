package com.example.practica401

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CuadernoApp()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuadernoApp() {
    var alumnos by remember { mutableStateOf(0) }
    var notas by remember { mutableStateOf(FloatArray(alumnos)) }
    var media by remember { mutableStateOf(0.0) }

    var notaValue by remember { mutableStateOf(TextFieldValue()) }
    var indiceValue by remember { mutableStateOf(TextFieldValue()) }

    var showErrorMessage by remember { mutableStateOf(false) }

    fun calcularMediaExcluyendo(notas: FloatArray) {
        if (notas.isNotEmpty()) {
            var notaMasAlta = notas[0]
            var notaMasBaja = notas[0]

            // Encontrar la nota más alta y la nota más baja
            for (i in notas.indices) {
                if (notas[i] > notaMasAlta) {
                    notaMasAlta = notas[i]
                } else if (notas[i] < notaMasBaja) {
                    notaMasBaja = notas[i]
                }
            }

            // Calcular la media excluyendo la nota más alta y la más baja
            val notasFiltradas = notas.filter { it != notaMasAlta && it != notaMasBaja }
            val nuevaMedia = if (notasFiltradas.isNotEmpty()) {
                val suma = notasFiltradas.sum()
                val cantidad = notasFiltradas.size
                media = (suma / cantidad).toDouble()
            } else {
                // Si no hay notas, la media es 0
                media = 0.0
            }
        } else {
            // Si no hay notas, la media es 0
            media = 0.0
        }
    }

    fun borrarTodas() {
        alumnos = 0
        notas = FloatArray(0)
        media = 0.0
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = notaValue,
            onValueChange = {
                notaValue = it
            },
            label = { Text("Nota") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    try {
                        val nota = notaValue.text.toFloat()
                        if (validarNota(nota)) {
                            alumnos++
                            notas = FloatArray(alumnos) { if (it < alumnos - 1) notas[it] else nota }
                            calcularMediaExcluyendo(notas)
                            notaValue = TextFieldValue()
                            showErrorMessage = false
                        } else {
                            showErrorMessage = true
                        }
                    } catch (e: NumberFormatException) {
                        showErrorMessage = true
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Done, contentDescription = null)
                Text("Añadir Nota")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (alumnos > 0) {
                        notas = notas.copyOfRange(0, alumnos - 1)
                        alumnos--
                        calcularMediaExcluyendo(notas)
                        showErrorMessage = false
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                Text("Eliminar última nota")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar las notas
        Text("Notas: ${notas.joinToString(", ")}")

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la media
        Text("Media: %.2f".format(media))

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    borrarTodas()
                    showErrorMessage = false
                }
            ) {
                Text("Borrar Todas las Notas")
            }

            Button(
                onClick = {
                    notas = FloatArray(0)
                    calcularMediaExcluyendo(notas)
                }
            ) {
                Text("Borrar Todas las Notas y Recalcular Media")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = indiceValue,
            onValueChange = {
                indiceValue = it
            },
            label = { Text("Índice de nota a borrar") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    val indice = indiceValue.text.toInt()
                    if (indice >= 0 && indice < alumnos) {
                        notas = borrarNota(indice, notas)
                        calcularMediaExcluyendo(notas)
                        showErrorMessage = false
                    } else {
                        showErrorMessage = true
                    }
                } catch (e: NumberFormatException) {
                    showErrorMessage = true
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            Text("Borrar Nota")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showErrorMessage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Por favor, introduce una nota válida (entre 0 y 10).", color = Color.Red)
            }
        }
    }
}


fun borrarNota(indice: Int, notas: FloatArray): FloatArray {
    if (indice >= 0 && indice < notas.size) {
        return notas.filterIndexed { index, _ -> index != indice }.toFloatArray()
    }
    return notas.copyOf()
}

fun validarNota(nota: Float): Boolean {
    return nota in 0.0f..10.0f
}




