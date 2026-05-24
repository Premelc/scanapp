import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

fun main() {
    val writer = MultiFormatWriter()
    val value = "Sample PDF417 content 12345"
    val sizes = listOf(
        840 to 352,
        840 to 840,
        600 to 200,
        1000 to 300,
        500 to 500,
    )
    for ((w,h) in sizes) {
        try {
            val m = writer.encode(value, BarcodeFormat.PDF_417, w, h)
            println("OK ${w}x$h -> matrix ${m.width}x${m.height}")
        } catch (e: Exception) {
            println("FAIL ${w}x$h -> ${e.message}")
        }
    }
}
