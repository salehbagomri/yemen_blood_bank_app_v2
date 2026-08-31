package com.bagomri.yemenbloodbank.core.constants

/**
 * جميع النصوص الثابتة في التطبيق
 */
object AppStrings {
    // اسم التطبيق
    const val appName = "بنك دم اليمن"
    const val appNameEnglish = "Yemen Blood Bank"

    // الصفحة الرئيسية والتنقل
    const val home = "الرئيسية"
    const val search = "بحث"
    const val addDonor = "إضافة متبرع"
    const val awareness = "التوعية"
    const val reportDonor = "الإبلاغ عن رقم"

    // البحث
    const val searchForDonors = "البحث عن متبرعين"
    const val selectBloodType = "اختر فصيلة الدم"
    const val selectDistrict = "اختر المحافظة"
    const val selectSubDistrict = "اختر المديرية (اختياري)"
    const val allDistricts = "جميع المديريات"
    const val searchResults = "نتائج البحث"
    const val noDonorsFound = "لا يوجد متبرعين"
    const val noDonorsMessage = "لم يتم العثور على متبرعين بهذه المواصفات"
    const val searchByNameOrPhone = "ابحث بالاسم أو رقم الهاتف..."

    // فصائل الدم
    const val bloodTypeA = "A+"
    const val bloodTypeANeg = "A-"
    const val bloodTypeB = "B+"
    const val bloodTypeBNeg = "B-"
    const val bloodTypeAB = "AB+"
    const val bloodTypeABNeg = "AB-"
    const val bloodTypeO = "O+"
    const val bloodTypeONeg = "O-"

    val bloodTypes = listOf(
        bloodTypeA, bloodTypeANeg,
        bloodTypeB, bloodTypeBNeg,
        bloodTypeAB, bloodTypeABNeg,
        bloodTypeO, bloodTypeONeg
    )

    // محافظات اليمن الـ 22
    val districts = listOf(
        "أمانة العاصمة",
        "عدن",
        "تعز",
        "حضرموت",
        "الحديدة",
        "إب",
        "لحج",
        "أبين",
        "شبوة",
        "مأرب",
        "المهرة",
        "البيضاء",
        "الضالع",
        "الجوف",
        "حجة",
        "عمران",
        "صعدة",
        "ذمار",
        "المحويت",
        "ريمة",
        "أرخبيل سقطرى",
        "صنعاء"
    )

