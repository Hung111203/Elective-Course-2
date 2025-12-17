package com.myapp.greetingcard

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

const val LESSON_SIZE = 3


@Composable
fun WordDisplay(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(50)) // Rounded border
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StudyCardsScreen(
    navigator: NavHostController,
    getLesson: suspend (Int) -> List<FlashCard>
) {
    var lesson by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var currentCardIndex by remember { mutableStateOf(0) }
    var showVietnamese by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        lesson = getLesson(LESSON_SIZE)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (lesson.isEmpty()) {
            Text("No flashcards found. Add at least $LESSON_SIZE cards to start a lesson.")
        } else {
            val currentCard = lesson[currentCardIndex]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp) // Space between word and button
            ) {
                val textToShow = if (showVietnamese) {
                    currentCard.vietnameseCard ?: "N/A"
                } else {
                    currentCard.englishCard ?: "N/A"
                }

                WordDisplay(text = textToShow) {
                    showVietnamese = !showVietnamese
                }

                if (showVietnamese) {
                    Button(
                        onClick = {
                            currentCardIndex = (currentCardIndex + 1) % lesson.size
                            showVietnamese = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Next", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
