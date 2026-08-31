package com.bagomri.yemenbloodbank.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Login : Screen("login")

    // المتبرعون
    object AddDonor : Screen("donor/add")
    object SearchDonors : Screen("donor/search")
    object ReportDonor : Screen("donor/report?donorId={donorId}&phone={phone}") {
        fun createRoute(donorId: String, phone: String) = "donor/report?donorId=$donorId&phone=$phone"
    }

    // معلومات وتوعية
    object Awareness : Screen("awareness")
    object About : Screen("info/about")
    object Contact : Screen("info/contact")

    // لوحة تحكم الأدمن
    object AdminDashboard : Screen("admin/dashboard")
    object AdminManageDonors : Screen("admin/manage_donors")
    object AdminManageHospitals : Screen("admin/manage_hospitals")
    object AdminAddHospital : Screen("admin/add_hospital")
    object AdminEditHospital : Screen("admin/edit_hospital/{hospitalId}") {
        fun createRoute(hospitalId: String) = "admin/edit_hospital/$hospitalId"
    }
    object AdminEditDonor : Screen("admin/edit_donor/{donorId}") {
        fun createRoute(donorId: String) = "admin/edit_donor/$donorId"
    }
    object AdminReviewReports : Screen("admin/review_reports")
    object AdminReportDetail : Screen("admin/report_detail/{reportId}") {
        fun createRoute(reportId: String) = "admin/report_detail/$reportId"
    }
    object AdminSystemOverview : Screen("admin/system_overview")
    object AdminManageLocations : Screen("admin/manage_locations")
    object AdminManageBanners : Screen("admin/banners")

    // لوحة تحكم المستشفى
    object HospitalDashboard : Screen("hospital/dashboard")
    object HospitalManageDonors : Screen("hospital/manage_donors")
    object HospitalSuspendedDonors : Screen("hospital/suspended_donors")
    object HospitalAdvancedSearch : Screen("hospital/advanced_search")
    object HospitalReportsHub : Screen("hospital/reports_hub")
    object HospitalBloodTypeReport : Screen("hospital/report/blood_type")
}
