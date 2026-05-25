package inovmidi.serivce

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import inovmidi.entity.Article
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

@Service
class PdfService {

    fun generatePdf(article: Article): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val writer = PdfWriter(outputStream)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)

        try {
            // بارگذاری فونت فارسی
            val fontPath = javaClass.getResource("/fonts/Vazirmatn-Regular.ttf")?.path
                ?: throw RuntimeException("Font file not found")

            val persianFont: PdfFont = PdfFontFactory.createFont(
                fontPath,
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            )

            // عنوان مقاله
            document.add(
                Paragraph(Text(article.title).setFont(persianFont))
                    .setFontSize(20f)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(15f)
            )

            // نویسنده
            document.add(
                Paragraph(Text("نویسنده: ${article.author}").setFont(persianFont))
                    .setFontSize(12f)
                    .setItalic()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(5f)
            )

            // تاریخ ایجاد
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
            document.add(
                Paragraph(Text("تاریخ ایجاد: ${article.createdAt.format(dateFormatter)}").setFont(persianFont))
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(5f)
            )

            // شناسه مقاله
            document.add(
                Paragraph(Text("شناسه مقاله: ${article.id}").setFont(persianFont))
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(5f)
            )

            // کلیدواژه‌ها
            document.add(
                Paragraph(Text("کلیدواژه‌ها: ${article.keywords}").setFont(persianFont))
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(5f)
            )

            // زبان
            document.add(
                Paragraph(Text("زبان: ${article.language}").setFont(persianFont))
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(5f)
            )

            // تعداد کلمات
            document.add(
                Paragraph(Text("تعداد کلمات: ${article.wordCount}").setFont(persianFont))
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20f)
            )

            // چکیده
            document.add(
                Paragraph(Text("چکیده").setFont(persianFont))
                    .setFontSize(14f)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(10f)
            )
            document.add(
                Paragraph(Text(article.abstract).setFont(persianFont))
                    .setFontSize(11f)
                    .setTextAlignment(TextAlignment.JUSTIFIED)
                    .setMarginBottom(20f)
            )

            // محتوای مقاله
            document.add(
                Paragraph(Text("محتوای مقاله").setFont(persianFont))
                    .setFontSize(14f)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(10f)
            )

            // تبدیل markdown به متن ساده و اضافه کردن به PDF
            val contentParagraphs = article.content.split("\n\n")
            for (para in contentParagraphs) {
                if (para.trim().isNotEmpty()) {
                    val cleanText = para
                        .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1") // حذف bold
                        .replace(Regex("\\*(.+?)\\*"), "$1") // حذف italic
                        .replace(Regex("^###\\s+"), "") // حذف h3
                        .replace(Regex("^##\\s+"), "") // حذف h2
                        .replace(Regex("^#\\s+"), "") // حذف h1
                        .trim()

                    document.add(
                        Paragraph(Text(cleanText).setFont(persianFont))
                            .setFontSize(11f)
                            .setTextAlignment(TextAlignment.JUSTIFIED)
                            .setMarginBottom(10f)
                    )
                }
            }

        } catch (e: Exception) {
            throw RuntimeException("خطا در تولید PDF: ${e.message}", e)
        } finally {
            document.close()
        }

        return outputStream.toByteArray()
    }
}
