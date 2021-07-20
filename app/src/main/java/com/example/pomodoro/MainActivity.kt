package com.example.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TimerListener {
    lateinit var binding: ActivityMainBinding
    private val timers = mutableListOf<Timer>()
    private var timerAdapter = TimerAdapter(this)
    private var nextId = 0
    private var currentMinutes: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(applicationContext)
            rcView.adapter = timerAdapter
            button.setOnClickListener {
            addTimer(currentMinutes)
            }
        }

    }

    private fun checkEditToOpenButton(minutes: String) {
        binding.apply {
            button.isEnabled =
                (edMinutes.text?.isNotEmpty() == true && button.error == null )
        }
        if (minutes.isNotEmpty()) {
            if (minutes.toInt() > MAX_TIME_MINUTES) {
                binding.edMinutes.error = NUMBER_EXCEEDS_RANGE
                binding.button.isEnabled = false
                return
            }
            binding.button.isEnabled = true
        }
        }


    private fun getInputTextWatcher(): TextWatcher {
        return object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val minutesString: String = binding.edMinutes.text.toString()
                checkEditToOpenButton(minutesString)
                currentMinutes = if(minutesString.isNotEmpty()) minutesString.toInt()
                else 0

            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun addTimer(minutes: Int) {
        val translateMs = (minutes * 60000).toLong()
        timers.add(Timer(nextId++, translateMs,false))
        timerAdapter.submitList(timers.toList())
    }

    override fun start(id: Int) {
        changeStopwatch(id, null, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        val newTimers = mutableListOf<Timer>()
        timers.forEach {
            if (it.id == id) {
                newTimers.add(Timer(it.id, currentMs ?: it.currentMs, isStarted))
            } else {
                newTimers.add(it)
            }
        }
        timerAdapter.submitList(newTimers)
        timers.clear()
        timers.addAll(newTimers)
    }
    private companion object {
        private const val MAX_TIME_MINUTES = 5999
        private const val NUMBER_EXCEEDS_RANGE = "Слишком большое число"
    }

}