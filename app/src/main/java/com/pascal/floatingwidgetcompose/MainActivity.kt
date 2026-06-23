package com.pascal.floatingwidgetcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
                FloatingWidgetDemoScreen()
            }
        }
    }
}

@Composable
private fun FloatingWidgetDemoScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasOverlayPermission by remember {
        mutableStateOf(FloatingWidgetCompose.canDrawOverlays(context))
    }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var selectedSample by rememberSaveable { mutableStateOf(FloatingSample.Draggable) }
    var fullScreenDragLayer by rememberSaveable { mutableStateOf(true) }
    var touchable by rememberSaveable { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasOverlayPermission = FloatingWidgetCompose.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderCard(
                hasOverlayPermission = hasOverlayPermission,
                isRunning = isRunning
            )

            ControlCard(
                selectedSample = selectedSample,
                onSampleSelected = { selectedSample = it },
                fullScreenDragLayer = fullScreenDragLayer,
                onFullScreenDragLayerChange = { fullScreenDragLayer = it },
                touchable = touchable,
                onTouchableChange = { touchable = it }
            )

            ActionButtons(
                hasOverlayPermission = hasOverlayPermission,
                isRunning = isRunning,
                onRequestPermission = {
                    FloatingWidgetCompose.openOverlayPermissionSettings(context)
                },
                onStart = {
                    val started = FloatingWidgetCompose.start(
                        context = context,
                        config = FloatingWidgetConfig(
                            touchable = touchable,
                            sizeMode = if (fullScreenDragLayer) SizeMode.FULL else SizeMode.WRAP,
                            startX = 24,
                            startY = 160
                        )
                    ) {
                        FloatingSampleContent(
                            sample = selectedSample,
                            fullScreenDragLayer = fullScreenDragLayer
                        )
                    }
                    hasOverlayPermission = FloatingWidgetCompose.canDrawOverlays(context)
                    isRunning = started
                },
                onStop = {
                    FloatingWidgetCompose.stop(context)
                    isRunning = false
                }
            )

            TipsCard()
        }
    }
}

@Composable
private fun HeaderCard(
    hasOverlayPermission: Boolean,
    isRunning: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Floating Widget Compose",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "A safer overlay sample with explicit start and stop controls, switchable widget styles, and no auto-start during recomposition.",
                style = MaterialTheme.typography.bodyMedium
            )
            StatusRow(
                label = "Overlay permission",
                value = if (hasOverlayPermission) "Granted" else "Not granted"
            )
            StatusRow(
                label = "Service",
                value = if (isRunning) "Running" else "Stopped"
            )
        }
    }
}

@Composable
private fun ControlCard(
    selectedSample: FloatingSample,
    onSampleSelected: (FloatingSample) -> Unit,
    fullScreenDragLayer: Boolean,
    onFullScreenDragLayerChange: (Boolean) -> Unit,
    touchable: Boolean,
    onTouchableChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Choose a widget sample",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingSample.entries.forEach { sample ->
                    FilterChip(
                        selected = selectedSample == sample,
                        onClick = { onSampleSelected(sample) },
                        label = { Text(sample.title) }
                    )
                }
            }
            ToggleRow(
                title = "Full drag layer",
                description = "Use a full-screen overlay area for draggable widgets.",
                checked = fullScreenDragLayer,
                onCheckedChange = onFullScreenDragLayerChange
            )
            ToggleRow(
                title = "Touchable",
                description = "Turn this off when the overlay is display-only and touches should pass through.",
                checked = touchable,
                onCheckedChange = onTouchableChange
            )
        }
    }
}

@Composable
private fun ActionButtons(
    hasOverlayPermission: Boolean,
    isRunning: Boolean,
    onRequestPermission: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!hasOverlayPermission) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRequestPermission
            ) {
                Text("Grant Permission")
            }

            Spacer(Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                enabled = hasOverlayPermission && !isRunning,
                onClick = onStart
            ) {
                Text("Start Floating")
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = isRunning,
                onClick = onStop
            ) {
                Text("Stop")
            }
        }
    }
}

@Composable
private fun TipsCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Safety notes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text("The service is started only from a button click, so Compose recomposition does not create duplicate overlays.")
            Text("If permission is revoked or Android recreates the service without content, the service stops safely.")
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun FloatingSampleContent(
    sample: FloatingSample,
    fullScreenDragLayer: Boolean
) {
    if (fullScreenDragLayer) {
        DraggableFloatingContainer {
            FloatingWidgetCard(sample = sample)
        }
    } else {
        FloatingWidgetCard(
            modifier = Modifier.padding(16.dp),
            sample = sample
        )
    }
}

@Composable
private fun DraggableFloatingContainer(content: @Composable () -> Unit) {
    var offsetX by remember { mutableFloatStateOf(24f) }
    var offsetY by remember { mutableFloatStateOf(160f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Box(
            modifier = Modifier.offset {
                IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
            }
        ) {
            content()
        }
    }
}

@Composable
private fun FloatingWidgetCard(
    sample: FloatingSample,
    modifier: Modifier = Modifier
) {
    when (sample) {
        FloatingSample.Draggable -> DraggableSampleCard(modifier)
        FloatingSample.Counter -> CounterSampleCard(modifier)
        FloatingSample.QuickAction -> QuickActionSampleCard(modifier)
    }
}

@Composable
private fun DraggableSampleCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .width(180.dp)
            .border(1.dp, Color(0x33000000), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF111827),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Drag me", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                text = "Drag from anywhere on the overlay area.",
                color = Color(0xFFD1D5DB),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CounterSampleCard(modifier: Modifier = Modifier) {
    var count by remember { mutableIntStateOf(0) }

    Surface(
        modifier = modifier.width(190.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F766E),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Counter", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                text = count.toString(),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { count++ }) {
                Text("Add")
            }
        }
    }
}

@Composable
private fun QuickActionSampleCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.width(220.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                Text("FW", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Quick Action", fontWeight = FontWeight.Bold)
                Text(
                    text = "A compact shortcut widget.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4B5563)
                )
            }
        }
    }
}

private enum class FloatingSample(val title: String) {
    Draggable("Draggable"),
    Counter("Counter"),
    QuickAction("Quick Action")
}

@Preview(showBackground = true)
@Composable
private fun FloatingWidgetDemoPreview() {
    FloatingWidgetComposeTheme {
        Box(modifier = Modifier.height(720.dp)) {
            FloatingWidgetDemoScreen()
        }
    }
}