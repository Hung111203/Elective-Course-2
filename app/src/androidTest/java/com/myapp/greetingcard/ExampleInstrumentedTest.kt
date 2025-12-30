package com.myapp.greetingcard

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MyComposeTest {
    //This creates a shell activity that doesn't have any pre-set content, allowing your tests to call setContent() to set up the UI for the test.
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeStartDestination() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val navController = TestNavHostController(context)

        val db = Room.inMemoryDatabaseBuilder(context, AnNamDatabase::class.java)
            .allowMainThreadQueries() // Allow database operations on the main thread for simplicity in tests.
            .build()
        val flashCardDao = db.flashCardDao()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://placeholder.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val networkService = retrofit.create(NetworkService::class.java)

        navController.navigatorProvider.addNavigator(ComposeNavigator())


        composeTestRule.setContent {
            Navigator(
                navController,
                networkService,
                flashCardDao
            )
        }
        assertEquals("home", navController.currentDestination?.route)
    }

    @Test
    fun clickBackButtonAddCard(){
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val navController = TestNavHostController(context)

        val db = Room.inMemoryDatabaseBuilder(context, AnNamDatabase::class.java)
            .allowMainThreadQueries() // Allow database operations on the main thread for simplicity in tests.
            .build()
        val flashCardDao = db.flashCardDao()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://placeholder.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val networkService = retrofit.create(NetworkService::class.java)

        navController.navigatorProvider.addNavigator(ComposeNavigator())
        // Set UI
        composeTestRule.setContent {
            Navigator(
                navController,
                networkService,
                flashCardDao
            )

        }
        composeTestRule.onNodeWithText("Add Card").performClick()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").assertExists().performClick()


    }

    @Test
    fun homeScreen_whenStudyCardButtonClicked_navigatesToStudyCardScreen() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val navController = TestNavHostController(context)

        val db = Room.inMemoryDatabaseBuilder(context, AnNamDatabase::class.java)
            .allowMainThreadQueries() // Allow database operations on the main thread for simplicity in tests.
            .build()
        val flashCardDao = db.flashCardDao()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://placeholder.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val networkService = retrofit.create(NetworkService::class.java)

        navController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.setContent {
            Navigator(navController, networkService, flashCardDao)
        }
        composeTestRule.onNodeWithText("Study Cards").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithText("Study Cards").performClick()
        assertEquals("study_cards", navController.currentDestination?.route)

    }
    @Test
    fun changeMessage(){
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val navController = TestNavHostController(context)

        val db = Room.inMemoryDatabaseBuilder(context, AnNamDatabase::class.java)
            .allowMainThreadQueries() // Allow database operations on the main thread for simplicity in tests.
            .build()
        val flashCardDao = db.flashCardDao()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://placeholder.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val networkService = retrofit.create(NetworkService::class.java)

        navController.navigatorProvider.addNavigator(ComposeNavigator())
        composeTestRule.setContent {
            Navigator(navController,networkService,flashCardDao)
        }
        composeTestRule.runOnUiThread {
            navController.navigate("add_card")
        }
        composeTestRule.onNodeWithContentDescription("Message") //called in nav
            .assertExists()
            .assert(hasText("Please, add a flash card."))

    }
    //In the Home screen, a button should exist with the text "Add Card"
    @Test
    fun homeScreen_addCardButtonExist(){
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val navController = TestNavHostController(context)

        val db = Room.inMemoryDatabaseBuilder(context, AnNamDatabase::class.java)
            .allowMainThreadQueries() // Allow database operations on the main thread for simplicity in tests.
            .build()
        val flashCardDao = db.flashCardDao()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://placeholder.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val networkService = retrofit.create(NetworkService::class.java)

        navController.navigatorProvider.addNavigator(ComposeNavigator())
        composeTestRule.setContent {
            Navigator(navController,networkService,flashCardDao)
        }
        composeTestRule.onNodeWithText("Add Card").assertExists().assertIsDisplayed()



    }

}

