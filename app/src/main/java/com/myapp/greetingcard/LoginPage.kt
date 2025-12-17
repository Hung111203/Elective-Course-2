package com.myapp.greetingcard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginPage(
    networkService: NetworkService,
    changeMessage : (String) -> Unit,
    navigateToToken: (String) -> Unit

) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    val token by remember { mutableStateOf("") }
    var code by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = Unit) {
        changeMessage("Enter your email to receive a login token")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        TextField(
            value = email,
            onValueChange = {email = it},
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { contentDescription = "emailTextField" },
            label = { Text("email") }
        )

        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { contentDescription = "Sign In" },
            onClick = {

                scope.launch {
                    try {
                        // 1. Call the network service and wait for the result.
                        val result = withContext(Dispatchers.IO) {
                            networkService.generateToken(email = UserCredential(email))
                        }

                        // 2. Check the result after the call is complete.
                        if (result.code == 200) {
                            changeMessage(result.message)
                            navigateToToken(email) // Navigate on success.
                        } else {
                            changeMessage("Error: ${result.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("LoginPage", "Network request failed: ${e.message}")
                        changeMessage("Request failed. Check connection.")
                    }
                }
            }
        ) {
            Text("Send Token")
        }
    }
}
