import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawer(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        DrawerHeader()
        
        NavigationDrawerItem(
            label = { Text("Completed Mitzvot") },
            selected = false,
            onClick = { 
                onNavigate("completed")
                onDismiss()
            }
        )

        NavigationDrawerItem(
            label = { Text("Suggest a Mitzvah!") },
            selected = false,
            onClick = { 
                onNavigate("suggest")
                onDismiss()
            }
        )

        NavigationDrawerItem(
            label = { Text("About") },
            selected = false,
            onClick = { 
                onNavigate("about")
                onDismiss()
            }
        )

        NavigationDrawerItem(
            label = { Text("Birkat Hamazon") },
            selected = false,
            onClick = { 
                onNavigate("birkat")
                onDismiss()
            }
        )

        NavigationDrawerItem(
            label = { Text("Brachot") },
            selected = false,
            onClick = { 
                onNavigate("brachot")
                onDismiss()
            }
        )

        NavigationDrawerItem(
            label = { Text("Tefilat Haderech") },
            selected = false,
            onClick = { 
                onNavigate("tefilat")
                onDismiss()
            }
        )
    }
}

@Composable
private fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap the \"Mitzvah Me\"\nbutton for a mitzvah!",
            style = MaterialTheme.typography.headlineMedium
        )
    }
} 