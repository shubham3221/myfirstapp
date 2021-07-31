package com.example.myfirstapp.extentions

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.annotation.StyleRes
import java.io.File
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

inline fun <reified T> T?.safe(default: T) = this ?: default
/**
 * If the string is a HTTP URL (ie. Starts with http:// or https://)
 */
fun String.isHttp(): Boolean {
    return this.matches(Regex("(http|https)://[^\\s]*"))
}
/**
 * Take some text, highlight some text with a color and add a click listener to it
 * @param source Text to click
 * @param color Color to change too, default = null,
 * @param onClick Callback
 */
fun SpannableString.onClick(source: String, shouldUnderline: Boolean = true, shouldBold: Boolean = true, color: Int? = null, textView: TextView? = null, onClick: () -> Unit): SpannableString {
    val startIndex = this.toString().indexOf(source)
    if (startIndex == -1) {
        throw Exception("Cannot highlight this title as $source is not contained with $this")
    }
    this.setSpan(object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            if (color != null) {
                ds.color = color
                ds.bgColor = Color.TRANSPARENT
            }
        }

        override fun onClick(widget: View) {
            onClick()
            textView?.clearFocus()
            textView?.invalidate()
        }
    }, startIndex, startIndex + source.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (shouldUnderline) {
        this.setSpan(UnderlineSpan(), startIndex, startIndex + source.length, 0)
    }
    if (shouldBold) {
        this.setSpan(StyleSpan(Typeface.BOLD), startIndex, startIndex + source.length, 0)
    }
    return this
}
/**
 * Highlight a given word in a string with a given colour
 */
fun String.highlight(source: String, color: Int): SpannableString {
    val startIndex = this.indexOf(source)
    if (startIndex == -1) {
        throw Exception("Cannot highlight this title as $source is not contained with $this")
    }
    val spannable = SpannableString(this)
    spannable.setSpan(object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = color
        }

        override fun onClick(widget: View) {}
    }, startIndex, startIndex + source.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannable
}

val String.decodeBase64: String get() = Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)
val String.encodeBase64: String get() = Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT)

fun String?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

val String.containsLetters get() = matches(".*[a-zA-Z].*".toRegex())

val String.containsNumbers get() = matches(".*[0-9].*".toRegex())


val String.isAlphanumeric get() = matches("^[a-zA-Z0-9]*$".toRegex())

fun CharSequence.isEmail() = isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


val String.isAlphabetic get() = matches("^[a-zA-Z]*$".toRegex())

fun String.wrapInQuotes(): String {
    var formattedConfigString: String = this
    if (!startsWith("\"")) {
        formattedConfigString = "\"$formattedConfigString"
    }
    if (!endsWith("\"")) {
        formattedConfigString = "$formattedConfigString\""
    }
    return formattedConfigString
}

inline val String.isIp: Boolean
    get() {
        val p = Pattern.compile(
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}")
        val m = p.matcher(this)
        return m.find()
    }
fun String.unwrapQuotes(): String {
    var formattedConfigString: String = this
    if (formattedConfigString.startsWith("\"")) {
        if (formattedConfigString.length > 1) {
            formattedConfigString = formattedConfigString.substring(1)
        } else {
            formattedConfigString = ""
        }
    }
    if (formattedConfigString.endsWith("\"")) {
        if (formattedConfigString.length > 1) {
            formattedConfigString =
                formattedConfigString.substring(0, formattedConfigString.length - 1)
        } else {
            formattedConfigString = ""
        }
    }
    return formattedConfigString
}

val String.mostCommonCharacter: Char?
    get() {
        if (length == 0) return null
        val map = HashMap<Char, Int>()
        for (char in toCharArray()) map[char] = (map[char] ?: 0) + 1
        var maxEntry = map.entries.elementAt(0)
        for (entry in map) maxEntry = if (entry.value > maxEntry.value) entry else maxEntry
        return maxEntry.key
    }

fun String.removeFirstChar(): String {
    return if (this.isEmpty()) {
        ""
    } else {
        this.substring(1)
    }
}

fun String.removeLastCharacter(): String {
    return if (this.isEmpty()) {
        ""
    } else {
        this.substring(0, this.length - 1)
    }
}


fun <T> T.concatAsString(b: T): String {
    return this.toString() + b.toString()
}


/**
 * Get md5 string.
 */
val String.md5 get() = encrypt(this, "MD5")

/**
 * Get sha1 string.
 */
val String.sha1 get() = encrypt(this, "SHA-1")


/**
 * Check if String is Phone Number.
 */
val String.isPhone: Boolean
    get() {
        val p = "^1([34578])\\d{9}\$".toRegex()
        return matches(p)
    }

/**
 * Check if String is Email.
 */
val String.isEmail: Boolean
    get() {
        val p = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)\$".toRegex()
        return matches(p)
    }


/**
 * Check if String is Number.
 */
val String.isNumeric: Boolean
    get() {
        val p = "^[0-9]+$".toRegex()
        return matches(p)
    }


/**
 * Method to check String equalsIgnoreCase
 */
fun String.equalsIgnoreCase(other: String) = this.toLowerCase().contentEquals(other.toLowerCase())


/**
 * Returns a new File Object with the Current String as Its path
 */
val String.asFile get() = File(this)


/**
 * Returns a new File Object with the Current String as Its path
 */
fun String.asFile() = File(this)


/**
 * Encode String to URL
 */
