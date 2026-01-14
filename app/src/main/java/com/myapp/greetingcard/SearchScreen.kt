package com.myapp.greetingcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(navigator: NavHostController,
                 changeMessage: (String) -> Unit){
    var enWord by rememberSaveable { mutableStateOf("") }

    var vnWord by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var searchByEnglish by rememberSaveable { mutableStateOf(true) }
    var searchByVietnamese by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        changeMessage("Please search a card.")
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Row(

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = searchByEnglish ,
                onCheckedChange = { searchByEnglish = it }
            )

        TextField(

            value = enWord,
            onValueChange = { enWord = it },
            modifier = Modifier.semantics{contentDescription = "English String"},
            label = { Text(stringResource(id = R.string.English_label))
            })
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = searchByVietnamese,
                onCheckedChange = { searchByVietnamese = it }
            )
            TextField(

                value = vnWord,
                onValueChange = { vnWord = it },
                modifier = Modifier.semantics { contentDescription = "Vietnamese String" },
                label = { Text(stringResource(id = R.string.Vietnamese_label)) })
        }
        Button(
            modifier = Modifier.semantics { contentDescription = "SearchCard" },
            onClick = {
                scope.launch {

                }
                navigator.navigate(SearchCardsRoute(
                    en = enWord,
                    vn = vnWord,
                    searchByEnglish = searchByEnglish,
                    searchByVietnamese = searchByVietnamese
                ))
            }


        ) {
            Text("Search")
        }


    }



}



