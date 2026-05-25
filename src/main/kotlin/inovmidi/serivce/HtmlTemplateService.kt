package inovmidi.serivce

import inovmidi.entity.Article
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class HtmlTemplateService {

    fun generateArticleHtml(article: Article): String {
        val persianDate = article.createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

        return """
<!DOCTYPE html>
<html lang="fa" dir="rtl" class="scroll-smooth">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${escapeHtml(article.title)}</title>
    <link href="https://cdn.jsdelivr.net/gh/rastikerdar/vazirmatn@v33.003/Vazirmatn-Variable-font-face.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body { font-family: 'Vazirmatn', sans-serif; }
        .text-justify { text-align: justify; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 leading-relaxed">

    <nav class="sticky top-0 bg-white/95 backdrop-blur-sm border-b border-slate-200 z-50">
        <div class="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
            <span class="font-bold text-lg text-blue-800">نشریه علمی پژوهشی</span>
            <ul class="flex gap-6 text-sm font-medium">
                <li><a href="#abstract" class="hover:text-blue-600 transition">چکیده</a></li>
                <li><a href="#content" class="hover:text-blue-600 transition">محتوا</a></li>
            </ul>
        </div>
    </nav>

    <main class="max-w-6xl mx-auto px-4 py-12 grid grid-cols-1 lg:grid-cols-12 gap-12">
        
        <article class="lg:col-span-8">
            <header class="mb-12">
                <h1 class="text-3xl lg:text-4xl font-extrabold mb-6 leading-tight">${escapeHtml(article.title)}</h1>
                <div class="flex flex-wrap gap-4 text-slate-600">
                    <p>نویسنده: ${escapeHtml(article.author)}</p>
                </div>
                <div class="mt-4 p-3 bg-slate-100 rounded text-sm text-slate-500 inline-block">
                    شناسه مقاله: ${article.id}
                </div>
            </header>

            <section id="abstract" class="mb-10">
                <h2 class="text-2xl font-bold mb-4 border-r-4 border-blue-600 pr-3">چکیده</h2>
                <p class="text-justify">${formatContent(article.abstract)}</p>
            </section>

            <section id="content" class="mb-10">
                <h2 class="text-2xl font-bold mb-4 border-r-4 border-blue-600 pr-3">محتوای مقاله</h2>
                <div class="text-justify space-y-4">
                    ${formatContent(article.content)}
                </div>
            </section>
        </article>

        <aside class="lg:col-span-4 space-y-8">
            <div class="p-6 bg-blue-50 rounded-lg border border-blue-100">
                <h3 class="font-bold mb-2">اطلاعات مقاله</h3>
                <p class="text-sm text-slate-700">تعداد کلمات: ${article.wordCount}</p>
                <p class="text-sm text-slate-700 mt-2">زبان: ${if (article.language == "fa") "فارسی" else article.language}</p>
            </div>
            <div class="p-6 border border-slate-200 rounded-lg">
                <h3 class="font-bold mb-2">جزئیات</h3>
                <p class="text-xs text-slate-500">تاریخ ایجاد: $persianDate</p>
                <p class="text-xs text-slate-500 mt-2">کلیدواژه‌ها: ${escapeHtml(article.keywords)}</p>
                <p class="text-xs text-slate-500 mt-2">وضعیت: ${if (article.isPublished) "منتشر شده" else "پیش‌نویس"}</p>
            </div>
        </aside>

    </main>
</body>
</html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun formatContent(content: String): String {
        // تبدیل markdown به HTML (ساده)
        return content
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .split("\n\n")
            .joinToString("") { paragraph ->
                if (paragraph.trim().isNotEmpty()) {
                    val formatted = paragraph
                        .replace(Regex("\\*\\*(.+?)\\*\\*"), "<strong>$1</strong>")
                        .replace(Regex("\\*(.+?)\\*"), "<em>$1</em>")
                        .replace(Regex("^###\\s+(.+)$", RegexOption.MULTILINE), "<h3 class='text-xl font-bold mt-6 mb-3'>$1</h3>")
                        .replace(Regex("^##\\s+(.+)$", RegexOption.MULTILINE), "<h2 class='text-2xl font-bold mt-8 mb-4'>$1</h2>")
                        .replace("\n", "<br>")
                    "<p class='mb-4'>$formatted</p>"
                } else ""
            }
    }
}
