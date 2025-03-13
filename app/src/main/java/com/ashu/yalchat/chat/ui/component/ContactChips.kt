package com.ashu.yalchat.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ashu.yalchat.chat.data.User
import com.ashu.yalchat.login.DataProvider

@Composable
fun ContactChips(navHostController: NavHostController, users: List<User>) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Users",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable { expanded = !expanded }
        )

        val rowHeight by animateDpAsState(if (expanded) 56.dp else 0.dp, label = "")

        AnimatedVisibility(visible = expanded) {
            LazyRow(modifier = Modifier.height(rowHeight)) {
                items(users.size) { i ->
                    ContactChip(navHostController, users[i].displayName.trim(), users[i].userId, DataProvider.user?.displayName?.trim())
                }
            }
        }
    }
}