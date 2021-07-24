package com.example.pomodoro

import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.TimerItemBinding

class TimerHolder(
    private val binding: TimerItemBinding,
    private val listener: TimerListener,


    ) : RecyclerView.ViewHolder(binding.root) {
    private var timerZ: CountDownTimer? = null

    fun bind(timer: Timer) {
        binding.customView.setPeriod(timer.startMs)
        binding.customView.setCurrent(timer.startMs - timer.currentMs)
        binding.timer.text = timer.currentMs.displayTime()
        if (timer.currentMs == -1L) {
            binding.cardView.setBackgroundColor(
                ContextCompat
                    .getColor(binding.cardView.context, R.color.timerEndColor)
            )
        } else {
            binding.cardView.setBackgroundColor(
                ContextCompat
                    .getColor(binding.cardView.context, R.color.white)
            )
        }
        if (timer.isStarted) {
            startTimer(timer)
            binding.bStart.text = "Stop"
        } else {
            stopTimer(timer)
            binding.bStart.text = "Start"
        }

        initButtonsListeners(timer)
    }

    private fun initButtonsListeners(timer: Timer) {
        binding.bStart.setOnClickListener {
            if (timer.isStarted) {
                listener.stop(timer.id, timer.currentMs)
            } else {
                listener.start(timer.id)
            }
        }

        binding.imDelete.setOnClickListener { listener.delete(timer.id) }
    }

    private fun startTimer(timer: Timer) {
//        val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)
//        binding.startPauseButton.setImageDrawable(drawable)

        timerZ?.cancel()
        timerZ = getCountDownTimer(timer)
        timerZ?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(timer: Timer) {
//        val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
//        binding.startPauseButton.setImageDrawable(drawable)
        timerZ?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(timer: Timer): CountDownTimer {
        return object : CountDownTimer(PERIOD, INTERVAL) {
            val interval = INTERVAL
            override fun onTick(millisUntilFinished: Long) {
                timer.forDifference = System.currentTimeMillis()
                timer.currentMs -= interval
                binding.customView.setCurrent(timer.startMs - timer.currentMs)
                binding.timer.text = timer.currentMs.displayTime()
                timer.forDifference = System.currentTimeMillis()

                if (timer.currentMs <= 0L) {
                    stopTimer(timer)
                    binding.customView.setCurrent(0L)
                    timer.currentMs = timer.startMs
                    timer.forDifference = 0L
                    timer.currentMs = -1L
                    binding.cardView.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.cardView.context,
                            R.color.timerEndColor
                        )
                    )
                    listener.timerEnd(timer.id)
                }
            }

            override fun onFinish() {
                binding.timer.text = timer.currentMs.displayTime()
                binding.customView.setCurrent(0L)

            }
        }
    }


    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00"
        private const val INTERVAL = 1000L
        private const val PERIOD = 1000L * 60L * 60L * 24L
    }
}