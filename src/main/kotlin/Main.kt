
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.*
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
import fr.cedriccreusot.viewmodel.PomodoroState
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
import kotlin.time.Duration


object Colors {
    const val RED = 0xFFE57373
    const val GREEN = 0xFF81C784
    const val BLUE = 0xFF64B5F6
}

// Format seconds into minutes and seconds
fun Duration.formatMinSec(): String {
    val minutes = this.inWholeMinutes
    val seconds = this.inWholeSeconds % 60
    return "$minutes:${if (seconds < 10) "0$seconds" else seconds}"
}

fun backgroundColorFromState(state: PomodoroState): Color {
    return when (state) {
        is PomodoroState.Pomodoro -> Color(Colors.RED)
        is PomodoroState.Break -> Color(Colors.GREEN)
        is PomodoroState.LongBreak -> Color(Colors.BLUE)
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val themeDefinition = JewelTheme.darkThemeDefinition()

    val viewModel = remember { PomodoroViewModel() }

    IntUiTheme(
        theme = themeDefinition,
        styling = ComponentStyling.decoratedWindow(),
        swingCompatMode = true,
    ) {
        val state: PomodoroState by viewModel.state.collectAsState()
        val pomodoroStep: Int by viewModel.pomodoroCount.collectAsState()

        Box(modifier = modifier.fillMaxSize().background(backgroundColorFromState(state))) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)) {
                Text(
                    state.duration.formatMinSec(), style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 92.sp,
                        fontWeight = FontWeight.Bold),
                    )
                Spacer(modifier = Modifier.height(32.dp))
                PomodoroControl(
                    onStop = { viewModel.stop() },
                    onStart = { viewModel.start() },
                    onNext = { viewModel.next() }
                )
            }
            PomodoroStep(
                currentStep = pomodoroStep,
                modifier = modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp).fillMaxWidth())
        }
    }
}

@Composable
fun PomodoroStep(
    currentStep: Int = 0,
    modifier: Modifier = Modifier
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

@Composable
fun PomodoroControl(onStop : () -> Unit, onStart : () -> Unit, onNext : () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
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
