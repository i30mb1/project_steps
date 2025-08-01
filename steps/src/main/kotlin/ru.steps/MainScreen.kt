package ru.steps

import kotlin.random.Random
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun MainScreen(server: Server) {
    val lifecycleOwner = LocalLifecycleOwner.current
    // Вынесено из тела composable с помощью remember
    val safeFlow = remember(server.state, lifecycleOwner) {
        server.state.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    val message by safeFlow.collectAsState(initial = "Ожидаю...")

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MyButton(message) {
                GlobalScope.launch {
                    HttpServer.sendSteps(Random.nextInt())
                }
            }
        }
    }
}

@Composable
fun MyButton(
    text: String,
    modifier: Modifier = Modifier
        .padding(16.dp)
        .height(56.dp)
        .width(200.dp),
    onClick: () -> Unit,
) {
    Button(onClick, modifier) {
        Text(text, fontSize = 18.sp)
    }
}
