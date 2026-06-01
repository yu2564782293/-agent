package com.solo.androidagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.solo.androidagent.ui.AppRoot
import com.solo.androidagent.ui.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: ChatViewModel = viewModel(factory = ChatViewModel.Factory(applicationContext))
            AppRoot(vm = vm)
        }
    }
}

