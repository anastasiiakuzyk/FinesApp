package ua.anastasiia.finesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import dagger.hilt.android.AndroidEntryPoint
import ua.anastasiia.finesapp.ui.theme.FinesAppTheme

@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinesAppTheme {
                FinesApp()
            }
        }
    }
}
