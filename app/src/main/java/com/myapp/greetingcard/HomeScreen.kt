package com.myapp.greetingcard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    navigator: NavHostController,
    changeMessage: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        changeMessage("Please, select an option.")
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
            navigator.navigate("search_cards") }) { Text("Search Card") }
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            navigator.navigate("login_page") }) { Text("Login") }
    }



}

