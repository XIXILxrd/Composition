package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding

class GameFinishedFragment : Fragment() {
    private val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun bindViews() {
        with(binding) {
            smileImageView.setImageResource(getSmileResId())

            requiredAnswersTextView.text = String.format(
                getString(R.string.required_score),
                args.gameResult.gameSettings.minCountOfRightAnswers
            )

            scoreAnswersTextView.text = String.format(
                getString(R.string.score_answers),
                args.gameResult.countOfRightAnswers
            )

            requiredPercentageTextView.text = String.format(
                getString(R.string.required_percentage),
                args.gameResult.gameSettings.minPercentOfRightAnswers
            )

            scorePercentageTextView.text = String.format(
                getString(R.string.score_percentage),
                getPercentOfRightAnswers()
            )
        }
    }

    private fun getPercentOfRightAnswers(): Int {
        if (args.gameResult.countOfQuestions == 0) {
            return 0
        }

        return ((args.gameResult.countOfRightAnswers / args.gameResult.countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun getSmileResId(): Int {
        return if (args.gameResult.isGameWon) {
            R.drawable.ic_funny_smile
        } else {
            R.drawable.ic_sad_smile
        }
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }
}