package com.bagomri.yemenbloodbank.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bagomri.yemenbloodbank.ui.screens.auth.LoginScreen
import com.bagomri.yemenbloodbank.ui.screens.awareness.AwarenessScreen
import com.bagomri.yemenbloodbank.ui.screens.donor.AddDonorScreen
import com.bagomri.yemenbloodbank.ui.screens.donor.SearchDonorsScreen
import com.bagomri.yemenbloodbank.ui.screens.home.HomeScreen
import com.bagomri.yemenbloodbank.ui.screens.info.AboutScreen
import com.bagomri.yemenbloodbank.ui.screens.info.ContactScreen
import com.bagomri.yemenbloodbank.ui.screens.onboarding.OnboardingScreen
import com.bagomri.yemenbloodbank.ui.screens.reports.ReportDonorScreen
import com.bagomri.yemenbloodbank.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // 1. Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.SearchDonors.route) },
                onNavigateToAddDonor = { navController.navigate(Screen.AddDonor.route) },
                onNavigateToAwareness = { navController.navigate(Screen.Awareness.route) },
                onNavigateToReport = { navController.navigate(Screen.ReportDonor.createRoute("", "")) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToContact = { navController.navigate(Screen.Contact.route) },
                onNavigateByRoute = { route ->
                    when (route) {
                        "search", "/donor/search" -> navController.navigate(Screen.SearchDonors.route)
                        "add_donor", "/donor/add" -> navController.navigate(Screen.AddDonor.route)
                        "awareness", "/awareness" -> navController.navigate(Screen.Awareness.route)
                        "login", "/login" -> navController.navigate(Screen.Login.route)
                        "about", "/info/about" -> navController.navigate(Screen.About.route)
                        else -> { /* Do nothing */ }
                    }
                }
            )
        }

        // 4. Login
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToAdminDashboard = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateToHospitalDashboard = {
                    navController.navigate(Screen.HospitalDashboard.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 5. Search Donors
        composable(Screen.SearchDonors.route) {
            SearchDonorsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReport = { donorId, phone ->
                    navController.navigate(Screen.ReportDonor.createRoute(donorId, phone))
                }
            )
        }

        // 6. Add Donor
        composable(Screen.AddDonor.route) {
            AddDonorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. Report Donor
        composable(
            route = Screen.ReportDonor.route,
            arguments = listOf(
                navArgument("donorId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("phone") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val donorId = backStackEntry.arguments?.getString("donorId") ?: ""
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            ReportDonorScreen(
                donorId = donorId,
                donorPhone = phone,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 8. Awareness
        composable(Screen.Awareness.route) {
            AwarenessScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 9. About
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 10. Contact
        composable(Screen.Contact.route) {
            ContactScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
