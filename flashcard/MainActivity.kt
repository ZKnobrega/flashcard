package com.example.flashcard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.theme.FlashcardTheme
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showDeckDialog by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf("") }
    val decks = remember { mutableStateListOf<Deck>() }
    var selectedDeck by remember { mutableStateOf<Deck?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { showDeckDialog = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        if (selectedDeck != null) {
            DeckDetailScreen(deck = selectedDeck!!, onBack = { selectedDeck = null })
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(decks) { deck ->
                        DeckItem(
                            deck = deck,
                            onDeckClick = { selectedDeck = deck },
                            onDelete = { decks.remove(deck) },
                            onRename = { newName -> deck.name = newName }
                        )
                    }
                }

                if (showDeckDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeckDialog = false },
                        title = { Text(text = "Criar Novo Baralho") },
                        text = {
                            OutlinedTextField(
                                value = deckName,
                                onValueChange = { deckName = it },
                                label = { Text("Nome do Baralho") }
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (deckName.isNotEmpty()) {
                                    decks.add(Deck(deckName, mutableStateListOf()))
                                    deckName = ""
                                    showDeckDialog = false
                                }
                            }) {
                                Text("Criar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeckDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DeckItem(deck: Deck, onDeckClick: () -> Unit, onDelete: () -> Unit, onRename: (String) -> Unit) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(deck.name) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onDeckClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = deck.name, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { showRenameDialog = true }) {
                Text("✏️") // Ícone para renomear
            }
            IconButton(onClick = { onDelete() }) {
                Text("🗑️") // Ícone para deletar
            }
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Renomear Baralho") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Novo Nome") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onRename(newName) // Use o callback para renomear
                    showRenameDialog = false
                }) {
                    Text("Renomear")
                }
            },
            dismissButton = {
                Button(onClick = { showRenameDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DeckDetailScreen(deck: Deck, onBack: () -> Unit) {
    var showCardDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var flashcardToEdit by remember { mutableStateOf<Pair<String, String>?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchField by remember { mutableStateOf(false) }

    // Nova variável para ativar/desativar a revisão
    var isReviewMode by remember { mutableStateOf(false) }

    val filteredFlashcards = deck.flashcards.filter {
        it.first.contains(searchQuery, ignoreCase = true) ||
                it.second.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { showCardDialog = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Button(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                Text("Voltar")
            }

            Text(
                text = "Flashcards de ${deck.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

            // Toggle para ativar/desativar modo de revisão
            Row(modifier = Modifier.padding(8.dp)) {
                Text("Modo de Revisão")
                Switch(
                    checked = isReviewMode,
                    onCheckedChange = { isReviewMode = it }
                )
            }

            // Ícone de busca
            Row(modifier = Modifier.padding(8.dp)) {
                IconButton(onClick = { showSearchField = !showSearchField }) {
                    Text("🔍") // Ícone de lupa
                }

                // Botão "+" para criar nova carta
                IconButton(onClick = { showCardDialog = true }) {
                    Text("➕") // Ícone para criar nova carta
                }
            }

            // Campo de busca
            if (showSearchField) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar Flashcards") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { /* Lógica de busca, se necessário */ }
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f)
            ) {
                if (isReviewMode) {
                    items(filteredFlashcards) { flashcard ->
                        ReviewFlashcardItem(flashcard) // Função para item de revisão
                    }
                } else {
                    items(filteredFlashcards) { flashcard ->
                        FlashcardItem(flashcard.first, flashcard.second) {
                            flashcardToEdit = flashcard
                            showEditDialog = true
                        }
                    }
                }
            }

            if (showCardDialog) {
                CardDialog(deck = deck, onDismiss = { showCardDialog = false })
            }

            if (showEditDialog && flashcardToEdit != null) {
                EditCardDialog(deck = deck, flashcard = flashcardToEdit!!, onDismiss = {
                    showEditDialog = false
                    flashcardToEdit = null
                })
            }
        }
    }
}

@Composable
fun ReviewFlashcardItem(flashcard: Pair<String, String>) {
    var showBack by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = flashcard.first, // Frente do flashcard
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Exibe a parte de trás quando clicado
        if (showBack) {
            Text(
                text = flashcard.second,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(
            onClick = { showBack = !showBack },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (showBack) "Mostrar Frente" else "Mostrar Verso")
        }
    }
}

@Composable
fun StudyMode(deck: Deck) {
    var currentCardIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    val flashcards = deck.flashcards

    if (currentCardIndex < flashcards.size) {
        val flashcard = flashcards[currentCardIndex]
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = flashcard.first, // Frente da carta
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

            if (showAnswer) {
                Text(
                    text = flashcard.second, // Verso da carta
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showAnswer = !showAnswer }) {
                Text(if (showAnswer) "Esconder Resposta" else "Mostrar Resposta")
            }

            Button(onClick = {
                showAnswer = false
                currentCardIndex++
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Próxima")
            }
        }
    } else {
        Text(
            text = "Você concluiu o estudo!",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun CardDialog(deck: Deck, onDismiss: () -> Unit) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Adicionar Carta") },
        text = {
            Column {
                OutlinedTextField(
                    value = front,
                    onValueChange = { front = it },
                    label = { Text("Frente") }
                )
                OutlinedTextField(
                    value = back,
                    onValueChange = { back = it },
                    label = { Text("Verso") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (front.isNotEmpty() && back.isNotEmpty()) {
                    deck.flashcards.add(Pair(front, back))
                    onDismiss()
                }
            }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditCardDialog(deck: Deck, flashcard: Pair<String, String>, onDismiss: () -> Unit) {
    var front by remember { mutableStateOf(flashcard.first) }
    var back by remember { mutableStateOf(flashcard.second) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Editar Carta") },
        text = {
            Column {
                OutlinedTextField(
                    value = front,
                    onValueChange = { front = it },
                    label = { Text("Frente") }
                )
                OutlinedTextField(
                    value = back,
                    onValueChange = { back = it },
                    label = { Text("Verso") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                deck.flashcards.remove(flashcard)
                deck.flashcards.add(Pair(front, back))
                onDismiss()
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun FlashcardItem(front: String, back: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEditClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = front, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = back, fontSize = 14.sp)
        }
    }
}

data class Deck(var name: String, val flashcards: MutableList<Pair<String, String>>)

@Preview
@Composable
fun MainScreenPreview() {
    FlashcardTheme {
        MainScreen()
    }
}

