package ru.dbuzin.dev.sbertestapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import ru.dbuzin.dev.sbertestapp.ui.feature.converter.ConverterScreen
import ru.dbuzin.dev.sbertestapp.ui.theme.SberTestAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SberTestAppTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    ConverterScreen()
}
