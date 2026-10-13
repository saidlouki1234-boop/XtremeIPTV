
package com.xtreme.iptv
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xtreme.iptv.data.model.Channel
import com.xtreme.iptv.ui.viewmodel.MainViewModel
import com.xtreme.iptv.ui.screens.PlayerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val vm: MainViewModel = viewModel()
                var selectedChannel by remember { mutableStateOf<Channel?>(null) }
                var selectedCat by remember { mutableStateOf("All") }
                var showAdd by remember { mutableStateOf(false) }
                val channels by vm.channels.collectAsState()
                val cats by vm.categories.collectAsState()
                val playlists by vm.playlists.collectAsState()

                if (selectedChannel != null) {
                    PlayerScreen(url = selectedChannel!!.url)
                } else {
                    Scaffold(
                        topBar = { TopAppBar(title = { Text("Xtreme IPTV") }, actions = { Button(onClick = { showAdd = true }) { Text("+ Playlist") } }) }
                    ) { pad ->
                        Column(Modifier.padding(pad).padding(12.dp)) {
                            if (showAdd) {
                                AddPlaylistDialog(onDismiss = { showAdd = false }, vm = vm)
                            }
                            // Categories chip row
                            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(selected = selectedCat == "All", onClick = { selectedCat = "All" }, label = { Text("All") })
                                cats.take(6).forEach { c ->
                                    FilterChip(selected = selectedCat == c, onClick = { selectedCat = c }, label = { Text(c) })
                                }
                            }
                            val filtered = if (selectedCat == "All") channels else channels.filter { it.group == selectedCat }
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(filtered) { ch ->
                                    Card(onClick = { selectedChannel = ch }) {
                                        ListItem(headlineContent = { Text(ch.name) }, supportingContent = { Text("${'$'}{ch.group} • ${'$'}{ch.epgId}") })
                                    }
                                }
                                if (filtered.isEmpty()) {
                                    item { Text("لا توجد قنوات. أضف قائمة M3U قانونية أو حساب Xtream خاص بك. تأكد أنك تملك حقوق البث.") }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("قوائم محفوظة (persist): ${'$'}{playlists.size}", style = MaterialTheme.typography.labelLarge)
                            playlists.forEach { p -> Text("- ${'$'}{p.name} [${'$'}{p.type}]") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPlaylistDialog(onDismiss: () -> Unit, vm: com.xtreme.iptv.ui.viewmodel.MainViewModel) {
    var type by remember { mutableStateOf("M3U") }
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("إضافة قائمة") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row { Button(onClick = { type = "M3U" }) { Text("M3U") }; Spacer(Modifier.width(8.dp)); Button(onClick = { type = "XTREAM" }) { Text("Xtream") } }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم القائمة") })
                if (type == "M3U") {
                    OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("رابط M3U/M3U8 - يجب أن يكون قانوني") })
                } else {
                    OutlinedTextField(value = host, onValueChange = { host = it }, label = { Text("Host e.g https://my-legal-server.com:8080") })
                    OutlinedTextField(value = user, onValueChange = { user = it }, label = { Text("Username") })
                    OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (type == "M3U") vm.addM3U(name.ifEmpty { "My Playlist" }, url) else vm.addXtream(name.ifEmpty { "Xtream" }, host, user, pass)
                onDismiss()
            }) { Text("حفظ") }
        }, dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
