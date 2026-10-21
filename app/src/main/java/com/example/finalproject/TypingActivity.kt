package com.example.finalproject

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.finalproject.databinding.ActivityTypingBinding

class TypingActivity : AppCompatActivity() {
    lateinit var binding: ActivityTypingBinding
    lateinit var fm1: myFragment1
    lateinit var fm2: myFragment2
    lateinit var fm3: myFragment3
    lateinit var fm4: myFragment4


    lateinit var currentCoverFragment: Fragment
    lateinit var currentContentFragment: Fragment

    private var countDownTimer: CountDownTimer? = null
    private val totalTimeMs: Long = 30000
    private var timeElapsedMs: Long = 0


    private var isAppleMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTypingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerName = intent.getStringExtra("PLAYER_NAME") ?: "神秘挑戰者"
        val gameMode = intent.getStringExtra("GAME_MODE") ?: "香蕉模式(簡單)"

        Toast.makeText(this, "歡迎 $playerName！即將開始 $gameMode", Toast.LENGTH_LONG).show()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fm1 = myFragment1.newInstance("", "")
        fm2 = myFragment2.newInstance("", "")
        fm3 = myFragment3.newInstance("", "")
        fm4 = myFragment4.newInstance("", "")
        isAppleMode = gameMode.contains("蘋果")
        if (isAppleMode) {
            currentCoverFragment = fm3
            currentContentFragment = fm4
        } else {
            currentCoverFragment = fm1
            currentContentFragment = fm2
        }
        resetGameState()
        binding.button3.setOnClickListener {
            startGame()
        }
        binding.button4.setOnClickListener {
            endGame()
        }
        binding.button8.setOnClickListener {
            resetGameState()
        }
    }

    private fun resetGameState() {
        countDownTimer?.cancel()
        timeElapsedMs = 0

        binding.editTextTextMultiLine.text?.clear()
        binding.editTextTextMultiLine.isEnabled = false

        if (isAppleMode) {
            binding.textView7.visibility = View.VISIBLE
            binding.textView7.text = "計時: 30 秒"
            binding.textView8.visibility = View.INVISIBLE
        } else {
            binding.textView7.visibility = View.INVISIBLE
            binding.textView8.visibility = View.INVISIBLE

        }
        binding.button3.visibility = View.VISIBLE
        binding.button4.visibility = View.INVISIBLE
        binding.button8.visibility = View.INVISIBLE

        hideKeyboard()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, currentCoverFragment)
            .commit()
    }
    private fun startGame() {

        binding.button3.visibility = View.INVISIBLE
        binding.button4.visibility = View.VISIBLE
        binding.button8.visibility = View.VISIBLE


        binding.editTextTextMultiLine.text?.clear()
        binding.editTextTextMultiLine.isEnabled = true

        showKeyboard()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, currentContentFragment)
            .commit()

        if (isAppleMode) {
            countDownTimer = object : CountDownTimer(totalTimeMs, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeElapsedMs = totalTimeMs - millisUntilFinished
                    binding.textView7.text = "倒數: ${millisUntilFinished / 1000} 秒"
                }

                override fun onFinish() {
                    timeElapsedMs = totalTimeMs
                    endGame()
                }

            }.start()
        }

    }

    private fun endGame() {
        countDownTimer?.cancel()

        binding.editTextTextMultiLine.isEnabled = false
        hideKeyboard()

        binding.button3.visibility = View.INVISIBLE
        binding.button4.visibility = View.VISIBLE
        binding.button8.visibility = View.VISIBLE

        if (isAppleMode) {
            binding.textView7.text = "時間到！"

            val typedText = binding.editTextTextMultiLine.text.toString()
            val charCount = typedText.length

            val timeInSeconds = timeElapsedMs / 1000.0
            val timeInMinutes = if (timeInSeconds > 0) timeInSeconds / 60.0 else 0.01
            val wpm = (charCount / 5.0) / timeInMinutes

            binding.textView8.text = String.format("結果: WPM = %.1f\n(總字元數: %d, 耗時: %.1f 秒)", wpm, charCount, timeInSeconds)
            binding.textView8.visibility = View.VISIBLE
        }
        else {

            binding.textView8.text = "練習結束"
            binding.textView8.visibility = View.VISIBLE
        }
    }
    private fun showKeyboard() {
        binding.editTextTextMultiLine.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.editTextTextMultiLine, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextTextMultiLine.windowToken, 0)
    }
}