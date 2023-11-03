package com.myapplication.valdistoryapp.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.myapplication.valdistoryapp.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

const val OFFLINE_ERROR_CODE = 400

fun String.isEmail(): Boolean {
    val emailRegex =
        Regex("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))\$")
    return this.matches(emailRegex)
}

fun AppCompatActivity.moveToActivity(
    destinationActivityClass: Class<*>,
    withAnimation: Boolean = false
) {
    val destinationActivityIntent = Intent(this, destinationActivityClass)

    if (withAnimation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        startActivity(
            destinationActivityIntent,
            ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        )
    } else {
        startActivity(destinationActivityIntent)
    }
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("GMT")
    val date = format.parse(this) as Date
    return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date)
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun EditText.afterTextChanged(afterTextChangedListener: (s: Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Nothing to do here...
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Nothing to do here...l
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChangedListener(s)
        }
    })
}

fun showSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }

        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")

    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()

    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir

    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int

    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }

    outputStream.close()
    inputStream.close()

    return myFile
}


fun File.reduceFileImage(): File {
    val MAXIMAL_SIZE = 1000000

    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)

    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}