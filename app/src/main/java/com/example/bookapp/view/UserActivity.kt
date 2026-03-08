/**
 * UserActivity displays the main book feed of the application.
 * Users can view books shared by other users and navigate to
 * book details or add a new book.
 */

package com.example.bookapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.begumsaraunal.bookapp.R
import com.begumsaraunal.bookapp.R.*
import com.example.bookapp.adapter.RecyclerAdapter
import com.begumsaraunal.bookapp.databinding.ActivityUserBinding
import com.example.bookapp.data.Book
import com.example.bookapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserActivity : AppCompatActivity()
{
    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUserBinding
    // RecyclerView adapter for displaying books
    private lateinit var adapter: RecyclerAdapter
    // ViewModel responsible for managing book data
    private lateinit var viewModel: UserViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        //create an inflater to bind the menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_bar,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == id.logout_menu) //when logout item selected
        {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        else if (item.itemId == id.add_book_menu) //when add book item selected
        {
            //go adding book page(activity)
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Creates the data binding and places it on the screen.
        binding = ActivityUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Creates a toolbar and adds it to the screen.
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        adapter = RecyclerAdapter { selectedBook ->
            handleBookClick(selectedBook)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        viewModel.books.observe(this) { bookList ->
            adapter.submitList(bookList)
        }
    }
    // Open book detail screen when a book item is clicked
    private fun handleBookClick(book: Book) {

        val currentUser = auth.currentUser?.displayName

            val intent = Intent(this, BookDetailActivity::class.java)

            intent.putExtra("id", book.id)
            intent.putExtra("username", book.username)
            intent.putExtra("bookName", book.bookName)
            intent.putExtra("bookAuthor", book.bookAuthor)
            intent.putExtra("bookContent", book.bookContent)
            intent.putExtra("bookPhotoBase64", book.bookPhotoBase64)
            intent.putExtra("likes",book.likes)
            startActivity(intent)

    }
}
