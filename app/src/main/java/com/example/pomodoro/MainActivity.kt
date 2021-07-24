package com.example.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), TimerListener, LifecycleObserver {
    lateinit var binding: ActivityMainBinding
    private val timers = mutableListOf<Timer>()
    private var timerAdapter = TimerAdapter(this)
    private var nextId = 0
    private var currentMinutes: Int = 0
    private var idTimerStart = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(applicationContext)
            rcView.adapter = timerAdapter
            edMinutes.addTextChangedListener(getInputTextWatcher())
            button.setOnClickListener {
                if (edMinutes.text.isEmpty()) binding.edMinutes.error = "Введите число"
                else addTimer(currentMinutes)
            }
        }

    }

    private fun checkEditToOpenButton(minutes: String) {
        binding.apply {
            button.isEnabled =
                (edMinutes.text?.isNotEmpty() == true && button.error == null)
        }
        if (minutes.isNotEmpty()) {
            if (minutes.toInt() > MAX_TIME_MINUTES) {
                binding.edMinutes.error = "Слишком большое число"
                binding.button.isEnabled = false
                return
            }
            binding.button.isEnabled = true
        }
    }

    private fun getInputTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val minutesString: String = binding.edMinutes.text.toString()
                checkEditToOpenButton(minutesString)
                if (minutesString.isNotEmpty()) {
                    currentMinutes = minutesString.toInt()
                } else 0

            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun addTimer(minutes: Int) {
        val translateMs = (minutes * 60000).toLong()
        timers.add(Timer(nextId++, translateMs, false, translateMs, 0L))
        timerAdapter.submitList(timers.toList())

    }

    override fun start(id: Int) {
        if (idTimerStart != -1) {
            val oldTimer = timers.find { it.id == idTimerStart }
            changeStopwatch(idTimerStart, oldTimer?.currentMs ?: 0, false)
        }
        changeStopwatch(id, null, true)

        idTimerStart = id
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
        idTimerStart = -1
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    override fun timerEnd(id: Int) {
        idTimerStart = -1
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        val newTimers = mutableListOf<Timer>()
        timers.forEach {
            if (it.id == id) {
                newTimers.add(
                    Timer(
                        it.id,
                        currentMs ?: it.currentMs,
                        isStarted,
                        it.startMs,
                        it.forDifference
                    )
                )
            } else {
                newTimers.add(it)
            }
        }
        timerAdapter.submitList(newTimers)
        timers.clear()
        timers.addAll(newTimers)
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onAppBackgrounded() {
//        val currentTimer = timers.find { it.id == idTimerStart }
//
//        val startIntent = Intent(this, ForegroundService::class.java)
//        startIntent.putExtra(COMMAND_ID, COMMAND_START)
//
//        startIntent.putExtra(STARTED_TIMER_TIME_MS, currentTimer?.currentMs)
//        startService(startIntent)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onAppForegrounded() {
//        val stopIntent = Intent(this, ForegroundService::class.java)
//        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
//        startService(stopIntent)
//    }


    private companion object {
        private const val MAX_TIME_MINUTES = 5999
    }

}