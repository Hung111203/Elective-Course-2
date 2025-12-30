package com.myapp.greetingcard


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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch

@Composable
fun ShowCardScreen(
    args: ShowCard,
    getCardById: suspend (Int) -> FlashCard?,
    updateCard: suspend (FlashCard) -> Unit,
    changeMessage: (String) -> Unit
) {
    var enWord by rememberSaveable { mutableStateOf("") }
    var vnWord by rememberSaveable { mutableStateOf("") }
    var cardFound by rememberSaveable { mutableStateOf(false) } // State to track if card was found
    var isLoading by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()


    LaunchedEffect(args.cardId) {
        isLoading = true
        val card = getCardById(args.cardId)
        if (card != null) {
            enWord = card.englishCard ?: ""
            vnWord = card.vietnameseCard ?: ""
            cardFound = true // Mark successfully found
        } else {
            cardFound = false
        }
        isLoading = false
    }

    Column() {
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
                    onValueChange = { vnWord = it }, // Link to state variable
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Vietnamese String" },
                    label = { Text(stringResource(id = R.string.Vietnamese_label)) }
                )

                Button(
                    onClick = {
                        scope.launch {
                            // Create an updated FlashCard object and save it
                            val updatedFlashCard = FlashCard(
                                uid = args.cardId,
                                englishCard = enWord,
                                vietnameseCard = vnWord
                            )
                            updateCard(updatedFlashCard)
                            changeMessage("Card updated successfully.");
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

            } else {
                Text("Card not found.")
            }
        }
    }

