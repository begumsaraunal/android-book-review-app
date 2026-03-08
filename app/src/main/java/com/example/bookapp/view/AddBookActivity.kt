/**
 * AddBookActivity allows users to create a new book post.
 * Users can enter book information and optionally upload
 * a book cover image.
 */

package com.example.bookapp.view

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.createSource
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.begumsaraunal.bookapp.R
import com.begumsaraunal.bookapp.databinding.ActivityAddBookBinding
import com.example.bookapp.data.BookRepository
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding

    private var chosenBitmap: Bitmap? = null

    private val bookRepository = BookRepository()

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    // Modern Image Picker
    @RequiresApi(Build.VERSION_CODES.P)
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val source = createSource(contentResolver, uri)
                chosenBitmap = ImageDecoder.decodeBitmap(source)
                binding.imageView.setImageBitmap(chosenBitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    // Toolbar Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_menu) {
            auth.signOut()
            finish()
        }
        return true
    }

    // Image Click
    @RequiresApi(Build.VERSION_CODES.P)
    fun addImage(view: View) {
        imagePicker.launch("image/*")
    }

    // // Save book information to Firestore database
    fun save(view: View) {

        val bookName = binding.titleText.text.toString()
        val bookAuthor = binding.authorText.text.toString()
        val bookContent = binding.contentText.text.toString()

        if (bookName.isEmpty() || bookAuthor.isEmpty() || bookContent.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
            return
        }

        val base64Image = chosenBitmap?.let { encodeImageToBase64(it) }


        bookRepository.addBook(
            bookName,
            bookAuthor,
            bookContent,
            base64Image,
            onSuccess = {
                Toast.makeText(this,"Book Added", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = {
                Toast.makeText(this,it, Toast.LENGTH_LONG).show()
            }
        )
    }

    // Bitmap → Base64
    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

}