package readthai.readthai

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.SynthesisCallback
import android.speech.tts.SynthesisRequest
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale

class TTSService : Service(), OnInitListener {

    private val localBinder = LocalBinder()
    var tts: TextToSpeech? = null
    private val TIMEOUT = 3000000

    private val handler = Handler(Looper.getMainLooper())
    private val stopSelfRunnable: Runnable = Runnable { stopSelf() }


    @Override
    override fun onCreate() {
        tts = TextToSpeech(this, this)
        startForeground()
        startTimer()
        Log.d("tts", "tts onCreate")
        super.onCreate()
    }
    private fun startForeground() {
        val channel = getChannel()
        var builder = NotificationCompat.Builder(this, "30")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("TTS")
            .setContentText("TTSContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        startForeground(30, builder.build())
    }

    private fun getChannel() {
        val name = "Channel name"
        val descriptionText ="Channel desc"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("30 ", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    @Override
    override fun onInit(status: Int) {
        Log.d("tts", "onInit")
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale("th"))
            Log.d("tts", "Init success")
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","Thai is not supported")
            }
        }
        else {
            Log.d("tts", "Init failed")
        }
    }

    @Override
    override fun onDestroy() {
        tts!!.stop()
        tts!!.shutdown()
        super.onDestroy()
    }
    inner class LocalBinder : Binder() {
        fun getService(): TTSService {
            Log.d("tts", "getService")
            return this@TTSService
        }
    }

    @Override
    override fun onBind(p0: Intent): IBinder {
        Log.d("tts", "bound")
        return localBinder
    }


    fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    fun startTimer(){
        handler.removeCallbacks(stopSelfRunnable)
        Log.d("tts", "Starting timer")
        handler.postDelayed({
            Log.d("tts", "Time out, closing")
            stopSelf()
        }, TIMEOUT.toLong())
    }
}