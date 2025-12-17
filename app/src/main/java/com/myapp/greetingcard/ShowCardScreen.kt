package com.myapp.greetingcard


import androidx.activity.result.launch
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    deleteCardById: suspend (Int) -> Unit,
    updateCard: suspend (FlashCard) -> Unit,
) {
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var cardFound by remember { mutableStateOf(false) } // State to track if card was found
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()


    // Fetches the card data and populates the text fields
    LaunchedEffect(args.cardId) {
        isLoading = true
        val card = getCardById(args.cardId)
        if (card != null) {
            enWord = card.englishCard ?: ""
            vnWord = card.vietnameseCard ?: ""
            cardFound = true // Mark that the card was successfully found
        } else {
            cardFound = false
        }
        isLoading = false
    }

    // --- Main Layout Column, styled like AddCardScreen ---
    Column() {
        if (isLoading) {
            // Show a loading spinner while fetching the card
            CircularProgressIndicator()
        } else if (cardFound) {


                // --- English Text Field  ---
                TextField(
                        value = enWord,
                        onValueChange = { enWord = it }, // Link to state variable
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "English String" },
                        label = { Text(stringResource(id = R.string.English_label)) }
                    )


                // --- Vietnamese Text Field ---
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
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

            } else {
                // Show a message if the card couldn't be found
                Text("Card not found.")
            }
        }
    }

