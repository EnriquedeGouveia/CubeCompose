package com.example.cubecompose

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cubecompose.ui.theme.CubeComposeTheme

class MainActivity : ComponentActivity() {

    private lateinit var unityPlayerHolder: UnityPlayerHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the singleton instance of the Unity Player holder
        unityPlayerHolder = UnityPlayerHolder.getInstance(this)

        setContent {
            CubeComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        // The interactive Unity View with touch controls in the top half
                        InteractiveUnityView(modifier = Modifier.weight(1f))

                        // The control panel with buttons in the bottom half
                        AndroidControlPanel(
                            modifier = Modifier
                                .weight(1f)
                                .background(color = androidx.compose.ui.graphics.Color(Color.BLACK))
                        )
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        unityPlayerHolder.player.windowFocusChanged(hasFocus)
    }

    override fun onDestroy() {
        if (isFinishing) {
            UnityPlayerHolder.destroy()
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        unityPlayerHolder.player.onPause()
    }

    override fun onResume() {
        super.onResume()
        unityPlayerHolder.player.onResume()
    }
}

@Composable
fun InteractiveUnityView(modifier: Modifier = Modifier) {
    val unityPlayerHolder = UnityPlayerHolder.getInstance(LocalContext.current)

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                (unityPlayerHolder.player.view.parent as? ViewGroup)?.removeView(unityPlayerHolder.player.view)
                unityPlayerHolder.player.view
            },
            update = { view ->
                view.requestLayout()
            },
            modifier = Modifier.fillMaxSize()
        )

        // The transparent overlay for touch input
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Invert both controls for natural feel
                        val newRotationX =
                            UnityPlayerHolder.rotationX.floatValue - dragAmount.y / 2f
                        val newRotationY =
                            UnityPlayerHolder.rotationY.floatValue - dragAmount.x / 2f
                        unityPlayerHolder.setAbsoluteXYRotation(newRotationX, newRotationY)
                    }
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidControlPanel(modifier: Modifier = Modifier) {
    val unityPlayerHolder = UnityPlayerHolder.getInstance(LocalContext.current)
    val rotationAmount = 5f
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    val rainbowColors = remember {
        listOf(
            androidx.compose.ui.graphics.Color.Red,
            androidx.compose.ui.graphics.Color(0xFF, 0xA5, 0x00), // Orange
            androidx.compose.ui.graphics.Color.Yellow,
            androidx.compose.ui.graphics.Color.Green,
            androidx.compose.ui.graphics.Color.Blue,
            androidx.compose.ui.graphics.Color(0x4B, 0x00, 0x82), // Indigo
            androidx.compose.ui.graphics.Color(0xEE, 0x82, 0xEE), // Violet
            androidx.compose.ui.graphics.Color.Red // Loop back to red
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Android Controls")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = UnityPlayerHolder.rotationData.value)
        Spacer(modifier = Modifier.height(16.dp))

        // Color Slider
        Text("Color")
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                unityPlayerHolder.setColorFromHue(it)
            },
            modifier = Modifier.padding(horizontal = 24.dp),
            track = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            Brush.horizontalGradient(rainbowColors),
                            RoundedCornerShape(4.dp)
                        )
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Rotate Cube by $rotationAmount degrees")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { unityPlayerHolder.incrementalRotate("X", rotationAmount) }) {
                Text("X-Axis +")
            }
            Button(onClick = { unityPlayerHolder.incrementalRotate("Y", rotationAmount) }) {
                Text("Y-Axis +")
            }
            Button(onClick = { unityPlayerHolder.incrementalRotate("Z", rotationAmount) }) {
                Text("Z-Axis +")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { unityPlayerHolder.incrementalRotate("X", -rotationAmount) }) {
                Text("X-Axis -")
            }
            Button(onClick = { unityPlayerHolder.incrementalRotate("Y", -rotationAmount) }) {
                Text("Y-Axis -")
            }
            Button(onClick = { unityPlayerHolder.incrementalRotate("Z", -rotationAmount) }) {
                Text("Z-Axis -")
            }
        }
    }
}
