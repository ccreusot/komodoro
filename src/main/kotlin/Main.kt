
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import fr.cedriccreusot.component.PomodoroControls
import fr.cedriccreusot.component.PomodoroStep
import fr.cedriccreusot.extension.formatToMinutesAndSeconds
import fr.cedriccreusot.theme.PomodoroColors
import fr.cedriccreusot.viewmodel.PomodoroState
import fr.cedriccreusot.viewmodel.PomodoroViewModel
import kotlinx.coroutines.delay
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Text

@Composable
fun App(pomodoroStateChange: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = remember {
        PomodoroViewModel()
    }

    fun backgroundColorFromState(state: PomodoroState): Color {
        return when (state) {
            is PomodoroState.Pomodoro -> PomodoroColors.RED
            is PomodoroState.Break -> PomodoroColors.GREEN
            is PomodoroState.LongBreak -> PomodoroColors.BLUE
        }
    }

    val state: PomodoroState by viewModel.state.collectAsState()
    val pomodoroStep: Int by viewModel.pomodoroCount.collectAsState()
    val backgroundColor: Color by animateColorAsState(
        targetValue = backgroundColorFromState(state),
        label = "backgroundColor",
        animationSpec = tween(500)
    )

    MainScreen(backgroundColor, state, viewModel, pomodoroStep, pomodoroStateChange, modifier)
}

@Composable
private fun MainScreen(
    backgroundColor: Color,
    state: PomodoroState,
    viewModel: PomodoroViewModel,
    pomodoroStep: Int,
    pomodoroStateChange: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var previousState: Class<out PomodoroState> by remember {
        mutableStateOf(PomodoroState.Pomodoro::class.java)
    }

    fun currentState(state: PomodoroState) =
        when (state) {
            is PomodoroState.Pomodoro -> PomodoroState.Pomodoro::class.java
            is PomodoroState.Break -> PomodoroState.Break::class.java
            is PomodoroState.LongBreak -> PomodoroState.LongBreak::class.java
        }

    LaunchedEffect(state) {
        if (previousState != currentState(state)) {
            pomodoroStateChange()
        }
        previousState = currentState(state)
    }

    Box(modifier = modifier.fillMaxSize().background(backgroundColor)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                state.duration.formatToMinutesAndSeconds(),
                style = LocalTextStyle.current.copy(
                    color = Color.White, fontSize = 92.sp, fontWeight = FontWeight.Bold
                ),
            )
            Spacer(modifier = Modifier.height(32.dp))
            PomodoroControls(onStop = { viewModel.stop() },
                onStart = { viewModel.start() },
                onNext = { viewModel.next() })
        }
        PomodoroStep(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp).fillMaxWidth(),
            currentStep = pomodoroStep
        )
    }
}

@OptIn(ExperimentalJewelApi::class, ExperimentalComposeUiApi::class)
fun main() = application {
    enableNewSwingCompositing()

    val themeDefinition = JewelTheme.darkThemeDefinition()
    val trayState = remember { TrayState() }

    Tray(
        icon = painterResource("icons/timer.svg"),
        state = trayState,
        menu = {
            Item("Exit", onClick = ::exitApplication)
        },
    )

    // To bring the app to front when the app trigger a pomodoro state change
    var windowOnTop by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication, title = "Komodoro",
        alwaysOnTop = windowOnTop,
    ) {
        IntUiTheme(
            theme = themeDefinition,
            styling = ComponentStyling.decoratedWindow(),
            swingCompatMode = true,
        ) {
            LaunchedEffect(windowOnTop) {
                window.requestFocus()
                window.toFront()
                delay(200L)
                windowOnTop = false
            }

            App({
                windowOnTop = true
            })
        }
    }
}
