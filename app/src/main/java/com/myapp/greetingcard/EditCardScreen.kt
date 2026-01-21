package com.myapp.greetingcard
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Composable
fun EditCardScreen(
    args: EditCard,
    getCardById: suspend (Int) -> FlashCard?,
    updateCard: suspend (FlashCard) -> Unit,
    changeMessage: (String) -> Unit,
    findByCards: suspend (String, String) -> FlashCard?,
    networkService: NetworkService
) {
    var enWord by rememberSaveable { mutableStateOf("") }
    var vnWord by rememberSaveable { mutableStateOf("") }
    var cardFound by rememberSaveable { mutableStateOf(false) } // State to track if card was found
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var audio by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appContext = context.applicationContext
    var audioFileExists by rememberSaveable { mutableStateOf(false) }
    fun checkAudioFile() {
        if (vnWord.isNotBlank()) {
            val fileName = hashStringSHA256(vnWord)
            val file = File(context.filesDir, fileName)
            audioFileExists = file.exists()
            if (audioFileExists) {
                audio = fileName
            } else {
                audio = ""
            }
        } else {
            audioFileExists = false
            audio = ""
        }
    }
    LaunchedEffect(args.cardId) {
        isLoading = true
        val card = getCardById(args.cardId)
        if (card != null) {
            enWord = card.englishCard ?: ""
            vnWord = card.vietnameseCard ?: ""
            cardFound = true // Mark successfully found
            checkAudioFile()
        } else {
            cardFound = false
        }
        isLoading = false
    }
    Column {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (cardFound) {

             TextField(
                        value = enWord,
                        onValueChange = { enWord = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "English String" },
                        label = { Text(stringResource(id = R.string.English_label)) }
                    )

             TextField(
                    value = vnWord,
                    onValueChange = { vnWord = it
                        checkAudioFile()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Vietnamese String" },
                    label = { Text(stringResource(id = R.string.Vietnamese_label)) }
                )
            TextField(
                value = audio,
                onValueChange = {  },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Audio String" },
                label = { Text(stringResource(R.string.audio_label)) }
            )

            Button(
                onClick = {
                    scope.launch {
                        val existingCard = findByCards(enWord, vnWord)
                        if (existingCard != null && existingCard.uid != args.cardId) {
                            changeMessage("A card with these words already exists.")
                        } else {
                            val updatedFlashCard = FlashCard(
                                uid = args.cardId,
                                englishCard = enWord,
                                vietnameseCard = vnWord
                            )
                            updateCard(updatedFlashCard)
                            changeMessage("Card saved successfully.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }

                Button(
                    onClick = {
                        val fileName = hashStringSHA256(vnWord)
                        val file = File(context.filesDir, fileName)
                        if (file.exists() && file.delete()) {
                            checkAudioFile()
                            changeMessage("Audio file deleted.")
                        } else {
                            changeMessage("Error: Could not delete audio file.")
                        }
                    }
                ) {
                    Text("Clean Audio")
                }

            if (audio.isEmpty()){
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "GenerateButton" },
                    onClick = {
                        scope.launch {
                            if (vnWord.isBlank()) {
                                changeMessage("Vietnamese word cannot be empty.")
                                return@launch
                            }

                            val preferencesFlow: Flow<Preferences> = appContext.dataStore.data
                            val preferences = preferencesFlow.first()
                            val email = preferences[EMAIL] ?: ""
                            val token = preferences[TOKEN] ?: ""

                            if (token.isBlank() || email.isBlank()) {
                                changeMessage("You must be logged in to generate audio.")
                                return@launch
                            }

                            changeMessage("Downloading...")
                            withContext(Dispatchers.IO) {
                                try {
                                    val response = networkService.getAudio(
                                        request = AudioRequest(
                                            word = vnWord,
                                            email = email,
                                            token = token
                                        )
                                    )
                                    if (response.code == 200) {
                                        val decodedBytes =
                                            Base64.decode(response.message, Base64.DEFAULT)
                                        val generatedFileName = hashStringSHA256(vnWord)
                                        saveAudioToInternalStorage(
                                            context,
                                            decodedBytes,
                                            generatedFileName
                                        )

                                        withContext(Dispatchers.Main) {
                                            checkAudioFile()
                                            changeMessage("Audio downloaded successfully.")
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            changeMessage("Error: ${response.message}")
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("EditCardScreen", "Download failed: ${e.message}")
                                    withContext(Dispatchers.Main) {
                                        changeMessage("Network error. Download failed.")
                                    }
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
                        val fileToPlay = File(context.filesDir, audio)
                        val uri = fileToPlay.toUri()
                        val mediaItem = MediaItem.fromUri(uri)
                        val player = ExoPlayer.Builder(context).build()
                        player.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                when (playbackState) {
                                    Player.STATE_BUFFERING -> changeMessage("Buffering...")
                                    Player.STATE_READY -> changeMessage("Ready")
                                    Player.STATE_ENDED -> {
                                        player.release()
                                        changeMessage("Finished")
                                    }
                                    Player.STATE_IDLE -> {}
                                }
                            }
                        })
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()
                    }
                ) {
                    Text("Play Audio")
                }
            }
            // --- END: MODIFIED LOGIC ---
        } else {
            Text("Card not found.")
        }
        }
    }

