package com.application.androidmusic

import android.media.Image
import android.media.MediaParser
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar

import android.net.Uri
import android.view.View
import android.widget.CompoundButton

import com.google.android.material.switchmaterial.SwitchMaterial


class MainActivity : AppCompatActivity() {

    private var _player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _player = MediaPlayer()
        val seekbar= findViewById<SeekBar>(R.id.seekbar)
        seekbar.progress=0
        seekbar.max= _player!!.duration

        val mediaFileUriStr = "android.resource://${packageName}/${R.raw.around_shinjuku}"
        val mediaFileUri = Uri.parse(mediaFileUriStr)

        _player?.let {
            it.setDataSource(this@MainActivity, mediaFileUri)
            it.setOnPreparedListener(PlayerPreparedListener())
            it.setOnCompletionListener(PlayerCompletionListener())
            it.prepareAsync()
        }

        val loopSwitch = findViewById<SwitchMaterial>(R.id.swLoop)
        loopSwitch.setOnCheckedChangeListener(LoopSwitchChangedListener())

        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if(changed){
                    _player!!.seekTo(pos)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }


    override fun onDestroy() {
        _player?.let {
            if(it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        _player = null
        super.onDestroy()
    }

    fun onPlayButtonClick(view: View) {
        _player?.let {
            val play_btn = findViewById<ImageButton>(R.id.play_btn)

            if(it.isPlaying) {
                it.pause()
                play_btn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                play_btn.setImageResource(R.drawable.btn_bg)
            }
            else {
                it.start()
                play_btn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                play_btn.setImageResource(R.drawable.btn_bg)
            }
        }
    }

    fun onBackButtonClick(view: View) {
        _player?.seekTo(0)
    }

    fun onForwardButtonClick(view: View) {
        _player?.let {

            val duration = it.duration
            it.seekTo(duration)

            if(!it.isPlaying) {
                it.start()
            }
        }
    }

    private inner class PlayerPreparedListener : MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer) {
            val play_btn = findViewById<ImageButton>(R.id.play_btn)
            play_btn.setImageResource(R.drawable.btn_bg)
            play_btn.isEnabled = true
            val next_btn = findViewById<ImageButton>(R.id.next_btn)
            next_btn.isEnabled = true
            val back_btn = findViewById<ImageButton>(R.id.back_btn)
            back_btn.isEnabled = true
        }
    }


    private inner class PlayerCompletionListener : MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer) {
            _player?.let {
                if(!it.isLooping) {
                    val play_btn = findViewById<ImageButton>(R.id.play_btn)
                    play_btn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                }
            }
        }
    }

    private inner class LoopSwitchChangedListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            _player?.isLooping = isChecked
        }
    }

}


