package com.example.cubecompose

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cubecompose.ui.theme.CubeComposeTheme
import com.unity3d.player.UnityPlayer

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
                        UnityView(
                            unityPlayer = unityPlayerHolder.player,
                            modifier = Modifier.weight(1f)
                        )
                        AndroidControlPanel(modifier = Modifier.weight(1f))
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
fun AndroidControlPanel(modifier: Modifier = Modifier) {
    // Get the singleton instance to call its methods
    val unityPlayerHolder = UnityPlayerHolder.getInstance(LocalContext.current)
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Greeting(name = "Android Controls")
        Spacer(modifier = Modifier.height(16.dp))

        // Read the static state for the UI
        Text(text = UnityPlayerHolder.rotationData.value)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { unityPlayerHolder.changeColor() }) {
            Text("Change to Random Color")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Rotate Cube by 45 degrees")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { unityPlayerHolder.rotate("X") }) {
                Text("X-Axis +")
            }
            Button(onClick = { unityPlayerHolder.rotate("Y") }) {
                Text("Y-Axis +")
            }
            Button(onClick = { unityPlayerHolder.rotate("Z") }) {
                Text("Z-Axis +")
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { unityPlayerHolder.rotate("X") }) {
                Text("X-Axis -")
            }
            Button(onClick = { unityPlayerHolder.rotate("Y") }) {
                Text("Y-Axis -")
            }
            Button(onClick = { unityPlayerHolder.rotate("Z") }) {
                Text("Z-Axis -")
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun UnityView(unityPlayer: UnityPlayer?, modifier: Modifier = Modifier) {
    unityPlayer?.let {
        AndroidView(
            factory = { context ->
                (it.view.parent as? ViewGroup)?.removeView(it.view)
                it.view
            },
            modifier = modifier
        )
    }
}
