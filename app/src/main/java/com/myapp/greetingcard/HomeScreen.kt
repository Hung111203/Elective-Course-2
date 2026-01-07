package com.myapp.greetingcard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigator: NavHostController,
    changeMessage: (String) -> Unit
) {
    val context = LocalContext.current
    //Activity Context -  tied directly to your MainActivity
    // and knows everything about it, including what theme is being used
    val appContext = context.applicationContext
    //read DataStore in a Compose screen
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val preferencesFlow: Flow<Preferences> = appContext.dataStore.data
        val preferences = preferencesFlow.first()
        changeMessage(preferences[EMAIL] ?: "Welcome back!")
        //3.Fetches the stored email if it exists.
        // 4.Updates the bottom bar with a personalized
        //welcome message (e.g., "john.doe@example.com")
        //if the user is logged in, or a generic "Welcome back!" if they are not.
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            navigator.navigate(StudyCardsRoute)
            changeMessage("Study Cards...")
            Log.d("TEST", "Navigating to StudyWordsScreen...")
        }) { Text("Study Cards") }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            //navigator.navigate(route = "add_card")
            navigator.navigate(AddCardRoute)
            changeMessage("Add Card...")
            Log.d("TEST", "Navigating to AddCardScreen...")
        }) { Text("Add Card") }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            navigator.navigate(SearchCardsRoute)
        }) { Text("Search Card") }
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            navigator.navigate(LoginRoute)
        }) { Text("Login") }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "ExecuteLogout" }, onClick = {

                scope.launch {
                    appContext.dataStore.edit { preferences ->
                        preferences.remove(EMAIL)
                        preferences.remove(TOKEN)
                        changeMessage(preferences[EMAIL] ?: "")

                    }

                }

            }) {
            Text(
                "Log out",
                modifier = Modifier.semantics { contentDescription = "Logout" }
            )
        }


    }
}
