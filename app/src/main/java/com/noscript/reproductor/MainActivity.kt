package com.noscript.reproductor

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.noscript.reproductor.databinding.ActivityMainBinding

import android.os.Handler
import android.os.PersistableBundle
import android.widget.SeekBar

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityMainBinding
    private var isPlaying: Boolean = false
    private var position: Int = 0
    private lateinit var currentSong: Song
    private var currentSongIndex: Int = 0
    private val handler = Handler()

    /*private val updateProgressAction = object : Runnable {
        override fun run() {
            updateProgressBar()
            handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL)
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentSong = AppConstant.songs[currentSongIndex]

        savedInstanceState?.let {
            position = it.getInt(AppConstant.MEDIA_PLAYER_POSITION)
        }

        binding.playPauseButton.setOnClickListener {
            playOrPauseMusic()
        }

        binding.btnSiguiente.setOnClickListener {
            playNextSong()
        }

        binding.btnAnterior.setOnClickListener {
            playPreviousSong()
        }

        mediaPlayer = MediaPlayer.create(this, currentSong.audioResId)

       // setOnSeekBarChangeListener()

    }

    override fun onStart() {
        super.onStart()
        //handler.post(updateProgressAction)

    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.seekTo(position)
        if (isPlaying) {
            mediaPlayer?.start()
            isPlaying = !isPlaying
        }
        updateProgressBar()
    }

    override fun onPause() {
        super.onPause()
        isPlaying = false
        position = mediaPlayer?.currentPosition ?: 0
    }

    override fun onStop() {
        isPlaying = false
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        updateUiSong()
        updatePlayPauseButton()
    }

    private fun playOrPauseMusic() {
        if (isPlaying) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        isPlaying = !isPlaying
        updateUiSong()
    }

    private fun updateUiSong() {
        binding.titleTextView.text = currentSong.title
        binding.albumCoverImageView.setImageResource(currentSong.imageResId)
        updatePlayPauseButton()
    }

    private fun updatePlayPauseButton() {
        binding.playPauseButton.text = if (isPlaying) "Pausa" else "Reproducir"
    }

    private fun playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % AppConstant.songs.size
        currentSong = AppConstant.songs[currentSongIndex]
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, currentSong.audioResId)
        mediaPlayer?.start()
        isPlaying = true
        updateUiSong()
    }

    private fun playPreviousSong() {
        currentSongIndex = (currentSongIndex - 1 + AppConstant.songs.size) % AppConstant.songs.size
        currentSong = AppConstant.songs[currentSongIndex]
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, currentSong.audioResId)
        mediaPlayer?.start()
        isPlaying = true
        updateUiSong()
    }

    private fun updateProgressBar() {
        val totalDuration = mediaPlayer?.duration ?: 0
        val currentPosition = mediaPlayer?.currentPosition ?: 0
        binding.progressBar.max = totalDuration
        binding.progressBar.progress = currentPosition
    }

    private fun seekTo(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }

    /* private fun setOnSeekBarChangeListener() {

         // Configurar el listener de la ProgressBar
         binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener) {
             override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                 // Manejar el cambio de progreso en la ProgressBar
                 if (fromUser) {
                     // El cambio de progreso fue causado por el usuario, por lo que se debe cambiar la posición de reproducción
                     seekTo(progress)
                 }
             }

             override fun onStartTrackingTouch(seekBar: SeekBar?) {
                 // No es necesario implementar nada aquí
             }

             override fun onStopTrackingTouch(seekBar: SeekBar?) {
                 // No es necesario implementar nada aquí
             }
         }
     }

     companion object {
         private const val PROGRESS_UPDATE_INTERVAL = 1000L // Intervalo de actualización de la ProgressBar (en milisegundos)
     }
}*/



}


