package com.example.composition.presentation

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.*
import com.example.composition.R
import com.example.composition.data.GameRepositoryImplementation
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    private val repository = GameRepositoryImplementation

    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel::class.java]
    }

    private val optionsTextView by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }
    private lateinit var level: Level
    private lateinit var gameSettings: GameSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseArguments()

        gameSettings = repository.getGameSettings(level)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setClickListenersToOptions()
        viewModel.startGame(level)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun setClickListenersToOptions() {
        for (optionTextView in optionsTextView) {
            optionTextView.setOnClickListener {
                viewModel.chooseAnswer(optionTextView.text.toString().toInt())
            }
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            question.observe(viewLifecycleOwner) {
                binding.sumTextView.text = it.sum.toString()
                binding.visibleNumberTextView.text = it.visibleNumber.toString()

                for (i in 0 until optionsTextView.size) {
                    optionsTextView[i].text = it.options[i].toString()
                }
            }

            percentOfRightAnswers.observe(viewLifecycleOwner) {
                binding.progressBar.setProgress(it, true)
            }

            isEnoughCountOfRightAnswers.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.setTextColor(getColorByState(it))
            }

            isEnoughPercentOfRightAnswers.observe(viewLifecycleOwner) {
                val color = getColorByState(it)

                binding.progressBar.progressTintList = ColorStateList.valueOf(color)
            }

            formattedTime.observe(viewLifecycleOwner) {
                binding.timerTextView.text = it
            }

            minPercent.observe(viewLifecycleOwner) {
                binding.progressBar.secondaryProgress = it
            }

            gameResult.observe(viewLifecycleOwner) {
                launchGameFinishedFragment(it)
            }

            progressAnswers.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.text = it
            }
        }
    }

    private fun getColorByState(goodState: Boolean): Int {
        val colorResId = if (goodState) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }

        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    @Suppress("DEPRECATION")
    private fun parseArguments() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(KEY_LEVEL, Level::class.java)?.let {
                level = it
            }
        } else {
            requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
                level = it
            }
        }
    }

    companion object {
        private const val KEY_LEVEL = "level"
        const val NAME = "GameFragment"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }
}