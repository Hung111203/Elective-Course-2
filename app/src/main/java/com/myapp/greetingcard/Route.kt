package com.myapp.greetingcard

import kotlinx.serialization.Serializable

//type-safe navigation route.

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