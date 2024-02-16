package com.example.androidscaffoldexample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidscaffoldexample.ui.theme.AndroidScaffoldexampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidScaffoldexampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScaffoldLayout()
                }
            }
        }
    }
}

@Composable
fun MyScaffoldLayout() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val contextForToast = LocalContext.current.applicationContext
    val drawerItemList = prepareNavigationDrawerItems()
    val selectedItem = remember { mutableStateOf(drawerItemList[0]) }
    var clickCount by remember {
        mutableIntStateOf(0) // or use mutableStateOf(0)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                LazyColumn {
                    items(drawerItemList) { item ->  //in our previous example, we did not utilize LazyColumn items method to create the list of items, but instead directly looped through the list and created the items: drawerItemList.forEach { item -> NavigationDrawerItem(.
                        NavigationDrawerItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = null) },
                            label = { Text(text = item.label) },
                            selected = (item == selectedItem.value),
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                                selectedItem.value = item
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }

        }
    ) {

        Scaffold(
            topBar = { MyTopAppBar() },
            bottomBar = { MyBottomBar(contextForToast = contextForToast) },
            floatingActionButton = { MyFAB(contextForToast = contextForToast) },
            floatingActionButtonPosition = FabPosition.End,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            // rest of the app's UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Rest of the app UI")
                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Snackbar # ${++clickCount}",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                ) {
                    Text(text = "Show Snackbar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar() {
    MediumTopAppBar(
        title = { Text(text = "SemicolonSpace") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    )
}

@Composable
fun MyFAB(contextForToast: Context) {

    ExtendedFloatingActionButton(
        onClick = { Toast.makeText(contextForToast, "FAB", Toast.LENGTH_SHORT)
            .show() },
        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "add icon") },
        text = { Text(text = "Extended FAB") },
    )
}

@Composable
fun MyBottomBar(contextForToast: Context) {
    val bottomBarItemsList = mutableListOf<BottomBarItem>()

    bottomBarItemsList.add(BottomBarItem(icon = Icons.Default.Home, name = "Home"))
    bottomBarItemsList.add(BottomBarItem(icon = Icons.Default.Person, name = "Profile"))
    bottomBarItemsList.add(BottomBarItem(icon = Icons.Default.Favorite, name = "Favorites"))

    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar {
        bottomBarItemsList.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.name) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    Toast.makeText(contextForToast, item.name, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

data class BottomBarItem(val icon: ImageVector, val name: String)
private fun prepareNavigationDrawerItems(): List<NavigationDrawerData> {
    val drawerItemsList = arrayListOf<NavigationDrawerData>()

    // add items
    drawerItemsList.add(NavigationDrawerData(label = "Home", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Profile", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Cart", icon = Icons.Filled.ShoppingCart))
    drawerItemsList.add(NavigationDrawerData(label = "Settings", icon = Icons.Filled.Settings))
    drawerItemsList.add(NavigationDrawerData(label = "Home", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Profile", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Cart", icon = Icons.Filled.ShoppingCart))
    drawerItemsList.add(NavigationDrawerData(label = "Settings", icon = Icons.Filled.Settings))
    drawerItemsList.add(NavigationDrawerData(label = "Home", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Profile", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Cart", icon = Icons.Filled.ShoppingCart))
    drawerItemsList.add(NavigationDrawerData(label = "Settings", icon = Icons.Filled.Settings))
    drawerItemsList.add(NavigationDrawerData(label = "Home", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Profile", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Cart", icon = Icons.Filled.ShoppingCart))
    drawerItemsList.add(NavigationDrawerData(label = "Settings", icon = Icons.Filled.Settings))
    drawerItemsList.add(NavigationDrawerData(label = "Home", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Profile", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Cart", icon = Icons.Filled.ShoppingCart))
    drawerItemsList.add(NavigationDrawerData(label = "Settings", icon = Icons.Filled.Settings))
    drawerItemsList.add(NavigationDrawerData(label = "Home", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Profile", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Cart", icon = Icons.Filled.ShoppingCart))
    drawerItemsList.add(NavigationDrawerData(label = "Settings", icon = Icons.Filled.Settings))
    drawerItemsList.add(NavigationDrawerData(label = "Home", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Profile", icon = Icons.Filled.Person))
    drawerItemsList.add(NavigationDrawerData(label = "Cart", icon = Icons.Filled.ShoppingCart))
    drawerItemsList.add(NavigationDrawerData(label = "Settings", icon = Icons.Filled.Settings))

    return drawerItemsList
}

data class NavigationDrawerData(val label: String, val icon: ImageVector)