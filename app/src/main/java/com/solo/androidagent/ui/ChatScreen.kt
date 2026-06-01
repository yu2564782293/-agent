package com.solo.androidagent.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreen(vm: ChatViewModel, contentPadding: PaddingValues) {
    val state by vm.state.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.messages, key = { it.id }) { msg ->
                MessageCard(msg = msg)
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = input,
                onValueChange = { input = it },
                enabled = !state.isSending,
                label = { Text("Message") },
                maxLines = 4,
            )
            Button(
                enabled = !state.isSending,
                onClick = {
                    val text = input
                    input = ""
                    vm.send(text)
                },
            ) {
                Text(if (state.isSending) "..." else "Send")
            }
        }
    }
}

@Composable
private fun MessageCard(msg: UiMessage) {
    val title =
        when (msg.role) {
            UiRole.User -> "User"
            UiRole.Assistant -> "Assistant"
            UiRole.Tool -> "Tool"
            UiRole.System -> "System"
            UiRole.Error -> "Error"
        }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = msg.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

