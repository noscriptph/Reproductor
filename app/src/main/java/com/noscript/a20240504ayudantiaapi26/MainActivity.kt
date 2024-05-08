package com.noscript.reproductor

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.noscript.reproductor.AppConstant.Companion.LOG_MAIN_ACTIVITY
import com.noscript.reproductor.databinding.ActivityMainBinding
import com.noscript.reproductor.AppConstant.Companion.MEDIA_PLAYER_POSITION


import java.util.LinkedList
import java.util.Queue

/**
 * Esta clase representa la actividad principal de la aplicación.
 * Controla la reproducción de música y muestra mensajes de toast.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Cola para mantener los mensajes de Toast en espera.
     */
    private val colaToast: Queue<String> = LinkedList()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityMainBinding
    var isPlaying: Boolean = false
    private var position: Int = 0
    private lateinit var currentSong: Song
    private var currentSongIndex: Int = 0

    /**
     * Método llamado cuando se crea la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización del objeto currentSong
        currentSong = AppConstant.songs[currentSongIndex]

        // Restaurar posición del MediaPlayer si hay un estado previo guardado
        savedInstanceState?.let {
            position = it.getInt(AppConstant.MEDIA_PLAYER_POSITION)
        }

        // Configuración del listener del botón de reproducción/pausa
        binding.playPauseButton.setOnClickListener {
            playOrPauseMusic() // Lógica para reproducir o pausar la música
        }

        // Configuración del listener del botón de siguiente canción
        binding.btnSiguiente.setOnClickListener {
            playNextSong() // Lógica para reproducir la siguiente canción
        }

        binding.btnAnterior.setOnClickListener {
            playPreviousSong() // Lógica para reproducir la siguiente canción
        }

        // Inicializa el MediaPlayer con el archivo de audio de la primera canción
        mediaPlayer = MediaPlayer.create(this, currentSong.audioResId)

        // Configuración del listener de la ProgressBar
        setOnSeekBarChangeListener()
    }


    /**
     * Método llamado cuando la actividad se vuelve visible para el usuario.
     */
    override fun onStart() {
        super.onStart()
        Log.i(LOG_MAIN_ACTIVITY, "onStart")
        agregarACola("onStart")
        mostrarSiguienteToast()
        if (isPlaying)
            mediaPlayer?.start()
        mediaPlayer = MediaPlayer.create(this, currentSong.audioResId)
    }

    /**
     * Método llamado cuando la actividad vuelve a estar en primer plano.
     */
    override fun onResume() {
        super.onResume()
        Log.i(LOG_MAIN_ACTIVITY, "onResume")
        agregarACola("onResume")
        mostrarSiguienteToast()
        mediaPlayer?.seekTo(position)
        if (isPlaying) {
            mediaPlayer?.start()
            isPlaying = !isPlaying
        }
        updateProgressBar()
    }

    /**
     * Método llamado cuando otra actividad viene a primer plano.
     */

    override fun onPause() {
        super.onPause()
        isPlaying = false
        if (mediaPlayer != null) {
            position = mediaPlayer?.currentPosition!!
        }
    }

    /**
     * Método llamado cuando la actividad ya no es visible para el usuario.
     */
    override fun onStop() {
        // Detiene la reproducción de música cuando la actividad se detiene
        isPlaying = false
        Log.i("MainActivityReproductor", "onStop")
        // Agrega un mensaje a la cola de Toast
        agregarACola("onStop")
        // Muestra el siguiente mensaje de Toast en la cola
        mostrarSiguienteToast()
        super.onStop()
    }

    /**
     * Método llamado cuando la actividad vuelve a estar en primer plano
     * después de haber estado detenida.
     */
    override fun onRestart() {
        super.onRestart()
        Log.i(LOG_MAIN_ACTIVITY, "onRestart")
        agregarACola("onRestart")
        mostrarSiguienteToast()
    }

    /**
     * Método llamado antes de que la actividad sea destruida.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_MAIN_ACTIVITY, "onDestroy")
        agregarACola("onDestroy")
        mostrarSiguienteToast()
    }

    /**
     * Agrega un mensaje a la cola de Toast.
     *
     * @param mensaje Mensaje a agregar.
     */
    private fun agregarACola(mensaje: String) {
        colaToast.add(mensaje)
    }

    /**
     * Muestra el siguiente mensaje de Toast en la cola.
     */
    private fun mostrarSiguienteToast() {
        if (!colaToast.isEmpty()) {
            Toast.makeText(this, colaToast.poll(), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que gestiona la reproducción o pausa de música.
     */
    private fun playOrPauseMusic() {
        if (isPlaying) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        isPlaying = !isPlaying
        updateUiSong()
    }

    /**
     * Actualiza la vista del reproductor de música.
     */


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MEDIA_PLAYER_POSITION, position)

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
        mediaPlayer=MediaPlayer.create(this, currentSong.audioResId)
        mediaPlayer?.start()
        isPlaying=true
        updateUiSong()
    }
    private fun playPreviousSong(){
        // Algoritmo para obtener el indice y hacer una lista circular
        //cancion anterior - tamaño lista de canciones pra que siempre sea positivo
        //% devuelve un número positico si el dividendo es negativo
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

}

private fun setOnSeekBarChangeListener() {

}
