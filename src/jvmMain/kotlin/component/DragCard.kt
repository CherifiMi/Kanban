package component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import model.DragCardData
import model.Particle
import model.SlideState
import util.Dimens
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


private val particlesStreamRadii = mutableListOf<Float>()
private var itemHeight = 0
private var particleRadius = 0f
private var slotItemDifference = 0f


@Composable
fun DragCard(
    dragCardData: DragCardData,
    slideState: SlideState,
    dragCards: MutableList<DragCardData>,
    updateSlideState: (dragCardData: DragCardData, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,
){
    val itemHeightDp = Dimens.image_size
    val slotPaddingDp = Dimens.slot_padding
    with(LocalDensity.current){
        itemHeight = itemHeightDp.toPx().toInt()
        particleRadius = Dimens.particle_radius.toPx()
        if (particlesStreamRadii.isEmpty())
            particlesStreamRadii.addAll(arrayOf(6.dp.toPx(), 10.dp.toPx(), 14.dp.toPx()))
        slotItemDifference = 18.dp.toPx()
    }
    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState) {
            SlideState.UP -> -itemHeight
            SlideState.DOWN -> itemHeight
            else -> 0
        },
    )

    val isDragged = remember { mutableStateOf(false) }
    val zIndex = if (isDragged.value) 1.0f else 0.0f
    val rotation = if (isDragged.value) -5.0f else 0.0f
    val elevation = if (isDragged.value) 8.dp else 0.dp

    val currentIndex = remember { mutableStateOf(0) }
    val destinationIndex = remember { mutableStateOf(0) }

    val isPlaced = remember { mutableStateOf(false) }
    val leftParticlesRotation = remember { Animatable((Math.PI / 4).toFloat()) }
    val rightParticlesRotation = remember { Animatable((Math.PI * 3 / 4).toFloat()) }

    LaunchedEffect(isPlaced.value){
        if(isPlaced.value){
            launch {
                leftParticlesRotation.animateTo(
                    targetValue = Math.PI.toFloat(),
                    animationSpec = tween(durationMillis = 400)
                )
                leftParticlesRotation.snapTo((PI/4).toFloat())
            }
            launch {
                rightParticlesRotation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 400)
                )
                rightParticlesRotation.snapTo((Math.PI * 3 / 4).toFloat())
                if (currentIndex.value != destinationIndex.value) {
                    updateItemPosition(currentIndex.value, destinationIndex.value)
                }
                isPlaced.value = false
            }
        }
    }

    val leftParticles =
        createParticles(leftParticlesRotation.value.toDouble(), dragCardData.color, isLeft = true)
    val rightParticles =
        createParticles(rightParticlesRotation.value.toDouble(), dragCardData.color, isLeft = false)

    Box(
        Modifier
            .padding(Dimens.horizontal_item_padding)
            .dragToReorder(
                dragCardData,
                dragCards,
                itemHeight,
                updateSlideState,
                isDraggedAfterLongPress = true,
                { isDragged.value = true },
                { cIndex, dIndex ->
                    isDragged.value = false
                    isPlaced.value = true
                    currentIndex.value = cIndex
                    destinationIndex.value = dIndex
                }
            )
            .offset { IntOffset(0, verticalTranslation) }
            .zIndex(zIndex)
            .rotate(rotation)
    ) {
        Canvas(modifier = Modifier) {
            leftParticles.forEach {
                drawCircle(it.color, it.radius, center = IntOffset(it.x, it.y).toOffset())
            }
        }
        Canvas(modifier = Modifier.align(Alignment.TopEnd)) {
            rightParticles.forEach {
                drawCircle(color = it.color,radius = it.radius, center = IntOffset(it.x, it.y).toOffset())
            }
        }
        Column(
            modifier = Modifier
                .shadow(elevation, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = dragCardData.color
                )
                .padding(slotPaddingDp)
                .align(Alignment.CenterStart)
                .fillMaxWidth().height(itemHeightDp)
        ){}
    }
}


private fun createParticles(rotation: Double, color: Color, isLeft: Boolean): List<Particle> {
    val particles = mutableListOf<Particle>()
    for (i in 0 until particlesStreamRadii.size) {
        val currentParticleRadius = particleRadius * (i + 1) / particlesStreamRadii.size
        val verticalOffset =
            (itemHeight.toFloat() - particlesStreamRadii[i] - slotItemDifference + currentParticleRadius).toInt()
        val horizontalOffset = currentParticleRadius.toInt()
        particles.add(
            Particle(
                color = color.copy(alpha = (i + 1) / (particlesStreamRadii.size).toFloat()),
                x = (particlesStreamRadii[i] * cos(rotation)).toInt() + if (isLeft) horizontalOffset else -horizontalOffset,
                y = (particlesStreamRadii[i] * sin(rotation)).toInt() + verticalOffset,
                radius = currentParticleRadius
            )
        )
    }
    return particles
}