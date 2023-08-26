package com.example.composition.domain.useCases

import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.repository .GameRepository

class GetGameSettingsUseCase(private val gameRepository: GameRepository) {
    operator fun invoke(level: Level): GameSettings {
        return gameRepository.getGameSettings(level)
    }
}