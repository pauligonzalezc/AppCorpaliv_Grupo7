package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp


@Composable
fun OpcionCheckbox(
    texto: String,
    marcado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .toggleable(value = marcado, role = Role.Checkbox, onValueChange = onCambio)
            .padding(end = 8.dp)
    ) {
        Checkbox(checked = marcado, onCheckedChange = null, modifier = Modifier.padding(start = 12.dp))
        Spacer(Modifier.width(12.dp))
        Text(texto)
    }
}
