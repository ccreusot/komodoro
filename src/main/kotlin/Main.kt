import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Text


object Colors {
    const val RED = 0xFFE57373
    const val GREEN = 0xFF81C784
    const val BLUE = 0xFF64B5F6
}

@Composable
@Preview
fun App() {
    val themeDefinition = JewelTheme.darkThemeDefinition()

    IntUiTheme(
        theme = themeDefinition,
        styling = ComponentStyling.decoratedWindow(),
        swingCompatMode = true,
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(Colors.RED))) {
            Text(
                "XX:XX:XX", style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                ), modifier = Modifier.align(Alignment.Center)
            )

            LazyRow(
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp).fillMaxWidth()
            ) {
                items(4) {
                    Box(modifier = Modifier.size(width = 92.dp, 8.dp).background(color = Color.White))
                }
            }
        }
    }
}

fun main() = application {
    enableNewSwingCompositing()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Komodoro"
    ) {
        App()
    }
}
