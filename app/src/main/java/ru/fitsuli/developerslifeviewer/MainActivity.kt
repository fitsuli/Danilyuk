package ru.fitsuli.developerslifeviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.fitsuli.developerslifeviewer.screens.MainScreen
import ru.fitsuli.developerslifeviewer.ui.theme.DevelopersLifeViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevelopersLifeViewerTheme {
                val systemUiController = rememberSystemUiController()
                val backgroundColor = MaterialTheme.colorScheme.background

                SideEffect {
                    systemUiController.setSystemBarsColor(backgroundColor)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DevelopersLifeViewerTheme {
        MainScreen()
    }
}