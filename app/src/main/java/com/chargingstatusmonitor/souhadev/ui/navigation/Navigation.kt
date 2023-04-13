package com.chargingstatusmonitor.souhadev.ui.navigation

import android.app.Activity
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.room.Room
import com.chargingstatusmonitor.souhadev.AdsManagerTradePlus
import com.chargingstatusmonitor.souhadev.R
import com.chargingstatusmonitor.souhadev.data.AppDatabase
import com.chargingstatusmonitor.souhadev.ui.screens.*
import com.chargingstatusmonitor.souhadev.ui.theme.Brown
import com.chargingstatusmonitor.souhadev.ui.theme.Red

@Composable
fun NavigationGraph(navController: NavHostController) {
    
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, context.getString(R.string.room_database_name)
        ).build()
    }

    val dao = remember {
        db.fileEntityDao()
    }
    NavHost(navController, startDestination = NavigationItem.Loading.route) {
        composable(NavigationItem.Loading.route) {
            LoadingScreen(navController)
        }
        composable(NavigationItem.Start.route) {
            StartScreen(navController)
        }
        navigation(
            startDestination = BottomNavItem.Home.route,
            route = NavigationItem.BottomNav.route
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }
            navigation(
                startDestination = NavigationItem.Ringtone.route,
                route = BottomNavItem.RingtoneRoute.route
            ) {
                composable(NavigationItem.Ringtone.route) {
                    RingtoneScreen(navController)
                    //AdsManagerTradePlus.show_inter_jetcompose(context as Activity?)
                }
                composable(NavigationItem.Phone.route) {
                    PhoneScreen(navController, dao)
                }
                composable(
                    "${NavigationItem.Unlock.route}/{id}",
                    arguments = listOf(
                        navArgument("id"){
                            type = NavType.IntType
                        }
                    )
                ) {
                    val id = it.arguments?.getInt("id")
                    if(id != null) {
                        UnlockScreen(navController, dao, id) }
                }
                composable(NavigationItem.Download.route) {
                    DownLoadScreen(navController, dao)
                }
                composable(NavigationItem.Record.route) {
                    RecordScreen(navController)
                }
            }
            composable(BottomNavItem.Search.route) {
                SearchScreen(navController,dao)
            }
            navigation(
                startDestination = NavigationItem.Settings.route,
                route = BottomNavItem.SettingsRoute.route
            ) {
                composable(NavigationItem.Settings.route) {
                    SettingsScreen(navController)
                }
                composable(NavigationItem.Privacy.route) {
                    PrivacyScreen(navController)
                }
            }
        }

    }
}

sealed class BottomNavItem(var title: String, var icon: Int, var route: String) {
    object Home : BottomNavItem("Home", R.drawable.ic_round_home_24, "home")
    object RingtoneRoute :
        BottomNavItem("Ringtone", R.drawable.ic_baseline_audiotrack_24, "ringtone_route")

    object Search : BottomNavItem("Search", R.drawable.ic_baseline_search_24, "search")
    object SettingsRoute : BottomNavItem("Settings", R.drawable.ic_baseline_settings_24, "settings_route")
}

sealed class NavigationItem(var title: String, var route: String) {
    object Loading : NavigationItem("Loading", "loading")
    object Start : NavigationItem("Start", "start")
    object BottomNav : NavigationItem("BottomNav", "bottom_nav")
    object Phone : NavigationItem("Phone", "phone")
    object Download : NavigationItem("Download", "download")
    object Record : NavigationItem("Record", "record")
    object Ringtone : NavigationItem("Ringtone", "ringtone")
    object Unlock : NavigationItem("Unlock", "unlock")
    object Settings : NavigationItem("Settings", "settings")
    object Privacy : NavigationItem("Privacy", "privacy")
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.RingtoneRoute,
            BottomNavItem.Search,
            BottomNavItem.SettingsRoute,
        )
    }
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                selectedContentColor = Red,
                unselectedContentColor = Brown,
                alwaysShowLabel = false,
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        (navController.graph.findNode(NavigationItem.BottomNav.route) as NavGraph?)?.let {
                            popUpTo(it.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }

                    }
                }
            )
        }
    }
}