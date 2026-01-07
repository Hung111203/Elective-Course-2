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
import androidx.navigation.toRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(navController: NavHostController,networkService: NetworkService,flashCardDao: FlashCardDao) {

    var message by rememberSaveable { mutableStateOf("Welcome") }

    val insertFlashCard: suspend (FlashCard) -> Unit ={
        flashCard ->flashCardDao.insertAll(flashCard)
        //suspend func
    }
    val changeMessage = fun(text:String){
        message = text
    }
    val getAllFlashCards: suspend () -> List<FlashCard> = {
        flashCardDao.getAll()
    }
    val getCardById: suspend (Int) -> FlashCard? = { id ->
        flashCardDao.getCardById(id)
    }
    val deleteCardById: suspend (Int) -> Unit = { id ->
        flashCardDao.deleteById(id)
    }
    val updateCard: suspend (FlashCard) -> Unit = { flashCard ->
        flashCardDao.updateCard(flashCard)
    }
    val getLesson: suspend (Int) -> List<FlashCard> = { size ->
        flashCardDao.getLesson(size)
    }
    val navigateToHome = fun(){
        navController.navigate("home")
    }
    val navigateToToken = fun(email:String){
        navController.navigate(TokenRoute(email))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
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
            startDestination = HomeRoute
        ) {
            // HOME
            composable<HomeRoute>{
                HomeScreen(
                    navigator = navController,
                    changeMessage = { message = it }
                )
            }
         // ADD CARD using composable
//            composable(route = "add_card") {
//            AddCardScreen(...)
//            }
            // ADD CARD
            composable<AddCardRoute>{
                AddCardScreen(
                    changeMessage = { message = it },
                    insertFlashCard = insertFlashCard
                )
            }
            // STUDY CARDS
            composable<StudyCardsRoute>{
                StudyCardsScreen(
                    getLesson = getLesson,
                    networkService= networkService,
                    changeMessage = changeMessage
                )
            }
            // SEARCH CARDS
            composable<SearchCardsRoute>{
                SearchCardsScreen(
                    getAllFlashCards = getAllFlashCards,
                    deleteCardById = deleteCardById,
                    onEditSelected = { flashCard ->
                        navController.navigate(ShowCard(cardId = flashCard.uid))
                    }
                )
            }
            composable<ShowCard> { backStackEntry ->
                // This automatically gets the arguments from the navigation action.
                val args: ShowCard = backStackEntry.toRoute()
                ShowCardScreen(
                    args = args,
                    getCardById = getCardById,
                    updateCard = updateCard,
                    changeMessage = changeMessage
                )
            }
            composable <LoginRoute>{
                LoginPage(
                    networkService= networkService,
                    changeMessage = changeMessage,
                    navigateToToken = navigateToToken
                )
            }
            composable <TokenRoute>{
                backStackEntry ->
                val tokenRoute : TokenRoute = backStackEntry.toRoute()
                TokenScreen(
                    changeMessage = changeMessage,
                    navigateToHome = navigateToHome,
                    email = tokenRoute.email
                )

            }


        }
    }
}
