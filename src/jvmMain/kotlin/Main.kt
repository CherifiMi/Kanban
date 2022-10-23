import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.math.roundToInt

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MyApp()
    }
}

@Composable
fun MyApp() {

    Row {

        LazyColumn(Modifier.fillMaxHeight().width(200.dp).background(Color.Black)) {
            item { DragBox().display() }
            item { DragBox().display() }
            item { DragBox().display() }
            item { DragBox().display() }
        }
        LazyColumn(Modifier.fillMaxHeight().width(200.dp).background(Color.Black)) {
            item { DragBox().display() }
            item { DragBox().display() }
            item { DragBox().display() }
            item { DragBox().display() }
        }
    }
}

class DragBox() {
    var offsetX = mutableStateOf(0f)
    var offsetY = mutableStateOf(0f)

    @Composable
    fun display() {
        Box(modifier = Modifier.fillMaxSize()) {

            LaunchedEffect(offsetY, offsetX) {
                println("$offsetX   $offsetY")
            }
            Box(
                Modifier
                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                    .height(100.dp).fillMaxWidth().padding(8.dp)
                    .background(Color.Blue)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX.value += dragAmount.x
                            offsetY.value += dragAmount.y
                        }
                    }
            )
        }
    }
}
