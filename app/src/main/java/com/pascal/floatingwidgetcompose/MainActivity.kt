package com.pascal.floatingwidgetcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pascal.floating_compose_widget.FloatingWidgetCompose
import com.pascal.floating_compose_widget.FloatingWidgetConfig
import com.pascal.floating_compose_widget.SizeMode
import com.pascal.floatingwidgetcompose.ui.theme.FloatingWidgetComposeTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FloatingWidgetComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SampleFloatingFullDraggable(
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

@Composable
fun SampleFloatingFullDraggable(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    FloatingWidgetCompose.start(
        context = context,
        config = FloatingWidgetConfig(
            touchable = true,
            sizeMode = SizeMode.FULL
        )
    ) {
        DraggableFloatingBox(modifier)
    }
}

@Composable
private fun DraggableFloatingBox(
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Surface(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                }
                .padding(16.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Text(
                text = "Drag Me",
                modifier = Modifier.padding(16.dp),
                color = Color.Black
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