package com.myapp.greetingcard


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun ShowCardScreen(
    args: ShowCard,
    getCardById: suspend (Int) -> FlashCard?
) {
    var flashCard by remember { mutableStateOf<FlashCard?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetches the card data when the screen is first displayed
    LaunchedEffect(args.cardId) {
        isLoading = true
        flashCard = getCardById(args.cardId)
        isLoading = false
    }

    // --- Main Layout Column, styled like AddCardScreen ---
    Column() {
        if (isLoading) {
            // Show a loading spinner while fetching the card
            CircularProgressIndicator()
        } else {
            val currentCard = flashCard
            if (currentCard != null) {

                // --- English Text Field  ---
                TextField(
                    value = currentCard.englishCard ?: "N/A",
                    onValueChange = {}, // Empty onValueChange makes it read-only
                    readOnly = true,    // Explicitly set to read-only
                    modifier = Modifier.semantics{contentDescription = "English String"},
                    label = { Text(stringResource(id = R.string.English_label)) }
                )

                // --- Vietnamese Text Field ---
                TextField(
                    value = currentCard.vietnameseCard ?: "N/A",
                    onValueChange = {}, // Read-only
                    readOnly = true,
                    modifier = Modifier.semantics{contentDescription = "Vietnamese String"},
                    label = { Text(stringResource(id = R.string.Vietnamese_label)) }

                )

                // --- Delete Button ---
                Button(
                    onClick = {
                        // TODO: Implement delete logic later
                    },
                    //modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete")
                }

            } else {
                // Show a message if the card couldn't be found
                Text("Card not found.")
            }
        }
    }
}
