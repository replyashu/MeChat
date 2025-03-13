package com.ashu.yalchat.chat.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ContactChip(navHostController: NavHostController, name: String, receiverId: String, sender: String?) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable {
                Log.d("naanna", name + "  " + sender)
                navHostController.navigate("chat/${receiverId}/${sender}/${name}")
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Person, contentDescription = "Contact", tint = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = name, color = Color.Black)
        }
    }
}