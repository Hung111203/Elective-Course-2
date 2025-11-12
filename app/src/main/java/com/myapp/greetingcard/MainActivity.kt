package com.myapp.greetingcard
import com.myapp.greetingcard.Navigator
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
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
                //Navigator()
                Navigator(navController, flashCardDao)
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