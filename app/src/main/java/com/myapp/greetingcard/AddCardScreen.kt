package com.myapp.greetingcard

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch

@Composable

fun AddCardScreen(changeMessage: (String) -> Unit,
                  insertFlashCard: suspend (FlashCard) -> Unit) {

    var enWord by rememberSaveable { mutableStateOf("") }

    var vnWord by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        changeMessage("Please, add a flash card.")
    }
    Column {

        TextField(

            value = enWord,
            onValueChange = { enWord = it },
            modifier = Modifier.semantics{contentDescription = "English String"},
            label = { Text(stringResource(id = R.string.English_label))
            }


        )

        TextField(

            value = vnWord,
            onValueChange = { vnWord = it },
            modifier = Modifier.semantics{contentDescription = "Vietnamese String"},
            label = { Text(stringResource(id = R.string.Vietnamese_label)) }

        )

        Button(
            modifier = Modifier.semantics { contentDescription = "Add" },
            onClick = {
                scope.launch {
                    try {
                        insertFlashCard(
                            FlashCard(
                                uid = 0,
                                englishCard = enWord,
                                vietnameseCard = vnWord
                            )
                        )
                        enWord = ""
                        vnWord = ""
                        changeMessage("The flash card has been added to your database")
                    } catch (e: SQLiteConstraintException) {
                        changeMessage("The flash card already exists in your database")
                    } catch (e: Exception) {
                        changeMessage("Something went wrong")
                    }
                }
            }
        ) {

            Text("Add")

        }

    }

}