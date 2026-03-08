/**
 * DeleteViewModel handles book deletion operations.
 * It communicates with the BookRepository to remove
 * books from Firestore.
 */

package com.example.bookapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bookapp.data.BookRepository

class DeleteViewModel: ViewModel() {
    // Repository instance for database operations
    private val repository = BookRepository()

    // Delete book by its document id
    fun deleteBook(
        bookName: String,
        onSuccess:() -> Unit,
        onError: (String) -> Unit
    ) {
        repository.deleteBook(bookName,onSuccess,onError)
    }
}