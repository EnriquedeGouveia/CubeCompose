package com.example.cubecompose

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerForActivityOrService
import kotlin.random.Random

/**
 * A singleton class to manage the UnityPlayer instance, using a static factory method.
 */
class UnityPlayerHolder private constructor(context: Context) {

    val player: UnityPlayer = UnityPlayerForActivityOrService(context.applicationContext)

    // --- Instance methods to control the player ---
    fun changeColor() {
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)
        val hexColor = String.format("#%02X%02X%02X", red, green, blue)
        UnityPlayer.UnitySendMessage("Cube", "SetColor", hexColor)
    }

    /**
     * Sends a rotation message to Unity.
     * @param axis The axis to rotate on ("X", "Y", or "Z").
     * @param amount The degrees to rotate by.
     */
    fun rotate(axis: String, amount: Float) {
        val message = "$axis,$amount"
        UnityPlayer.UnitySendMessage("Cube", "Rotate", message)
    }

    companion object {
        // The volatile annotation ensures that multiple threads handle the instance variable correctly
        @Volatile
        private var instance: UnityPlayerHolder? = null

        // The state for the UI, kept static for easy access
        val rotationData = mutableStateOf("Rotation: (0, 0, 0)")

        // The factory method that provides the singleton instance
        fun getInstance(context: Context): UnityPlayerHolder {
            return instance ?: synchronized(this) {
                instance ?: UnityPlayerHolder(context).also { instance = it }
            }
        }

        /**
         * This method is called from Unity.
         */
        @JvmStatic
        fun updateRotationData(data: String) {
            rotationData.value = "Rotation: ($data)"
            Log.d("enrique", "updateRotationData: data: $data currentRotation: ${rotationData.value} ")
        }

        /**
         * Destroys the player instance when the app is finishing.
         */
        fun destroy() {
            instance?.player?.destroy()
            instance = null
        }
    }
}