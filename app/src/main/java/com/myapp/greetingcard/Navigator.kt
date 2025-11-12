package com.myapp.greetingcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(navController: NavHostController,flashCardDao: FlashCardDao) {

    var message by rememberSaveable { mutableStateOf("Welcome") }
    val insertFlashCard: suspend (FlashCard) -> Unit ={
        flashCard ->flashCardDao.insertAll(flashCard)
        //suspend,coroutine func

    }
    val changeMessage = fun(text:String){
        message = text
    }
    val getAllFlashCards: suspend () -> List<FlashCard> = {
        flashCardDao.getAll()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                // FIX: Used TopAppBarDefaults.topAppBarColors for clarity and correctness
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "An Nam"
                        )
                    }
                },
                navigationIcon = {
                    val currentRoute =
                        navController.currentBackStackEntryAsState().value?.destination?.route
                    if (currentRoute != "home") {
                        Button(

                            onClick = {
                                message = "Welcome"
                                navController.navigateUp()
                            },
                            modifier = Modifier.padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "Message"
                        },
                    textAlign = TextAlign.Center,
                    text = message
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = "home"
        ) {
            // HOME
            composable(route = "home") {
                HomeScreen(
                    navigator = navController,
                    changeMessage = { message = it }
                )
            }
            // ADD CARD
            composable(route = "add_card") {
                AddCardScreen(
                    navigator = navController,
                    changeMessage = { message = it },
                    insertFlashCard = insertFlashCard
                )
            }
            // STUDY CARDS
            composable(route = "study_cards") {
                StudyCardsScreen(navigator = navController)
            }
            // SEARCH CARDS
            composable(route = "search_cards") {
                SearchCardsScreen(
                   // flashCards = emptyList(),
                    getAllFlashCards = getAllFlashCards,
                    selectedItem ={})
            }
        }
    }
}
