package com.pascal.floatingwidgetcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pascal.floating_compose_widget.FloatingWidgetCompose
import com.pascal.floating_compose_widget.FloatingWidgetConfig
import com.pascal.floating_compose_widget.SizeMode
import com.pascal.floatingwidgetcompose.ui.theme.FloatingWidgetComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FloatingWidgetComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SampleFloating(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SampleFloating(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    FloatingWidgetCompose.start(
        context = context,
        config = FloatingWidgetConfig(
            touchable = true,
            sizeMode = SizeMode.WRAP
        )
    ) {
        Surface(
            modifier = modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.Black
        ) {
            Text(
                "Floating Compose",
                modifier = Modifier.padding(16.dp),
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FloatingWidgetComposeTheme {
        SampleFloating()
    }
}