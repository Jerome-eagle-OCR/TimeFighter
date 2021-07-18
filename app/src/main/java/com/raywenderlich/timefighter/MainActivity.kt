package com.raywenderlich.timefighter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.timefighter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var score = 0

    private lateinit var tapMeButton: Button
    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView

    private var gameStarted: Boolean = false

    private lateinit var countDownTimer: CountDownTimer
    private val initialCountDown: Long = 60000
    private val countDownInterval: Long = 1000
    private var timeLeftOnTimer: Long = 60000

    private lateinit var binding: ActivityMainBinding

    private companion object {
        private val TAG = MainActivity::class.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
        private const val GAME_STARTED_KEY = "GAME_STARTED_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called. Score is: $score")

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            gameStarted = savedInstanceState.getBoolean(GAME_STARTED_KEY)
        }

        initActivity()
    }

    private fun initActivity() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tapMeButton = binding.tapMeButton
        gameScoreTextView = binding.gameScoreTextView
        timeLeftTextView = binding.timeLeftTextView

        tapMeButton.setOnClickListener {
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            it.startAnimation(bounceAnimation)
            incrementScore()
        }

        setGame()
    }

    private fun setGame() {
        if (!gameStarted) {
            score = 0
            timeLeftOnTimer = initialCountDown
        }
        gameScoreTextView.text = getString(R.string.yourScore, score)

        val initialOrRestoredTimeLeft = timeLeftOnTimer / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, initialOrRestoredTimeLeft)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        // If game was running when activity was destroyed we start the count down timer
        if (gameStarted) countDownTimer.start()
    }

    private fun incrementScore() {
        if (!gameStarted) startGame()

        score++
        val newScore = getString(R.string.yourScore, score)
        gameScoreTextView.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        gameScoreTextView.startAnimation(blinkAnimation)
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    @SuppressLint("ShowToast")
    private fun endGame() {
        tapMeButton.isClickable = false
        gameStarted = false

        Snackbar.make(
            binding.root,
            getString(R.string.gameOverMessage, score),
            5000,
        ).setAnchorView(tapMeButton)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
            .setTextColor(ContextCompat.getColor(this, R.color.black))
            .show()

        setGame()

        Handler(Looper.getMainLooper()).postDelayed({
            tapMeButton.isClickable = true
        }, 5000)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        outState.putBoolean(GAME_STARTED_KEY, gameStarted)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: saving score: $score & time left: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy called.")
    }
}