val String.encodeToUrlUTF8: String
    get() {
        return URLEncoder.encode(this, "UTF-8")
    }

/**
 * Decode String to URL
 */
val String.decodeToUrlUTF8: String
    get() {
        return URLDecoder.decode(this, "UTF-8")
    }

/**
 * Encode String to URL
 */
fun String.encodeToUrl(charSet: String = "UTF-8"): String = URLEncoder.encode(this, charSet)

/**
 * Decode String to URL
 */
fun String.decodeToUrl(charSet: String = "UTF-8"): String = URLDecoder.decode(this, charSet)

/**
 **
 * Encrypt String to AES with the specific Key
 */
fun String.encryptAES(key: String): String {
    var crypted: ByteArray? = null
    try {
        val skey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, skey)
        crypted = cipher.doFinal(toByteArray())
    } catch (e: Exception) {
        println(e.toString())
    }
    return String(Base64.encode(crypted, Base64.DEFAULT))
}

/**
 * Decrypt String to AES with the specific Key
 */
fun String.decryptAES(key: String): String {
    var output: ByteArray? = null
    try {
        val skey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, skey)
        output = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
    } catch (e: Exception) {
        println(e.toString())
    }
    return output?.let { String(it) } ?: ""
}

/**
 * encode The String to Binary
 */
fun String.encodeToBinary(): String {
    val stringBuilder = StringBuilder()
    toCharArray().forEach {
        stringBuilder.append(Integer.toBinaryString(it.toInt()))
        stringBuilder.append(" ")
    }
    return stringBuilder.toString()
}

/**
 * Decode the String from binary
 */
val String.deCodeToBinary: String
    get() {
        val stringBuilder = StringBuilder()
        split(" ").forEach {
            stringBuilder.append(Integer.parseInt(it.replace(" ", ""), 2))
        }
        return stringBuilder.toString()
    }

/**
 * Save String to a Given File
 */
fun String.saveToFile(file: File) = FileOutputStream(file).bufferedWriter().use {
    it.write(this)
    it.flush()
    it.close()
}

/**
 * Method to convert byteArray to String.
 */
private fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}

/**
 * Method to get encrypted string.
 */
private fun encrypt(string: String?, type: String): String {
    if (string.isNullOrEmpty()) {
        return ""
    }
    val md5: MessageDigest
    return try {
        md5 = MessageDigest.getInstance(type)
        val bytes = md5.digest(string.toByteArray())
        bytes2Hex(bytes)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}

/**
 * Clear all HTML tags from a string.
 */
fun String.clearHtmlTags(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this).toString()
    }
}

/**
 * Returns true if this string contains exactly the provided string.
 * This method uses RegEx to evaluate and its case-sensitive. What makes it different from the classic
 * [contains] is that it doesn't uses [indexOf], hence it's more performant when used on long char sequences
 * and has much higher probabilities of not returning false positives per approximation.
 */
fun String.containsExact(string: String): Boolean =
    Pattern.compile("(?<=^|[^a-zA-Z0-9])\\Q$string\\E(?=\$|[^a-zA-Z0-9])")
        .matcher(this)
        .find()

/**
 * Converts a string to boolean such as 'Y', 'yes', 'TRUE'
 */

fun String.toBoolean(): Boolean {
    return this != "" &&
            (this.equals("TRUE", ignoreCase = true)
                    || this.equals("Y", ignoreCase = true)
                    || this.equals("YES", ignoreCase = true))
}

/**
 * Converts string to camel case. Handles multiple strings and empty strings
 */
fun String.convertToCamelCase(): String {
    var titleText = ""
    if (this.isNotEmpty()) {
        val words = this.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        words.filterNot { it.isEmpty() }
            .map { it.substring(0, 1).toUpperCase() + it.substring(1).toLowerCase() }
            .forEach { titleText += "$it " }
    }
    return titleText.trim { it <= ' ' }
}

fun String.ellipsize(at: Int): String {
    if (this.length > at) {
        return this.substring(0, at) + "..."
    }
    return this
}

@Suppress("DEPRECATION")
fun String.htmlToSpanned(): Spanned {
    return if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

/**
 * Normalize string - convert to lowercase, replace diacritics and trim trailing whitespaces
 */
fun String.normalize(): String {
    return Normalizer.normalize(toLowerCase(), Normalizer.Form.NFD)
        .replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "").trim()
}

/**
 * Highlight substring [query] in this spannable with custom [style]. All occurrences of this substring
 * are stylized
 */
fun Spannable.highlightSubstring(query: String, @StyleRes style: Int): Spannable {
    val spannable = Spannable.Factory.getInstance().newSpannable(this)
    val substrings = query.toLowerCase().split("\\s+".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
    var lastIndex = 0
    for (i in substrings.indices) {
        do {
            lastIndex = this.toString().toLowerCase().indexOf(substrings[i], lastIndex)
            if (lastIndex != -1) {
                spannable.setSpan(StyleSpan(style), lastIndex, lastIndex + substrings[i].length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                lastIndex++
            }
        } while (lastIndex != -1)
    }
    return spannable
}

fun String?.urlEncode(charsetName: String = "UTF-8"): String {
    if (this.isNullOrEmpty()) return ""
    try {
        return URLEncoder.encode(this, charsetName)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}

fun String?.urlDecode(charsetName: String = "UTF-8"): String {
    if (this.isNullOrEmpty()) return ""
    try {
        return URLDecoder.decode(this, charsetName)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}