    // خريطة المديريات التابعة لكل محافظة في اليمن
    val governorateDistricts: Map<String, List<String>> = mapOf(
        "أمانة العاصمة" to listOf("السبعين", "شعوب", "معين", "التحرير", "الثورة", "الصافية", "صنعاء القديمة", "الوحدة", "آزال", "بني الحارث"),
        "عدن" to listOf("صيرة (كريتر)", "التواهي", "المعلا", "خور مكسر", "الشيخ عثمان", "المنصورة", "دار سعد", "البريقة"),
        "تعز" to listOf("القاهرة", "المظفر", "صالة", "التعزية", "شرعب السلام", "شرعب الرونة", "المخا", "التربة", "المواسط", "خدير", "ماوية", "المعافر", "حيفان"),
        "حضرموت" to listOf("المكلا", "سيئون", "الشحر", "تريم", "شبام", "القطن", "غيل باوزير", "دوعن", "غيل بن يمين", "الريدة وقصيعر"),
        "الحديدة" to listOf("الحوك", "الميناء", "الحالي", "باجل", "بيت الفقية", "زبيد", "الجراحي", "الخوخة", "حيس", "الدريهمي", "المرواعة"),
        "إب" to listOf("المشنة", "الظهار", "ذي السفال", "السياني", "جبلة", "حبيش", "بعدان", "يريم", "السدة", "النادرة", "العدين", "حزم العدين"),
        "لحج" to listOf("الحوطة", "تبن", "يافع (لبعوس)", "ردفان", "طور الباحة", "القبيطة", "المقاطرة", "الملاح"),
        "أبين" to listOf("زنجبار", "خنفر (جعار)", "لودر", "مودية", "المحفد", "أحور", "رصد"),
        "شبوة" to listOf("عتق", "بيحان", "عسيلان", "نصاب", "حبان", "ميفعة", "الروضة", "رضوم"),
        "مأرب" to listOf("المدينة", "مأرب", "صرواح", "حريب", "الجوبة", "مجزر"),
        "المهرة" to listOf("الغيضة", "حوف", "شحن", "قشن", "سيحوت", "المسيلة", "حصوين", "منعر", "حات"),
        "البيضاء" to listOf("البيضاء", "رداع", "مكيراس", "السوادية", "الزاهر"),
        "الضالع" to listOf("الضالع", "قعطبة", "دمت", "جبن", "الحشاء", "الشعيب"),
        "الجوف" to listOf("الحزم", "خب والشعف", "المطمة", "المصلوب", "الغيل"),
        "حجة" to listOf("حجة", "عبس", "المحابشة", "كحلان عفار", "حرض", "ميدي"),
        "عمران" to listOf("عمران", "خمر", "ريدة", "ثلاء", "شهارة", "حوث"),
        "صعدة" to listOf("صعدة", "سحار", "مجز", "الصفراء", "باقم", "كتاف والبقع"),
        "ذمار" to listOf("ذمار", "جهران (معبر)", "ضوران آنس", "عتمة", "وصاب العالي", "وصاب السافل"),
        "المحويت" to listOf("المحويت", "شبام كوكبان", "الطويلة", "الرجم", "الخبت"),
        "ريمة" to listOf("الجبين", "كسمة", "مزهر", "السلفية", "بلاد الطعام"),
        "أرخبيل سقطرى" to listOf("حديبو", "قلنسية"),
        "صنعاء" to listOf("همدان", "بني مطر", "سنحان وبني بهلول", "الحيمتين", "خولان", "أرحب", "نهم")
    )

    // بيانات المتبرع
    const val donorName = "الاسم"
    const val phoneNumber = "رقم الهاتف"
    const val phoneNumber2 = "رقم إضافي 1"
    const val phoneNumber3 = "رقم إضافي 2"
    const val bloodType = "فصيلة الدم"
    const val district = "المحافظة"
    const val subDistrict = "المديرية"
    const val age = "العمر"
    const val gender = "الجنس"
    const val male = "ذكر"
    const val female = "أنثى"
    const val notes = "ملاحظات"
    const val optional = "اختياري"
    const val availableForDonation = "متاح للتبرع حالياً"
    const val lastDonationDate = "آخر تاريخ تبرع"
    const val suspendedUntil = "موقوف حتى"

    // أزرار
    const val save = "حفظ"
    const val cancel = "إلغاء"
    const val call = "اتصال"
    const val whatsapp = "واتساب"
    const val edit = "تعديل"
    const val delete = "حذف"
    const val confirm = "تأكيد"
    const val back = "رجوع"
    const val next = "التالي"
    const val submit = "إرسال"
    const val refresh = "تحديث"
    const val retry = "إعادة المحاولة"
    const val filter = "تصفية"
    const val clear = "مسح"
    const val logout = "تسجيل الخروج"

    // رسائل النجاح
    const val donorAddedSuccessfully = "تمت إضافة المتبرع بنجاح"
    const val donorUpdatedSuccessfully = "تم تحديث بيانات المتبرع"
    const val donorDeletedSuccessfully = "تم حذف المتبرع"
    const val donorSuspendedSuccessfully = "تم إيقاف المتبرع لمدة 6 أشهر"
    const val donationDateUpdatedSuccessfully = "تم تحديث تاريخ التبرع بنجاح"

