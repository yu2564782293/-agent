package com.solo.androidagent.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.solo.androidagent.R

private enum class Screen {
    Chat,
    Settings,
}

@Composable
fun AppRoot(vm: ChatViewModel, modifier: Modifier = Modifier) {
    var screen by remember { mutableStateOf(Screen.Chat) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = screen == Screen.Chat,
                    onClick = { screen = Screen.Chat },
                    icon = { Icon(painterResource(R.drawable.ic_chat), contentDescription = null) },
                    label = { Text("Chat") },
                )
                NavigationBarItem(
                    selected = screen == Screen.Settings,
                    onClick = { screen = Screen.Settings },
                    icon = {
                        Icon(
                            painterResource(R.drawable.ic_settings),
                            contentDescription = null,
                        )
                    },
                    label = { Text("Settings") },
                )
            }
        }
    ) { padding ->
        when (screen) {
            Screen.Chat -> ChatScreen(vm = vm, contentPadding = padding)
            Screen.Settings -> SettingsScreen(vm = vm, contentPadding = padding)
        }
    }
}

