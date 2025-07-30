package ru.steps

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun MainScreen(

) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MyButton("Hello") {
                GlobalScope.launch {
                    ConnectToServer.sendMessage()
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
