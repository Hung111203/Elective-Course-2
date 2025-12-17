package com.myapp.greetingcard

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//The Preferences DataStore implementation uses the DataStore and Preferences classes to persist key-value pairs to disk.
//Use the property delegate created by preferencesDataStore to create an instance of DataStore<Preferences>.
//Call it once at the top level of your Kotlin file. Access DataStore through this property
//throughout the rest of your application. This makes it easier to keep your DataStore as a singleton.
val Context.dataStore by preferencesDataStore(
    name = "user_credentials"
)

//Because Preferences DataStore doesn't use a predefined schema,
//you must use the corresponding key type function to define a key for each value that you need to store
//in the DataStore<Preferences> instance.
//For example, to define a key for an int value, use intPreferencesKey()
val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val appContext = applicationContext
                val db = Room.databaseBuilder(
                    applicationContext,
                    AnNamDatabase::class.java, "An Nam database"
                ).build()
                val flashCardDao = db.flashCardDao()
                //flashCardDao.insertAll(FlashCard(uid = 0, englishCard = "test1", vietnameseCard = "test2"))
                //testing
                runBlocking {

                    val flashCard = flashCardDao.getAll()
                    Log.d("AnNam",flashCard.toString() )

                }
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("https://placeholder.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val networkService = retrofit.create(NetworkService::class.java)
                //Navigator()
                Navigator(navController, networkService, flashCardDao)
            }
        }
    }
}
//@Composable
////
//fun MenuScreen() {
//    val slateBlue = Color(0xFF3E5385)
//    val bg = Color(0xFFF7F4FA)
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(bg)
//            .padding(horizontal = 20.dp, vertical = 48.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
//    ) {
//        MenuButton("Study Cards", slateBlue)
//        Spacer(Modifier.height(22.dp))
//        MenuButton("Add Card", slateBlue)
//        Spacer(Modifier.height(22.dp))
//        MenuButton("Search Card", slateBlue)
//    }
//}
//@Composable
//private fun MenuButton(text: String, color: Color) {
//    Button(
//        onClick = { Log.d("MyTest","clickButton " +text) },
//        shape = RoundedCornerShape(28.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(64.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = color,
//            contentColor = Color.White
//        )
//    ) {
//        Text(text, fontSize = 22.sp, fontWeight = FontWeight.W600)
//    }
//}
//
//@Preview(showBackground = true, widthDp = 400)
//@Composable
//private fun MenuScreenPreview() {
//    MaterialTheme { MenuScreen() }
//}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Surface(color=Color.Yellow) {
//        Text(
//            text = "Hello $name!",
//            modifier = modifier.padding(24.dp)
//        )
//    }
//}



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    GreetingCardTheme {
//        Greeting("Hung")
//    }
//}