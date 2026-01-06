package com.example.cubecompose

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerForActivityOrService
import java.util.Locale

/**
 * A singleton class to manage the UnityPlayer instance, using a static factory method.
 */
class UnityPlayerHolder private constructor(context: Context) {

    val player: UnityPlayer = UnityPlayerForActivityOrService(context.applicationContext)

    // --- Instance methods to control the player ---
    fun setColorFromHue(hue: Float) {
        // Android's Color.hsvToColor takes an array. The hue value is 0-360.
        val hsv = floatArrayOf(hue * 360f, 1f, 1f)
        val colorInt = android.graphics.Color.HSVToColor(hsv)

        // Format as a hex string for Unity, ensuring alpha is fully opaque
        val hexColor = String.format("#%06X", 0xFFFFFF and colorInt)
        UnityPlayer.UnitySendMessage("Cube", "SetColor", hexColor)
    }

    fun setAbsoluteXYRotation(x: Float, y: Float) {
        // Clamp the vertical rotation to avoid flipping the cube upside down
        val clampedX = x.coerceIn(-80f, 80f)
        rotationX.floatValue = clampedX
        rotationY.floatValue = y
        // Preserve the current Z rotation
        val message = String.format(Locale.US, "%.2f,%.2f,%.2f", clampedX, y, rotationZ.floatValue)
        UnityPlayer.UnitySendMessage("Cube", "SetRotation", message)
    }

    fun incrementalRotate(axis: String, amount: Float) {
        when (axis) {
            "X" -> rotationX.floatValue += amount
            "Y" -> rotationY.floatValue += amount
            "Z" -> rotationZ.floatValue += amount
        }
        val message = String.format(
            Locale.US,
            "%.2f,%.2f,%.2f",
            rotationX.floatValue,
            rotationY.floatValue,
            rotationZ.floatValue
        )
        UnityPlayer.UnitySendMessage("Cube", "SetRotation", message)
    }

    companion object {
        @Volatile
        private var instance: UnityPlayerHolder? = null

        // --- State Properties ---
        val rotationData = mutableStateOf("Rotation: (0, 0, 0)")
        val rotationX = mutableFloatStateOf(35.264f)
        val rotationY = mutableFloatStateOf(45f)
        val rotationZ = mutableFloatStateOf(0f)


        fun getInstance(context: Context): UnityPlayerHolder {
            return instance ?: synchronized(this) {
                instance ?: UnityPlayerHolder(context).also { instance = it }
            }
        }

        @JvmStatic
        fun updateRotationData(data: String) {
            rotationData.value = "Rotation: ($data)"
            Log.d("enrique", "updateRotationData: data: $data currentRotation: ${rotationData.value} ")
        }

        fun destroy() {
            instance?.player?.destroy()
            instance = null
        }
    }
}