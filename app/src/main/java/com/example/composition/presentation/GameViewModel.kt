package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImplementation
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.useCases.GenerateQuestionUseCase
import com.example.composition.domain.useCases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {

    private val gameRepository = GameRepositoryImplementation

    private val generateQuestionUseCase = GenerateQuestionUseCase(gameRepository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(gameRepository)

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _isEnoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val isEnoughCountOfRightAnswers: LiveData<Boolean>
        get() = _isEnoughCountOfRightAnswers

    private val _isEnoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val isEnoughPercentOfRightAnswers: LiveData<Boolean>
        get() = _isEnoughPercentOfRightAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult


    private lateinit var gameSettings: GameSettings

    private var timer: CountDownTimer? = null

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    init {
        startGame()
    }

    private fun startGame() {
        getGameSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLISECONDS_IN_SECONDS,
            MILLISECONDS_IN_SECONDS
        ) {
            override fun onTick(p0: Long) {
                _formattedTime.value = formatTime(p0)
            }

            override fun onFinish() {
                finishGame()
            }
        }

        timer?.start()
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase.invoke(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLISECONDS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)

        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()

        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _isEnoughCountOfRightAnswers.value =
            countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _isEnoughPercentOfRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        if (countOfQuestions == 0) {
            return 0
        }

        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }


    private fun checkAnswer(number: Int) {
        val rightAnswer = _question.value?.rightAnswer

        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            isGameWon = _isEnoughPercentOfRightAnswers.value == true && _isEnoughCountOfRightAnswers.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLISECONDS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }

}