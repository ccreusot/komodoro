
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fr.cedriccreusot.viewmodel.PomodoroViewModel
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text


object Colors {
    const val RED = 0xFFE57373
    const val GREEN = 0xFF81C784
    const val BLUE = 0xFF64B5F6
}

@Composable
fun App() {
    val themeDefinition = JewelTheme.darkThemeDefinition()

    val viewModel = remember { PomodoroViewModel() }

    IntUiTheme(
        theme = themeDefinition,
        styling = ComponentStyling.decoratedWindow(),
        swingCompatMode = true,
    ) {
        val state = viewModel.state.collectAsState()



        Box(modifier = Modifier.fillMaxSize().background(Color(Colors.RED))) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)) {
                Text(
                    "${state.value.duration.inWholeSeconds}", style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 92.sp,
                        fontWeight = FontWeight.Bold),
                    )
                Spacer(modifier = Modifier.height(32.dp))
                PomodoroControl(
                    onStop = { },
                    onStart = { viewModel.start() },
                    onNext = { viewModel.next() }
                )
            }
            LazyRow(
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp).fillMaxWidth()
            ) {
                items(4) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier.size(width = 92.dp, 8.dp)
                            .background(color = Color.White.copy(alpha = 0.75f))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

@Composable
fun PomodoroControl(onStop : () -> Unit, onStart : () -> Unit, onNext : () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onStop) {
            Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(100.dp)
            )
        }

        IconButton(onClick = onStart) {
            Icon(
                imageVector = Icons.Rounded.PlayCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(128.dp)
            )
        }

        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(100.dp)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    enableNewSwingCompositing()

    val trayState = remember { TrayState() }

    Tray(
        icon = painterResource("icons/timer.svg"),
        state = trayState,
        menu = {
            Item("Exit", onClick = ::exitApplication)
        },
    )

//    trayState.sendNotification(Notification(title = "Komodoro", message = "Started", type = Notification.Type.Info))

    Window(
        onCloseRequest = ::exitApplication,
        title = "Komodoro"
    ) {
        App()
    }
}


@Composable
@Preview
private fun AppPreview() {
    App()
}
