package model

import androidx.compose.ui.graphics.Color

data class DragCardData(
    var id: Int = 0,
    var color: Color = Color.Transparent
){
    companion object{
        var ID = 0
    }
}