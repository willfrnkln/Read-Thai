package readthai.readthai

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.CursorIndexOutOfBoundsException
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.getSystemService
import com.google.android.flexbox.FlexboxLayout
import com.ibm.icu.text.BreakIterator
import com.ibm.icu.util.ULocale
import java.util.Locale


class FloatingWindow : Service() {

    private lateinit var floatView: FloatingLayout
    private var LAYOUT_TYPE: Int = 0
    private lateinit var floatWindowLayoutParam: WindowManager.LayoutParams
    private lateinit var windowManager: WindowManager
    private lateinit var  newBtn: Button
    private var TTS_ENABLED = true
    private var ttsService: TTSService? = null
    private var mConnection = object: ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder ) {
            Log.d("ServiceConnection","connected");
            val binder = binder as TTSService.LocalBinder
            ttsService = binder.getService()
        }

        override fun onServiceDisconnected(className: ComponentName ) {
            Log.d("ServiceConnection","disconnected");
        }
    }


    @Override
    override fun onBind(p0: Intent): IBinder?{
        Log.d("life", "onBInd")
        return null
    }

    @Override
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("life", "onStartCommand")
        var text =  intent?.extras?.getString("SELECTED_TEXT") ?: "null"
        addFlexBox(text)

        ttsService?.startTimer()

        return super.onStartCommand(intent, flags, startId)
    }
    @Override
    override fun onCreate() {
        super.onCreate()
        Log.d("life", "onCreate")

        createWindow()

        ttsService = TTSService()
        var started = startService(Intent(this, TTSService::class.java))
        var bound = bindService(Intent(this, TTSService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        Log.d("bind", "Started: ${started},  bound: ${bound}")

    }

    @Override
    override fun onDestroy(){
        Log.d("life", "onDestroy")

        //stopService(Intent(this, TTSService::class.java))
        unbindService(mConnection)
        stopSelf()
        super.onDestroy()



    }


    private fun tokenize(text: String): ArrayList<String> {
        val tokens = ArrayList<String>()
        val iter = BreakIterator.getWordInstance(ULocale.forLanguageTag("th"))
        iter.setText(text)
        var start = iter.first()

        while (iter.next() != BreakIterator.DONE) {
            var current = iter.current()
            tokens.add(text.substring(start, current))
            start = current
        }
        return tokens
    }

    private fun createWindow() {
        val displayMetrics = applicationContext.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        windowManager = getSystemService<WindowManager>() as WindowManager

        val inflater = baseContext.getSystemService<LayoutInflater>() as LayoutInflater
        floatView = inflater.inflate(R.layout.floating_window_layout, null) as FloatingLayout
        floatView.setListener {
            if (it) {
            } else {
                stopSelf()
                windowManager.removeView(floatView)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST
        }

        floatWindowLayoutParam = WindowManager.LayoutParams(
            width.toInt(),
            (height * 0.40f).toInt(),
            LAYOUT_TYPE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParam.gravity = Gravity.BOTTOM
        floatWindowLayoutParam.x = 0
        floatWindowLayoutParam.y = 0

        windowManager.addView(floatView, floatWindowLayoutParam)

        //check if needed
        floatView.isFocusable = true
    }

    private fun addFlexBox(text:String) {
        val tokens = tokenize(text)
        val flexbox:FlexboxLayout = floatView.findViewById(R.id.flexbox)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        for(i in 0..tokens.size-1){
            addTextBox(tokens.get(i), flexbox)
        }
    }

    private fun addTextBox(text: String, flexbox: FlexboxLayout) {
        val textbox = TextView(this)
        if(text.compareTo("\n") == 0) return
        textbox.text = ' ' + text + ' '
        if(text.compareTo(" ") != 0) {
            textbox.setOnClickListener {
                fillDefinitionBox(textbox.text.toString())
                if (TTS_ENABLED) ttsService!!.speakOut(text)
            }
            textbox.setBackgroundColor(
                resources.getColor(
                    R.color.dark_teal,
                    applicationContext.theme
                )
            )
            textbox.textSize = 25f
            textbox.setPadding(0, 0, 0, 0)
            textbox.background =
                resources.getDrawable(R.drawable.textbox_border, applicationContext.theme)
        }
        flexbox.addView(
            textbox
        )
    }
    private fun fillDefinitionBox(text: String) {
        val trimmed = text.trim()
        val box = floatView.findViewById(R.id.definitionBox) as TextView
        var def = "${trimmed}\nDefinition:"
        val dbh = DatabaseHelper.getInstance(applicationContext)
        val db = dbh.writableDatabase
        var cursor = db.rawQuery("SELECT * FROM entry WHERE thai = '"+ trimmed + "'", null)
        cursor.moveToFirst()
        try {
            def += '\n' + cursor.getString(cursor.getColumnIndexOrThrow("ipa"))
            def += '\n' + cursor.getString(cursor.getColumnIndexOrThrow("english"))
        } catch (e: IllegalArgumentException){
            def = "No definition for this word"
        } catch (e: CursorIndexOutOfBoundsException){
            def = "Word does not exist in dictionary"
        }
        box.text = def
        cursor.close()
        db.close()
    }
}