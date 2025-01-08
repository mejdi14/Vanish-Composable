package com.example.vanishedcomposable

import ComposableAnimation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vanishedcomposable.ui.theme.VanishedComposableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VanishedComposableTheme {
                var vanished = remember { mutableStateOf(false) }
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) { innerPadding ->
                    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.flowers)
                    /*Box(Modifier.height(200.dp).width(80.dp)) {
                        DottedImage(
                            image = imageBitmap,
                            dotSize = 2f,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }*/
                    Column(Modifier.padding(innerPadding)) {
                        ComposableAnimation(
                            Modifier
                                .height(200.dp)
                                .fillMaxWidth(),
                            dotSize = 2f,
                            vanished = vanished
                        ) {
                            ContentComposable(vanished)
                        }

                    }
                }
            }
        }
    }
}


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
fun ContentComposable(vanished: MutableState<Boolean>) {
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
                painter = painterResource(id = R.drawable.flowers),
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
                vanished.value = true
            }) {
                Text("Delete", color = Color.White)
            }
        }
    }
}