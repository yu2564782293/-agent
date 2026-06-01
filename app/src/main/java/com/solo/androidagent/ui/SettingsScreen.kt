package com.solo.androidagent.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(vm: ChatViewModel, contentPadding: PaddingValues) {
    val state by vm.state.collectAsState()
    val current = state.settings

    var baseUrl by remember(current.baseUrl) { mutableStateOf(current.baseUrl) }
    var apiKey by remember(current.apiKey) { mutableStateOf(current.apiKey) }
    var model by remember(current.model) { mutableStateOf(current.model) }
    var systemPrompt by remember(current.systemPrompt) { mutableStateOf(current.systemPrompt) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL") },
            singleLine = true,
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = model,
            onValueChange = { model = it },
            label = { Text("Model") },
            singleLine = true,
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = systemPrompt,
            onValueChange = { systemPrompt = it },
            label = { Text("System Prompt") },
            minLines = 3,
        )

        Button(
            onClick = {
                vm.saveSettings(
                    UiSettings(
                        baseUrl = baseUrl.trim(),
                        apiKey = apiKey.trim(),
                        model = model.trim(),
                        systemPrompt = systemPrompt,
                    )
                )
            },
        ) {
            Text("Save")
        }
    }
}

