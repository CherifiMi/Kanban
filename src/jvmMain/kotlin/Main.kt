import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import component.DragCard
import model.DragCardData
import model.SlideState
import util.Dimens

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

val allDragCards = arrayOf(
    DragCardData(
        color = Color.Red
    ),
    DragCardData(
        color = Color.Blue
    ),
    DragCardData(
        color = Color.Black
    ),
    DragCardData(
        color = Color.Cyan
    )
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    val dragCards = remember { mutableStateListOf(*allDragCards) }
    val slideStates = remember {
        mutableStateMapOf<DragCardData, SlideState>()
            .apply {
                dragCards.map { dragCards ->
                    dragCards to SlideState.NONE
                }.toMap().also {
                    putAll(it)
                }
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Mito")
                },
                actions = {
                    IconButton(onClick = {
                        val newShoesArticles = mutableListOf<DragCardData>()
                        DragCardData.ID += 1
                        allDragCards.forEach {
                            newShoesArticles.add(it.copy(id = DragCardData.ID))
                        }

                        dragCards.addAll(newShoesArticles)
                    }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null)
                    }
                },
                backgroundColor = MaterialTheme.colors.background
            )
        }
    ) { innerPadding ->
        ShoesList(
            modifier = Modifier.padding(innerPadding),
            shoesArticles = dragCards,
            slideStates = slideStates,
            updateSlideState = { shoesArticle, slideState -> slideStates[shoesArticle] = slideState },
            updateItemPosition = { currentIndex, destinationIndex ->
                val shoesArticle = dragCards[currentIndex]
                dragCards.removeAt(currentIndex)
                dragCards.add(destinationIndex, shoesArticle)
                slideStates.apply {
                    dragCards.map { shoesArticle ->
                        shoesArticle to SlideState.NONE
                    }.toMap().also {
                        putAll(it)
                    }
                }
            }
        )
    }
}



@ExperimentalAnimationApi
@Composable
fun ShoesList(
    modifier: Modifier,
    shoesArticles: MutableList<DragCardData>,
    slideStates: Map<DragCardData, SlideState>,
    updateSlideState: (shoesArticle: DragCardData, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        modifier = modifier.padding(top = Dimens.list_top_padding).fillMaxSize()
    ) {
        items(shoesArticles.size) { index ->
            val shoesArticle = shoesArticles.getOrNull(index)
            if (shoesArticle != null) {
                key(shoesArticle) {
                    val slideState = slideStates[shoesArticle] ?: SlideState.NONE
                    DragCard(
                        dragCardData = shoesArticle,
                        slideState = slideState,
                        dragCards = shoesArticles,
                        updateSlideState = updateSlideState,
                        updateItemPosition = updateItemPosition
                    )
                }
            }
        }
    }
}

