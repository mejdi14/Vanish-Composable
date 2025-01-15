package com.example.vanishedcomposable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vanishedcomposable.ui.theme.VanishedComposableTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.VanishOptions
import com.example.vanishcomposable.composable.VanishComposable
import com.example.vanishcomposable.controller.AnimationController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VanishedComposableTheme {
                var controller: AnimationController? by remember { mutableStateOf(null) }
                VanishComposable(
                 vanishOptions = VanishOptions(
                    pixelSize = 50f,  // Larger size for more visible squares
                    pixelSpacing = 0f,  // No spacing between squares
                    animationDuration = 5000,  // Customize as needed
                    triggerFinishAt = 1f,
                    pixelVelocity = 100
                ),
                    effect = AnimationEffect.MOSAIC,
                    onControllerReady = {
                        controller = it
                    }){
                    ImageContent(controller)
                }
            }
        }
    }
}

data class VanishItem(
    val id: Long,
    val effect: AnimationEffect,
    @DrawableRes val drawableRes: Int
)




@Composable
fun ContentComposable(
    controller: AnimationController?,
    item: VanishItem,
    listItems: SnapshotStateList<VanishItem>,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,

            ) {
            Image(
                painter = painterResource(id = item.drawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp)),

                )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. ",
                Modifier.width(100.dp),
                color = Color.White,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Image(
            painter = painterResource(id = R.drawable.cross),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(35.dp)
                .clickable {
                    controller?.triggerVanish() {
                        listItems.remove(item)
                    }
                }
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),

            )
    }
}

@Composable
fun ImageContent(controller: AnimationController?) {
    Image(
        painterResource(R.drawable.messi),
        contentScale = ContentScale.Crop,
        contentDescription = "",
        modifier = Modifier.fillMaxSize()
            .clickable{
                controller?.triggerVanish()
            }
    )
}