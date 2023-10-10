package readthai.readthai

import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class WindowLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!Settings.canDrawOverlays(this)){
            Toast.makeText(this, "Please enable display permissions first", Toast.LENGTH_SHORT)
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(intent)
        }
        else {
            var text: String
            val passedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
            when (passedText) {
                is String -> text = passedText
                else -> text = "Null"
            }

            val intent = Intent(this@WindowLauncherActivity, FloatingWindow::class.java)
            intent.putExtra("SELECTED_TEXT", passedText)
            startService(intent)

        }
        finish()
    }
}