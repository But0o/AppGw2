package com.example.gw2.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Barra de búsqueda personalizada estilo “fondo gris claro con esquinas redondeadas”
 *
 * @param query         Texto actual de búsqueda.
 * @param onQueryChange Callback que se dispara cada vez que cambia el texto.
 * @param onSearch      Callback que se dispara cuando el usuario presiona la lupa.
 * @param placeholder   Texto de placeholder (por defecto: "Buscar").
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    placeholder: String = "Buscar"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(fiftySixDp) // Alto 56dp (igual que un TextField estándar)
            .background(
                color = Color(0xFFF2F2F2), // Gris muy claro de fondo
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1) Ícono de lupa clickeable
        IconButton(
            onClick = { onSearch() },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // 2) Campo de texto transparente encima del fondo gris
        TextField(
            value = query,
            onValueChange = { onQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // Altura interna de la caja de texto
            placeholder = {
                androidx.compose.material3.Text(
                    text = placeholder,
                    color = Color.Gray
                )
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
        )
    }
}
// Definimos la constante para 56.dp
private val fiftySixDp = 70.dp
