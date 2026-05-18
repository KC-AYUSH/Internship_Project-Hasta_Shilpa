package com.example.hastashilpa.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hastashilpa.data.model.getSampleDesignItems
import com.example.hastashilpa.ui.screens.*
import com.example.hastashilpa.viewmodel.AuthState
import com.example.hastashilpa.viewmodel.AuthViewModel
import com.example.hastashilpa.viewmodel.MarketplaceViewModel
import com.example.hastashilpa.viewmodel.TrendViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

sealed class Screen(val route: String, val label: String? = null, val icon: ImageVector? = null) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Trends : Screen("trends", "Trends", Icons.Filled.Home)
    object Blueprint : Screen("blueprint?name={name}", "Blueprint", Icons.Filled.Build) {
        fun createRoute(name: String? = null) = if (name != null) "blueprint?name=$name" else "blueprint"
    }
    object Tracker : Screen("tracker", "Tracker", Icons.AutoMirrored.Filled.List)
    object Price : Screen("price", "Price", Icons.Filled.CurrencyRupee)
    object Marketplace : Screen("marketplace", "Market", Icons.Filled.ShoppingCart)
    object History : Screen("history", "Activity", Icons.AutoMirrored.Filled.ReceiptLong)
    object Cart : Screen("cart", "Cart", Icons.Filled.ShoppingBag)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object ProductDetail : Screen("product_detail/{itemId}") {
        fun createRoute(itemId: String) = "product_detail/$itemId"
    }
}

val bottomNavItems = listOf(
    Screen.Trends,
    Screen.Blueprint,
    Screen.Tracker,
    Screen.Price,
    Screen.Marketplace,
    Screen.History,
    Screen.Cart,
    Screen.Profile
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    
    val marketplaceViewModel: MarketplaceViewModel = viewModel()
    val trendViewModel: TrendViewModel = viewModel()

    val showBottomBar = currentRoute in bottomNavItems.map { it.route } || 
                      currentRoute?.startsWith("blueprint") == true

    // Google Sign In Setup
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.google.android.gms.common.R.string.common_google_play_services_unknown_issue))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { idToken ->
                    authViewModel.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) { }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Screen.Trends.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    val startDestination = if (authViewModel.currentUser != null) Screen.Trends.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // Scrollable Bottom Bar
                Surface(
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        bottomNavItems.forEach { screen ->
                            val selected = currentRoute?.split("?")?.firstOrNull() == screen.route.split("?")?.firstOrNull()
                            
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    val route = if (screen == Screen.Blueprint) Screen.Blueprint.createRoute() else screen.route
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { 
                                    Icon(
                                        imageVector = screen.icon!!, 
                                        contentDescription = screen.label,
                                        modifier = Modifier.size(28.dp)
                                    ) 
                                },
                                label = { Text(screen.label!!, maxLines = 1) },
                                modifier = Modifier.width(85.dp)
                            )
                        }
                    }
                }
            }
        }
    )
{ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClick = { email, password ->
                        authViewModel.signIn(email, password)
                    },
                    onSignUpClick = {
                        navController.navigate(Screen.SignUp.route)
                    },
                    onForgotPasswordClick = {
                        Toast.makeText(context, "Password reset email sent (if email exists)", Toast.LENGTH_LONG).show()
                    },
                    onGoogleSignInClick = {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    },
                    isLoading = authState is AuthState.Loading,
                    errorMessage = (authState as? AuthState.Error)?.message
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpClick = { email, password, name, phone ->
                        authViewModel.signUp(email, password, name, phone)
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    },
                    isLoading = authState is AuthState.Loading,
                    errorMessage = (authState as? AuthState.Error)?.message
                )
            }
            composable(Screen.Trends.route) {
                TrendFeedScreen(
                    innerPadding = innerPadding,
                    onItemClick = { item ->
                        navController.navigate(Screen.Blueprint.createRoute(item.name))
                    },
                    viewModel = trendViewModel
                )
            }
            composable(
                route = Screen.Blueprint.route,
                arguments = listOf(navArgument("name") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")
                BlueprintScreen(innerPadding, initialBlueprintName = name)
            }
            composable(Screen.Tracker.route) { TrackerScreen(innerPadding) }
            composable(Screen.Price.route) { PriceSuggesterScreen(innerPadding) }
            composable(Screen.Marketplace.route) {
                MarketplaceScreen(
                    innerPadding = innerPadding,
                    onItemClick = { item ->
                        val idToPass = if (item.firestoreId.isNotEmpty()) item.firestoreId else item.id.toString()
                        navController.navigate(Screen.ProductDetail.createRoute(idToPass))
                    },
                    viewModel = marketplaceViewModel
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(innerPadding = innerPadding)
            }
            composable(Screen.Cart.route) {
                CartScreen(innerPadding = innerPadding)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    innerPadding = innerPadding,
                    onLogoutClick = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                
                val firestoreItems by marketplaceViewModel.items.collectAsState()
                val trendItems by trendViewModel.trends.collectAsState()
                
                val item = getSampleDesignItems().find { it.id.toString() == itemId }
                    ?: firestoreItems.find { it.firestoreId == itemId || it.id.toString() == itemId }
                    ?: trendItems.find { it.firestoreId == itemId || it.id.toString() == itemId }

                if (item != null) {
                    ProductDetailScreen(
                        item = item,
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
