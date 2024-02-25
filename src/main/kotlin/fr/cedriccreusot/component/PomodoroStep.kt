package fr.cedriccreusot.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PomodoroStep(
    modifier: Modifier = Modifier,
    currentStep: Int = 0
) {
    LazyRow(
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        items(4) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier.size(width = 92.dp, 8.dp)
                    .background(color = Color.White.copy(alpha = if (it == currentStep) 1.0f else 0.75f ))
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
