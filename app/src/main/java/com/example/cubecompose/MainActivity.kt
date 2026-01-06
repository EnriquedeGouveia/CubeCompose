package com.example.cubecompose

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cubecompose.ui.theme.CubeComposeTheme
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerForActivityOrService

class MainActivity : ComponentActivity() {

    private var mUnityPlayer: UnityPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mUnityPlayer = UnityPlayerForActivityOrService(this)
        setContent {
            CubeComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.weight(1f)
                        )
                        UnityView(
                            unityPlayer = mUnityPlayer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mUnityPlayer?.windowFocusChanged(hasFocus)
    }

    override fun onDestroy() {
        mUnityPlayer?.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mUnityPlayer?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mUnityPlayer?.onResume()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
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
