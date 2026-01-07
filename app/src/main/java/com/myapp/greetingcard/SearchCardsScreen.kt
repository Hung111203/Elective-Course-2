package com.myapp.greetingcard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
//the content of the list itself
fun FlashCardList(
    flashCards: List<FlashCard>,
    onEditClicked: (FlashCard) -> Unit,
    onDeletedClicked: (FlashCard) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(
            items = flashCards,
            key = { flashCard ->
                flashCard.uid
            }
        ) { flashCard ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(6.dp)

                    .clickable(onClick = {
                        onEditClicked(flashCard)
                    }

                    )
            ) {
                Column(modifier = Modifier.padding(6.dp))
                { Text(flashCard.englishCard.toString()) }
                Column(modifier = Modifier.padding(6.dp)) { Text(" = ") }
                Column(modifier = Modifier.padding(6.dp))
                { Text(flashCard.vietnameseCard.toString()) }

                Button(
                    onClick = { onEditClicked(flashCard) },
                    modifier = Modifier.padding(start = 8.dp)
                ){
                    Text("Edit")
                }

                Button(
                    onClick = { onDeletedClicked(flashCard) },
                    modifier = Modifier.padding(start = 8.dp)
                ){
                    Text("Delete")
                }
            }
        }
    }
}


@Composable
fun SearchCardsScreen(
    getAllFlashCards: suspend () -> List<FlashCard>,
    onEditSelected: (FlashCard) -> Unit,
    deleteCardById: suspend (Int) -> Unit,
    ) {
    var flashCards  by remember { mutableStateOf(emptyList<FlashCard>()) }
    val scope = rememberCoroutineScope()

    fun refreshFlashCards() {
        scope.launch {
            flashCards = getAllFlashCards()
        }
    }

    LaunchedEffect(Unit) {
        flashCards = getAllFlashCards()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(
            modifier = Modifier.size(16.dp)
        )
        //page layout, not the content details
        FlashCardList(
            flashCards = flashCards,
            onEditClicked = onEditSelected,
            onDeletedClicked = {
                cardToDelete -> scope.launch{
                    deleteCardById(cardToDelete.uid)
                refreshFlashCards()
            }
            }
        )
    }
}
