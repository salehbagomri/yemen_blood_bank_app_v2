package com.bagomri.yemenbloodbank.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminAddHospitalScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminDashboardScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminEditDonorScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminEditHospitalScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminManageBannersScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminManageDonorsScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminManageHospitalsScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminManageLocationsScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminReportDetailScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminReviewReportsScreen
import com.bagomri.yemenbloodbank.ui.screens.admin.AdminSystemOverviewScreen
import com.bagomri.yemenbloodbank.ui.screens.auth.LoginScreen
import com.bagomri.yemenbloodbank.ui.screens.awareness.AwarenessScreen
import com.bagomri.yemenbloodbank.ui.screens.donor.AddDonorScreen
import com.bagomri.yemenbloodbank.ui.screens.donor.SearchDonorsScreen
import com.bagomri.yemenbloodbank.ui.screens.home.HomeScreen
import com.bagomri.yemenbloodbank.ui.screens.hospital.HospitalAdvancedSearchScreen
import com.bagomri.yemenbloodbank.ui.screens.hospital.HospitalBloodTypeReportScreen
import com.bagomri.yemenbloodbank.ui.screens.hospital.HospitalDashboardScreen
import com.bagomri.yemenbloodbank.ui.screens.hospital.HospitalManageDonorsScreen
import com.bagomri.yemenbloodbank.ui.screens.hospital.HospitalReportsHubScreen
import com.bagomri.yemenbloodbank.ui.screens.hospital.HospitalSuspendedDonorsScreen
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
                navArgument("donorId") { type = NavType.StringType; defaultValue = "" },
                navArgument("phone") { type = NavType.StringType; defaultValue = "" }
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

        // ==================== لوحة تحكم المستشفى ====================
        composable(Screen.HospitalDashboard.route) {
            HospitalDashboardScreen(
                onNavigateToManageDonors = { navController.navigate(Screen.HospitalManageDonors.route) },
                onNavigateToSuspendedDonors = { navController.navigate(Screen.HospitalSuspendedDonors.route) },
                onNavigateToAdvancedSearch = { navController.navigate(Screen.HospitalAdvancedSearch.route) },
                onNavigateToReportsHub = { navController.navigate(Screen.HospitalReportsHub.route) },
                onNavigateToAddDonor = { navController.navigate(Screen.AddDonor.route) },
                onLogout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HospitalManageDonors.route) {
            HospitalManageDonorsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddDonor = { navController.navigate(Screen.AddDonor.route) }
            )
        }

        composable(Screen.HospitalSuspendedDonors.route) {
            HospitalSuspendedDonorsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.HospitalAdvancedSearch.route) {
            HospitalAdvancedSearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.HospitalReportsHub.route) {
            HospitalReportsHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBloodTypeReport = { navController.navigate(Screen.HospitalBloodTypeReport.route) },
                onNavigateToSuspendedDonors = { navController.navigate(Screen.HospitalSuspendedDonors.route) }
            )
        }

        composable(Screen.HospitalBloodTypeReport.route) {
            HospitalBloodTypeReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== لوحة تحكم الأدمن ====================
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToReviewReports = { navController.navigate(Screen.AdminReviewReports.route) },
                onNavigateToManageHospitals = { navController.navigate(Screen.AdminManageHospitals.route) },
                onNavigateToManageDonors = { navController.navigate(Screen.AdminManageDonors.route) },
                onNavigateToSystemOverview = { navController.navigate(Screen.AdminSystemOverview.route) },
                onNavigateToManageLocations = { navController.navigate(Screen.AdminManageLocations.route) },
                onNavigateToManageBanners = { navController.navigate(Screen.AdminManageBanners.route) },
                onLogout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminManageDonors.route) {
            AdminManageDonorsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddDonor = { navController.navigate(Screen.AddDonor.route) },
                onNavigateToEditDonor = { donorId ->
                    navController.navigate(Screen.AdminEditDonor.createRoute(donorId))
                }
            )
        }

        composable(
            route = Screen.AdminEditDonor.route,
            arguments = listOf(navArgument("donorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val donorId = backStackEntry.arguments?.getString("donorId") ?: ""
            AdminEditDonorScreen(
                donorId = donorId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminManageHospitals.route) {
            AdminManageHospitalsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddHospital = { navController.navigate(Screen.AdminAddHospital.route) },
                onNavigateToEditHospital = { hospitalId ->
                    navController.navigate(Screen.AdminEditHospital.createRoute(hospitalId))
                }
            )
        }

        composable(Screen.AdminAddHospital.route) {
            AdminAddHospitalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AdminEditHospital.route,
            arguments = listOf(navArgument("hospitalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val hospitalId = backStackEntry.arguments?.getString("hospitalId") ?: ""
            AdminEditHospitalScreen(
                hospitalId = hospitalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminReviewReports.route) {
            AdminReviewReportsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Screen.AdminReportDetail.createRoute(reportId))
                }
            )
        }

        composable(
            route = Screen.AdminReportDetail.route,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            AdminReportDetailScreen(
                reportId = reportId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminSystemOverview.route) {
            AdminSystemOverviewScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminManageLocations.route) {
            AdminManageLocationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminManageBanners.route) {
            AdminManageBannersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
