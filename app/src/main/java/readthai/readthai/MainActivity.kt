package readthai.readthai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ibm.icu.text.BreakIterator
import com.ibm.icu.util.ULocale
import readthai.readthai.ui.theme.ReadThaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!DatabaseHelper.doesDatabaseExist(this)){
            var dbh = DatabaseHelper.getInstance(this)
            Log.d("DB", "Copying database")
        }
        setContent {
            ReadThaiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        if (!Settings.canDrawOverlays(this@MainActivity)) {
                            Text(text = "Please press the button below and enable the \"Draw over other apps\" permission for the Read Thai app")
                            Button(onClick = { goToPermissions() }) { }
                        } else {
                            Text(text = "To use this app, just highlight some text and select \"Read Thai\" from the pop-up menu")
                        }
                    }
                }
            }
        }
    }
    private fun goToPermissions() {
        if(Settings.canDrawOverlays(this))
            Toast.makeText(this, "Permission already granted",
                Toast.LENGTH_SHORT).show()
        else {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(intent)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    SelectionContainer {

    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ReadThaiTheme {
    }
}