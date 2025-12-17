package com.myapp.greetingcard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.myapp.greetingcard.EMAIL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigator: NavHostController,
    changeMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val preferencesFlow: Flow<Preferences> = appContext.dataStore.data
        val preferences = preferencesFlow.first()
        changeMessage(preferences[EMAIL] ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            navigator.navigate(route = "study_cards")
            changeMessage("Study Cards...")
            Log.d("TEST", "Navigating to StudyWordsScreen...")
        }) { Text("Study Cards") }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            navigator.navigate(route = "add_card")
            changeMessage("Add Card...")
            Log.d("TEST", "Navigating to AddCardScreen...")
        }) { Text("Add Card") }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            navigator.navigate("search_cards")
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
