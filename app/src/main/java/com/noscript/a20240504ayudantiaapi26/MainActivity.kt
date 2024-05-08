package com.noscript.reproductor

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.noscript.reproductor.databinding.ActivityMainBinding
import com.noscript.reproductor.AppConstant.Companion.MEDIA_PLAYER_POSITION
import com.noscript.reproductor.AppConstant.Companion.LOG_MAIN_ACTIVITY
import java.io.IOException

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

        // Actualizar la UI con la canción actual
        updateUiSong()

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
    }


    /**
     * Método llamado cuando la actividad se vuelve visible para el usuario.
     */

    override fun onStart() {
        super.onStart()
        // Registro del inicio de la actividad
        Log.i("MainActivityReproductor", "onStart")
        // Agrega un mensaje a la cola de Toast
        agregarACola("onStart")
        // Muestra el siguiente mensaje de Toast en la cola
        mostrarSiguienteToast()
        // Inicia la reproducción de música
        if(isPlaying)
            mediaPlayer?.start()
        // Inicializa el MediaPlayer con el archivo de audio en res/raw
        mediaPlayer = MediaPlayer.create(this, currentSong.audioResId)

    }







    /**
     * Método llamado cuando la actividad vuelve a estar en primer plano.
     */
    override fun onResume() {
        super.onResume()
        // Registro de la reanudación de la actividad
        Log.i("MainActivityReproductor", "onResume")
        // Agrega un mensaje a la cola de Toast
        agregarACola("onResume")
        // Muestra el siguiente mensaje de Toast en la cola
        mostrarSiguienteToast()
        // Reanuda la reproducción de música
        mediaPlayer?.seekTo(position)

        if(isPlaying){
            mediaPlayer?.start()
            isPlaying=!isPlaying
        }
    }

    /**
     * Método llamado cuando otra actividad viene a primer plano.
     */
    override fun onPause() {
        super.onPause()
        // Pausa la reproducción de música cuando la actividad está en pausa
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
        // Registro del reinicio de la actividad
        Log.i("MainActivityReproductor", "onRestart")
        // Agrega un mensaje a la cola de Toast
        agregarACola("onRestart")
        // Muestra el siguiente mensaje de Toast en la cola
        mostrarSiguienteToast()
    }

    /**
     * Método llamado antes de que la actividad sea destruida.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Registro de la destrucción de la actividad
        Log.i("MainActivityReproductor", "onDestroy")
        // Agrega un mensaje a la cola de Toast
        agregarACola("onDestroy")
        // Muestra el siguiente mensaje de Toast en la cola
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
    private fun playOrNotPLayMusic() {
        if (isPlaying) {
            // Pausa la música si se está reproduciendo
            mediaPlayer?.pause()
            isPlaying = false
            agregarACola("Pausado")
        } else {
            // Reproduce la música si se está pausada
            mediaPlayer?.start()
            isPlaying = true
            agregarACola("Reproduciendo")
        }
        // Actualiza la vista del reproductor de música
        updateViewMediaPlayer()
    }

    /**
     * Actualiza la vista del reproductor de música.
     */
    private fun updateViewMediaPlayer() {
        if (isPlaying) {
            // Actualiza el texto del botón de reproducción y el título cuando se está reproduciendo música
            binding.playPauseButton.text = "Pause"
            binding.titleTextView.text = "Reproduciendo people are awesome"
        } else {
            // Actualiza el texto del botón de reproducción y el título cuando se está pausando la música
            binding.playPauseButton.text = "Play"
            binding.titleTextView.text = "Pausado people are awesome"

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MEDIA_PLAYER_POSITION, position)

    }

    private fun updateUiSong() {
        binding.titleTextView.text = currentSong.title
        binding.albumCoverImageView.setImageResource(currentSong.imageResId)
        updatePlayPauseButton()
    }

    private fun playOrPauseMusic() {
        if (isPlaying) {
            mediaPlayer?.pause()
        }
        isPlaying = !isPlaying
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
}
