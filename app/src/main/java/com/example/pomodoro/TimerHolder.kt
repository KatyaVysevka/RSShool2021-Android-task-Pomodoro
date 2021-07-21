package com.example.pomodoro

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.provider.Settings.Global.getString
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.TimerItemBinding

class TimerHolder(
    private val binding: TimerItemBinding,
    private val listener: TimerListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {
    private var timerZ: CountDownTimer? = null

    fun bind(timer: Timer) {
        binding.timer.text = timer.currentMs.displayTime()
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
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS
            override fun onTick(millisUntilFinished: Long) {
                timer.currentMs -= interval
                binding.customView.setCurrent(timer.startMs - timer.currentMs)
                binding.timer.text = timer.currentMs.displayTime()
                timer.forDifference = System.currentTimeMillis()

                if (timer.currentMs <= 0L) {
                    stopTimer(timer)
                    binding.customView.setCurrent(0L)
//                    binding.container.setBackgroundColor(getColor(binding.container.context, R.color.end_timer))
                    binding.bStart.isEnabled = false
                    listener.timerEnd(timer.id)
                }
            }

//           override fun onTick(millisUntilFinished: Long) {
//                timer.currentMs += interval
//                binding.timer.text = timer.currentMs.displayTime()
//            }

            override fun onFinish() {
                binding.timer.text = timer.currentMs.displayTime()
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
        private const val UNIT_TEN_MS = 1000L
        private const val PERIOD = 1000L * 60L * 60L * 24L
    }
}