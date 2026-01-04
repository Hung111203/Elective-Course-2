package com.myapp.greetingcard

import kotlinx.serialization.Serializable

// This data class IS the type-safe navigation route.
// It says: to go to this destination, you MUST provide an integer `cardId`.

@Serializable
data class ShowCard(
    val cardId: Int
)
@Serializable
object LoginRoute

@Serializable
object HomeRoute

@Serializable
object AddCardRoute

@Serializable
object StudyCardsRoute

@Serializable
object SearchCardsRoute
@Serializable
data class TokenRoute(
    val email: String
)