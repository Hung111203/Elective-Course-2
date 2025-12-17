package com.myapp.greetingcard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginPage(
    networkService: NetworkService
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var token by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, // Centers children horizontally
        verticalArrangement = Arrangement.Center, // Centers children vertically
    ) {


        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("token") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email") }
        )
        Button(
            modifier = Modifier
                .semantics { contentDescription = "Sign In" },
            onClick = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val result = networkService.generateToken(
                                email = UserCredential(email)
                            )
                            token = result.token
                            Log.d("FLASHCARD", result.toString())
                        } catch (e: Exception) {
                            Log.d("FLASHCARD", "Unexpected exception: $e")
                        }
                    }
                }
            },
            content = { Text(text = "Sign In") }
        )
    }
}