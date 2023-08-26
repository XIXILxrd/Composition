package com.example.composition.domain.entity

data class GameResult(
    val isGameWon: Boolean,
    val countOfRightAnswers: Int,
    val countOfQuestions: Int,
    val gameSettings: GameSettings
) {
}