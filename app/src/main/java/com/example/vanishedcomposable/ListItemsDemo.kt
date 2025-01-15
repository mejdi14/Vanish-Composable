package com.example.vanishedcomposable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vanishcomposable.Animation.AnimationEffect
import com.example.vanishcomposable.composable.VanishComposable
import com.example.vanishcomposable.controller.AnimationController

@Composable
fun ListItemsDemo() {
    val listItems = remember {
        mutableStateListOf(
            VanishItem(1L, AnimationEffect.DISSOLVE, R.drawable.flowers),
            VanishItem(2L, AnimationEffect.SWIRL, R.drawable.cameleon),
            VanishItem(4L, AnimationEffect.SCATTER, R.drawable.sea),
            VanishItem(3L, AnimationEffect.LEFT_TO_RIGHT, R.drawable.cheetah),
            VanishItem(5L, AnimationEffect.DISSOLVE, R.drawable.flowers),
        )
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(Modifier.padding(innerPadding)) {
            itemsIndexed(listItems, key = { _, item -> item.id }) { index, item ->
                var controller: AnimationController? by remember { mutableStateOf(null) }
                VanishComposable(
                    Modifier
                        .height(200.dp)
                        .padding(horizontal = 20.dp),
                    effect = item.effect,
                    onControllerReady = {
                        controller = it
                    }
                ) {
                    ContentComposable(controller, item, listItems)
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}