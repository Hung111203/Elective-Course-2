package com.myapp.greetingcard

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.io.IOException
import java.security.MessageDigest

private fun saveAudioToInternalStorage(context: Context, audioData: ByteArray, filename: String) {
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { fos ->
        fos.write(audioData)
    }
}

fun hashStringSHA256(input: String): String {
    val bytes = input.toByteArray(Charsets.UTF_8)
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold(""){
            str, it -> str + "%02x".format(it)
    }

}

@Composable
fun StudyCardsScreen(
    navigator: NavHostController,
    getLesson: suspend (Int) -> List<FlashCard>,
    networkService: NetworkService,
    changeMessage: (String) -> Unit
) {


    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appContext = context.applicationContext
    var lesson: List<FlashCard> by remember { mutableStateOf(emptyList()) }
    var currentCardIndex by remember { mutableStateOf(0) }
    var lang by remember { mutableStateOf("en") }
    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    val size  = 3
    var flashCard by remember { mutableStateOf<FlashCard?>(null) }
    // FIX: Using simple `remember` is better here since we re-check file existence every time.
    var audioFile by remember { mutableStateOf("") }


    // Fetch the lesson on first composition
    LaunchedEffect(Unit) {
        val preferencesFlow : Flow<Preferences> = appContext.dataStore.data
        val preferences = preferencesFlow.first()
        email = preferences[EMAIL] ?: ""
        token = preferences[TOKEN] ?: ""

        lesson = getLesson(size)
        // FIX 1: The logic was inverted. This should be `isNotEmpty`.
        if (lesson.isNotEmpty()) {
            lesson = lesson.shuffled()
            currentCardIndex = 0;
            flashCard = lesson.get(currentCardIndex)
        } else {
            changeMessage("The database is empty.")
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (flashCard != null) {
            // FIX 2: Wrapped UI elements in a Column for proper vertical layout.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card display logic
                if (lang == "en"){
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { lang = "vn" }
                    ) {
                        Text(
                            style = MaterialTheme.typography.headlineLarge,
                            text = flashCard?.englishCard.orEmpty()
                        )
                    }
                } else {
                    // ADDED: The missing display for the Vietnamese card.
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { lang = "en" }
                    ) {
                        Text(
                            style = MaterialTheme.typography.headlineLarge,
                            text = flashCard?.vietnameseCard.orEmpty()
                        )
                    }
                }

                // File existence check
                val fileName = hashStringSHA256(flashCard?.vietnameseCard.orEmpty())
                val file = File(context.filesDir, fileName)
                if (file.exists()) {
                    audioFile = fileName
                } else {
                    // ADDED: Reset audioFile if the file for the current card doesn't exist.
                    audioFile = ""
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        currentCardIndex = (currentCardIndex + 1) % lesson.size
                        flashCard = lesson[currentCardIndex]
                        lang = "en"
                    }
                ) {
                    Text("Next")
                }

                // This `if/else` block determines whether to show the "Download" or "Play" button.
                if (audioFile.isEmpty()){
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "GenerateButton" },
                        onClick = {
                            scope.launch {
                                changeMessage("Downloading...") // UI feedback
                                withContext(Dispatchers.IO) {
                                    try {
                                        val response = networkService.getAudio(
                                            request = AudioRequest(
                                                word = flashCard?.vietnameseCard.orEmpty(),
                                                email = email,
                                                token = token
                                            )
                                        )
                                        if (response.code == 200) {
                                            val decodedBytes = Base64.decode(response.message, Base64.DEFAULT)
                                            val generatedFileName =
                                                hashStringSHA256(flashCard?.vietnameseCard.orEmpty())

                                            // FIX 3: Correctly pass the filename parameter.
                                            saveAudioToInternalStorage(
                                                context = context,
                                                audioData = decodedBytes,
                                                filename = generatedFileName
                                            )
                                            // Update state to make the "Play" button appear next time.
                                            audioFile = generatedFileName
                                            changeMessage("Audio downloaded successfully.")
                                        } else {
                                            changeMessage("Error: ${response.message}")
                                        }
                                    } catch (e: Exception) {
                                        Log.e("StudyCardsScreen", "Download failed: ${e.message}")
                                        changeMessage("Network error. Download failed.")
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Download Audio")
                    }
                } else {
                    // This button appears only when the audio file exists.
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val fileToPlay = File(context.filesDir, audioFile)
                            val filePath = fileToPlay.absolutePath
                            // Create a Uri from the file path
                            val uri = filePath.toUri()
                            // Build the MediaItem
                            val mediaItem = MediaItem.fromUri(uri)
                            // Build the Player
                            val player = ExoPlayer.Builder(context).build()
                            player.addListener(object : Player.Listener {
                                override fun onPlaybackStateChanged(playbackState: Int) {
                                    when (playbackState) {
                                        Player.STATE_BUFFERING -> {
                                            changeMessage("Buffering...")
                                        }
                                        Player.STATE_READY -> {
                                            changeMessage("Ready")
                                        }
                                        Player.STATE_ENDED -> {
                                            // Playback has finished
                                            player.release() // Important: Release the player
                                            changeMessage("Finished")
                                        }
                                        Player.STATE_IDLE -> {
                                            // Player is idle
                                        }
                                    }
                                }
                            })
                            // Set the media item to the player and prepare
                            player.setMediaItem(mediaItem)
                            // Prepare the player.
                            player.prepare()
                            // Start the playback.
                            player.play()
                        }
                    ) {
                        Text("Play Audio")
                    }
                }
            }
        } else {
            // Show a loading indicator while the lesson is being prepared.
            CircularProgressIndicator()
        }
    }
}
