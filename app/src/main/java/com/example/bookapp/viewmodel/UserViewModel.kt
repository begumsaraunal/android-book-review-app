/**
 * UserViewModel provides book data to the UserActivity UI.
 * It communicates with the BookRepository to fetch books
 * from Firebase Firestore.
 */

package com.example.bookapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.bookapp.data.Book
import com.example.bookapp.data.BookRepository

class UserViewModel: ViewModel() {

    // Repository responsible for book related database operations
    private val repository = BookRepository()
    // LiveData list of books observed by the UI
    val books: LiveData<List<Book>> = repository.getBooks()

}