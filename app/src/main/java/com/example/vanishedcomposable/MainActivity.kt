package com.example.vanishedcomposable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vanishedcomposable.ui.theme.VanishedComposableTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.composable.VanishComposable
import com.example.vanishcomposable.controller.AnimationController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VanishedComposableTheme {
                val listItems = remember {
                    mutableStateListOf(
                        VanishItem(1L, AnimationEffect.DISSOLVE, R.drawable.flowers),
                        VanishItem(2L, AnimationEffect.EXPLODE, R.drawable.cameleon),
                        VanishItem(3L, AnimationEffect.LEFT_TO_RIGHT, R.drawable.cheetah),
                        VanishItem(4L, AnimationEffect.SCATTER, R.drawable.sea),
                    )
                }
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    containerColor = Color.Black
                ) { innerPadding ->
                    LazyColumn(Modifier.padding(innerPadding)) {
                        itemsIndexed(listItems, key = { _, item -> item.id }) { index, item ->
                            var controller: AnimationController? by remember { mutableStateOf(null) }
                            VanishComposable(
                                Modifier
                                    .height(200.dp)
                                    .fillMaxWidth(),
                                dotSize = 2f,
                                effect = item.effect,
                                onControllerReady = {
                                    controller = it
                                }
                            ) {
                                ContentComposable(controller, item, listItems)
                            }
                        }
                    }
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VanishedComposableTheme {
        Greeting("Android")
    }
}

@Composable
fun ContentComposable(
    controller: AnimationController?,
    item: VanishItem,
    listItems: SnapshotStateList<VanishItem>,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(id = item.drawableRes),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "This is a random text just to make sure that you undersand this!",
                Modifier.width(100.dp),
                color = Color.Black,
                fontSize = 30.sp,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                controller?.triggerVanish() {
                    listItems.remove(item)
                }
            }) {
                Text("Delete", color = Color.White)
            }
        }
    }
}