    // رسائل الخطأ والتحقق
    const val errorOccurred = "حدث خطأ ما"
    const val pleaseCheckInternet = "يرجى التحقق من الاتصال بالإنترنت"
    const val requiredField = "هذا الحقل مطلوب"
    const val invalidPhone = "رقم الهاتف غير صالح (9 أرقام تبدأ بـ 7)"
    const val invalidAge = "العمر يجب أن يكون بين 17 و 70 سنة"
    const val duplicatePhone = "رقم الهاتف مسجل بالفعل"

    // الإحصائيات
    const val statistics = "الإحصائيات"
    const val totalDonors = "إجمالي المتبرعين"
    const val availableDonors = "المتبرعون المتاحون"
    const val suspendedDonorsCount = "المتبرعون الموقوفون"
    const val mostCommonBloodType = "أكثر فصيلة متوفرة"
    const val mostActiveDistrict = "أكثر محافظة نشاطًا"
    const val latestDonor = "أحدث متبرع"
    const val bloodTypeDistribution = "توزيع فصائل الدم"
    const val governorateDistribution = "توزيع المحافظات"

    // التوعية
    const val awarenessTitle = "التوعية والإرشادات"
    const val importanceOfDonation = "أهمية التبرع بالدم"
    const val whoCanDonate = "من يمكنه التبرع؟"
    const val beforeDonation = "قبل التبرع"
    const val afterDonation = "بعد التبرع"
    const val prohibitedCases = "الحالات الممنوعة"
    const val donationInterval = "المدة بين التبرعات"

    // الإبلاغ
    const val reportTitle = "الإبلاغ عن رقم غير صالح"
    const val reportReason = "سبب البلاغ"
    const val numberNotWorking = "الرقم لا يعمل"
    const val wrongNumber = "رقم خاطئ"
    const val refusesToDonate = "يرفض التبرع"
    const val numberBusy = "الرقم مشغول دائماً"
    const val noAnswer = "لا يرد على الاتصال"
    const val deceased = "متوفى"
    const val movedAway = "انتقل من المنطقة"
    const val healthIssues = "لديه مشاكل صحية"
    const val other = "سبب آخر"
    const val reportSubmitted = "تم إرسال البلاغ بنجاح"

    // تسجيل الدخول
    const val login = "تسجيل الدخول"
    const val loginSubtitle = "سجل دخولك كمدير نظام أو مستشفى"
    const val email = "البريد الإلكتروني"
    const val password = "كلمة المرور"
    const val hospital = "مستشفى"
    const val admin = "مدير النظام"

    // لوحة المستشفى
    const val hospitalDashboard = "لوحة إدارة المستشفى"
    const val manageDonors = "إدارة المتبرعين"
    const val advancedSearch = "بحث متقدم"
    const val suspendedDonors = "المتبرعون الموقوفون"
    const val bloodTypeReport = "تقرير الفصائل"
    const val districtReport = "تقرير المحافظات"
    const val suspendFor6Months = "إيقاف لمدة 6 أشهر"
    const val updateLastDonation = "تحديث آخر تبرع"
    const val exportReports = "تصدير التقارير"

    // لوحة الأدمن
    const val adminDashboard = "لوحة إدارة النظام"
    const val manageHospitals = "إدارة المستشفيات"
    const val addHospital = "إضافة مستشفى"
    const val editHospital = "تعديل مستشفى"
    const val reviewReports = "مراجعة البلاغات"
    const val systemOverview = "نظرة عامة على النظام"
    const val manageLocations = "إدارة المناطق"
    const val manageBanners = "إدارة الإعلانات والبانرات"
    const val approveReport = "قبول البلاغ"
    const val rejectReport = "رفض البلاغ"

    // رسالة WhatsApp الافتراضية
    const val whatsappDefaultMessage =
        "السلام عليكم ورحمة الله وبركاته\n" +
        "نأمل منكم التبرع بالدم لإنقاذ حياة إنسان\n" +
        "جزاكم الله خيراً"
}
