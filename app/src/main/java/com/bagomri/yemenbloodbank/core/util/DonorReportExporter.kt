package com.bagomri.yemenbloodbank.core.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bagomri.yemenbloodbank.data.model.Donor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * محرك تصدير ومشاركة كشوفات المتبرعين الرسمية (PDF & Excel/CSV)
 */
object DonorReportExporter {

    private const val A4_WIDTH = 595
    private const val A4_HEIGHT = 842
    private const val MARGIN = 36f

    /**
     * توليد وثيقة PDF رسمية جاهزة للطباعة والمشاركة
     */
    suspend fun generatePdfReport(
        context: Context,
        donors: List<Donor>,
        title: String = "كشف متبرعي الدم",
        organizationName: String,
        governorate: String,
        filterSummary: String,
        includeSecondaryPhones: Boolean = true,
        includeNotes: Boolean = false
    ): Result<File> = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        try {
            val reportsDir = File(context.cacheDir, "reports").apply { if (!exists()) mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(reportsDir, "donor_report_$timeStamp.pdf")

            val nowFormatted = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.forLanguageTag("ar")).format(Date())

            // إعدادات الرسم والخطوط
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(33, 33, 33)
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }

            val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(183, 28, 28)
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            val tableHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val rowTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(33, 33, 33)
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.RIGHT
            }

            val rowCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(33, 33, 33)
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            // توزيع الأعمدة (إجمالي العرض المتاح: 595 - 72 = 523pt)
            // الترتيب من اليمين إلى اليسار (RTL):
            // 1. م (تسلسل): 25pt
            // 2. اسم المتبرع: 125pt
            // 3. الفصيلة: 45pt
            // 4. رقم الهاتف الأساسي (+ البديل): 105pt
            // 5. المديرية: 80pt
            // 6. العمر/الجنس: 55pt
            // 7. الجاهزية / آخر تبرع: 88pt
            val colSeqW = 25f
            val colNameW = 125f
            val colBloodW = 45f
            val colPhoneW = 105f
            val colDistW = 80f
            val colAgeW = 55f
            val colStatusW = 88f

            val rowHeight = 24f
            val tableWidth = colSeqW + colNameW + colBloodW + colPhoneW + colDistW + colAgeW + colStatusW // 523f
            val tableRight = A4_WIDTH - MARGIN
            val tableLeft = tableRight - tableWidth

            // حساب عدد الصفوف لكل صفحة
            // الصفحة الأولى بها ترويسة كبيرة (تستوعب حوالي 19 صفاً)
            // الصفحات اللاحقة بها ترويسة مصغرة (تستوعب حوالي 26 صفاً)
            val rowsFirstPage = 18
            val rowsOtherPages = 25

            val totalDonors = donors.size
            val totalPages = if (totalDonors <= rowsFirstPage) 1 else {
                1 + ((totalDonors - rowsFirstPage + rowsOtherPages - 1) / rowsOtherPages)
            }

            var donorIndex = 0
            for (pageNum in 1..totalPages) {
                val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageNum).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                var currentY = MARGIN

                // --- رسم الترويسة ---
                if (pageNum == 1) {
                    // شريط علوي ملون - مرتفع لأعلى الصفحة (16pt) لمنع أي تداخل مع العنوان
                    val barTop = 16f
                    val barHeight = 4f
                    bgPaint.color = Color.rgb(183, 28, 28) // Crimson / Dark Blood
                    canvas.drawRect(MARGIN, barTop, A4_WIDTH - MARGIN, barTop + barHeight, bgPaint)

                    currentY = 50f

                    // العنوان الرئيسي (باستخدام الشرطة القصيرة -)
                    boldPaint.textSize = 15f
                    boldPaint.color = Color.rgb(183, 28, 28)
                    boldPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("بنك دم اليمن - $title", tableRight, currentY, boldPaint)

                    // شعار / صفة الجهة
                    textPaint.textSize = 10.5f
                    textPaint.color = Color.rgb(66, 66, 66)
                    textPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("صادر عن: $organizationName • محافظة: $governorate", tableRight, currentY + 16f, textPaint)

                    // معلومات الاستخراج والعدد (يسار الترويسة)
                    textPaint.textSize = 8.5f
                    textPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("تاريخ الاستخراج: $nowFormatted", MARGIN, currentY, textPaint)
                    canvas.drawText("إجمالي المسجلين بالكشف: $totalDonors متبرع", MARGIN, currentY + 16f, textPaint)

                    currentY += 34f

                    // صندوق معايير الفلترة المطبقة
                    bgPaint.color = Color.rgb(245, 247, 250)
                    val filterRect = RectF(MARGIN, currentY, tableRight, currentY + 24f)
                    canvas.drawRoundRect(filterRect, 6f, 6f, bgPaint)

                    bgPaint.style = Paint.Style.STROKE
                    bgPaint.color = Color.rgb(224, 224, 224)
                    bgPaint.strokeWidth = 1f
                    canvas.drawRoundRect(filterRect, 6f, 6f, bgPaint)
                    bgPaint.style = Paint.Style.FILL

                    textPaint.textSize = 8.5f
                    textPaint.color = Color.rgb(44, 62, 80)
                    textPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("معايير وتصفية الكشف: $filterSummary", tableRight - 10f, currentY + 15f, textPaint)

                    currentY += 34f
                } else {
                    // ترويسة مصغرة للصفحات التالية مع شريط علوي أنيق
                    val barTop = 16f
                    val barHeight = 3f
                    bgPaint.color = Color.rgb(183, 28, 28)
                    canvas.drawRect(MARGIN, barTop, A4_WIDTH - MARGIN, barTop + barHeight, bgPaint)

                    currentY = 38f
                    boldPaint.textSize = 10.5f
                    boldPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("بنك دم اليمن - $organizationName ($governorate)", tableRight, currentY, boldPaint)

                    textPaint.textSize = 8.5f
                    textPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("صفحة $pageNum من $totalPages", MARGIN, currentY, textPaint)

                    currentY += 16f
                }

                // --- رسم رأس الجدول (Table Header) ---
                val tableHeaderHeight = 22f
                bgPaint.color = Color.rgb(183, 28, 28) // Header Background
                canvas.drawRect(tableLeft, currentY, tableRight, currentY + tableHeaderHeight, bgPaint)

                var colX = tableRight
                val headers = listOf(
                    "م" to colSeqW,
                    "اسم المتبرع" to colNameW,
                    "الفصيلة" to colBloodW,
                    "رقم الهاتف" to colPhoneW,
                    "المديرية" to colDistW,
                    "العمر/الجنس" to colAgeW,
                    "الحالة والجاهزية" to colStatusW
                )

                for ((headerText, width) in headers) {
                    val centerCol = colX - (width / 2f)
                    canvas.drawText(headerText, centerCol, currentY + 15f, tableHeaderPaint)
                    // خط فاصل عمودي أبيض خفيف
                    bgPaint.color = Color.rgb(220, 220, 220)
                    bgPaint.strokeWidth = 0.5f
                    canvas.drawLine(colX - width, currentY, colX - width, currentY + tableHeaderHeight, bgPaint)
                    colX -= width
                }

                currentY += tableHeaderHeight

                // --- رسم صفوف المتبرعين ---
                val rowsOnThisPage = if (pageNum == 1) rowsFirstPage else rowsOtherPages
                var pageRowCount = 0

                while (donorIndex < totalDonors && pageRowCount < rowsOnThisPage) {
                    val donor = donors[donorIndex]
                    val isAlt = pageRowCount % 2 == 1
                    bgPaint.color = if (isAlt) Color.rgb(249, 250, 251) else Color.WHITE
                    canvas.drawRect(tableLeft, currentY, tableRight, currentY + rowHeight, bgPaint)

                    // حدود أفقية سفلية للصف
                    bgPaint.color = Color.rgb(238, 238, 238)
                    bgPaint.strokeWidth = 0.5f
                    canvas.drawLine(tableLeft, currentY + rowHeight, tableRight, currentY + rowHeight, bgPaint)

                    var cellX = tableRight

                    // 1. الرقم التسلسلي (م)
                    rowCenterPaint.color = Color.rgb(120, 120, 120)
                    rowCenterPaint.textSize = 8.5f
                    canvas.drawText("${donorIndex + 1}", cellX - (colSeqW / 2f), currentY + 15f, rowCenterPaint)
                    cellX -= colSeqW

                    // 2. اسم المتبرع
                    rowTextPaint.color = Color.rgb(30, 30, 30)
                    rowTextPaint.textSize = 8.5f
                    val nameTrimmed = if (donor.name.length > 24) donor.name.take(22) + ".." else donor.name
                    canvas.drawText(nameTrimmed, cellX - 6f, currentY + 15f, rowTextPaint)
                    cellX -= colNameW

                    // 3. فصيلة الدم (شارة ملونة صغيرة)
                    val bloodBg = when {
                        donor.bloodType.contains("-") -> Color.rgb(255, 235, 238) // Pink/Red tint for rare
                        else -> Color.rgb(240, 244, 248)
                    }
                    bgPaint.color = bloodBg
                    val badgeRect = RectF(cellX - colBloodW + 6f, currentY + 4f, cellX - 6f, currentY + rowHeight - 4f)
                    canvas.drawRoundRect(badgeRect, 4f, 4f, bgPaint)

                    rowCenterPaint.color = Color.rgb(183, 28, 28)
                    rowCenterPaint.textSize = 9f
                    canvas.drawText(donor.bloodType, cellX - (colBloodW / 2f), currentY + 15f, rowCenterPaint)
                    cellX -= colBloodW

                    // 4. رقم الهاتف (الأساسي + الثانوي إن وجد - موحد بدون رمز الدولة)
                    rowCenterPaint.color = Color.rgb(44, 62, 80)
                    rowCenterPaint.textSize = 8.5f
                    val p1 = PhoneUtils.cleanLocalPhone(donor.phoneNumber)
                    val p2 = PhoneUtils.cleanLocalPhone(donor.phoneNumber2)
                    val phoneText = if (includeSecondaryPhones && p2.isNotBlank()) {
                        "$p1 / $p2"
                    } else {
                        p1
                    }
                    canvas.drawText(phoneText, cellX - (colPhoneW / 2f), currentY + 15f, rowCenterPaint)
                    cellX -= colPhoneW

                    // 5. المديرية
                    rowTextPaint.color = Color.rgb(66, 66, 66)
                    val subDist = donor.subDistrict.ifEmpty { donor.district }
                    val distTrimmed = if (subDist.length > 16) subDist.take(15) + ".." else subDist
                    canvas.drawText(distTrimmed, cellX - 6f, currentY + 15f, rowTextPaint)
                    cellX -= colDistW

                    // 6. العمر والجنس
                    val genderLabel = if (donor.gender == "female") "أنثى" else "ذكر"
                    rowCenterPaint.color = Color.rgb(80, 80, 80)
                    canvas.drawText("${donor.age}س • $genderLabel", cellX - (colAgeW / 2f), currentY + 15f, rowCenterPaint)
                    cellX -= colAgeW

                    // 7. الحالة والجاهزية
                    val statusText = when {
                        !donor.isActive -> "غير نشط"
                        donor.isSuspended -> "موقوف مؤقتاً"
                        donor.canDonateNow -> "جاهز للتبرع"
                        else -> donor.lastDonationDate?.take(10) ?: "غير متوفر"
                    }
                    val statusColor = when {
                        !donor.isActive -> Color.rgb(158, 158, 158)
                        donor.isSuspended -> Color.rgb(230, 81, 0) // Orange
                        donor.canDonateNow -> Color.rgb(46, 125, 50) // Green
                        else -> Color.rgb(100, 116, 139)
                    }
                    rowCenterPaint.color = statusColor
                    canvas.drawText(statusText, cellX - (colStatusW / 2f), currentY + 15f, rowCenterPaint)

                    currentY += rowHeight
                    donorIndex++
                    pageRowCount++
                }

                // خط إغلاق الجدول أسفل الصفحة
                bgPaint.color = Color.rgb(183, 28, 28)
                bgPaint.strokeWidth = 1f
                canvas.drawLine(tableLeft, currentY, tableRight, currentY, bgPaint)

                // --- مربع الختم والتوقيع (فقط في الصفحة الأخيرة) ---
                if (pageNum == totalPages) {
                    val signY = A4_HEIGHT - MARGIN - 60f
                    bgPaint.color = Color.rgb(250, 250, 250)
                    val stampRect = RectF(MARGIN, signY, MARGIN + 180f, signY + 45f)
                    canvas.drawRoundRect(stampRect, 6f, 6f, bgPaint)

                    bgPaint.style = Paint.Style.STROKE
                    bgPaint.color = Color.rgb(200, 200, 200)
                    bgPaint.strokeWidth = 0.8f
                    canvas.drawRoundRect(stampRect, 6f, 6f, bgPaint)
                    bgPaint.style = Paint.Style.FILL

                    textPaint.textSize = 8.5f
                    textPaint.color = Color.rgb(80, 80, 80)
                    textPaint.textAlign = Paint.Align.CENTER
                    canvas.drawText("ختم واعتماد بنك الدم / المستشفى", MARGIN + 90f, signY + 16f, textPaint)
                }

                // --- تذييل الصفحة (Footer) ---
                val footerY = A4_HEIGHT - MARGIN + 10f
                bgPaint.color = Color.rgb(224, 224, 224)
                canvas.drawLine(MARGIN, footerY - 8f, A4_WIDTH - MARGIN, footerY - 8f, bgPaint)

                textPaint.textSize = 8f
                textPaint.color = Color.rgb(120, 120, 120)
                textPaint.textAlign = Paint.Align.RIGHT
                canvas.drawText("وثيقة رسمية مخصصة للأغراض الإنسانية والطبية فقط - يحظر النشر أو الاستخدام التجاري", tableRight, footerY + 2f, textPaint)

                textPaint.textAlign = Paint.Align.LEFT
                canvas.drawText("صفحة $pageNum من $totalPages", MARGIN, footerY + 2f, textPaint)

                pdfDocument.finishPage(page)
            }

            // كتابة الملف
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            pdfDocument.close()
        }
    }

    /**
     * توليد جدول بيانات إكسل (CSV متوافق 100% مع Microsoft Excel واللغة العربية)
     */
    suspend fun generateCsvReport(
        context: Context,
        donors: List<Donor>,
        title: String = "كشف متبرعي الدم",
        organizationName: String,
        governorate: String,
        filterSummary: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val reportsDir = File(context.cacheDir, "reports").apply { if (!exists()) mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(reportsDir, "donor_roster_$timeStamp.csv")

            val nowFormatted = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.forLanguageTag("ar")).format(Date())

            FileOutputStream(file).use { fos ->
                // كتابة الـ UTF-8 BOM لفتح الملف مباشرة باللغة العربية دون أي مشاكل ترميز في Excel
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    // الترويسة ومعلومات الكشف
                    writer.appendLine("\"# بنك دم اليمن - $title\"")
                    writer.appendLine("\"# الجهة المصدرة:\",\"${escapeCsv(organizationName)}\"")
                    writer.appendLine("\"# المحافظة:\",\"${escapeCsv(governorate)}\"")
                    writer.appendLine("\"# تاريخ الاستخراج:\",\"$nowFormatted\"")
                    writer.appendLine("\"# معايير الفلترة:\",\"${escapeCsv(filterSummary)}\"")
                    writer.appendLine("\"# إجمالي المتبرعين:\",\"${donors.size}\"")
                    writer.appendLine()

                    // رؤوس الأعمدة
                    writer.appendLine(
                        listOf(
                            "م",
                            "اسم المتبرع",
                            "فصيلة الدم",
                            "رقم الهاتف الأساسي",
                            "رقم الهاتف الثاني",
                            "رقم الهاتف الثالث",
                            "المحافظة",
                            "المديرية",
                            "العمر",
                            "الجنس",
                            "حالة الجاهزية",
                            "تاريخ آخر تبرع",
                            "موقوف حتى",
                            "ملاحظات"
                        ).joinToString(",") { "\"${escapeCsv(it)}\"" }
                    )

                    // البيانات
                    donors.forEachIndexed { index, donor ->
                        val genderLabel = if (donor.gender == "female") "أنثى" else "ذكر"
                        val statusLabel = when {
                            !donor.isActive -> "غير نشط"
                            donor.isSuspended -> "موقوف مؤقتاً"
                            donor.canDonateNow -> "جاهز للتبرع الآن"
                            else -> "في فترة راحة"
                        }

                        val row = listOf(
                            (index + 1).toString(),
                            donor.name,
                            donor.bloodType,
                            PhoneUtils.cleanLocalPhone(donor.phoneNumber),
                            PhoneUtils.cleanLocalPhone(donor.phoneNumber2),
                            PhoneUtils.cleanLocalPhone(donor.phoneNumber3),
                            donor.governorate,
                            donor.subDistrict,
                            donor.age.toString(),
                            genderLabel,
                            statusLabel,
                            donor.lastDonationDate ?: "",
                            donor.suspendedUntil ?: "",
                            donor.notes ?: ""
                        )

                        writer.appendLine(row.joinToString(",") { "\"${escapeCsv(it)}\"" })
                    }
                }
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * مشاركة التقرير المولد عبر نافذة مشاركة أندرويد (WhatsApp, Email, etc.)
     */
    fun shareReport(context: Context, file: File, mimeType: String, chooserTitle: String = "مشاركة الكشف") {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر مشاركة الملف: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * فتح التقرير المولد مباشرة في تطبيق خارجي (عارض PDF أو إكسل)
     */
    fun openReport(context: Context, file: File, mimeType: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(viewIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "لم يتم العثور على تطبيق لفتح هذا الملف", Toast.LENGTH_SHORT).show()
        }
    }

    private fun escapeCsv(text: String): String {
        return text.replace("\"", "\"\"")
    }
}
