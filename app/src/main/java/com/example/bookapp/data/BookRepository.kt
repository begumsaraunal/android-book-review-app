/**
 * BookRepository manages all Firestore database operations
 * related to books such as adding, retrieving and deleting books.
 */
package com.example.bookapp.data

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BookRepository {

    // Firebase Authentication instance
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun addBook(
        bookName: String,
        bookAuthor: String,
        bookContent: String,
        base64Image: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )    {
        val bookMap = hashMapOf(
            "username" to auth.currentUser?.displayName,
            "bookName" to bookName,
            "bookAuthor" to bookAuthor,
            "bookContent" to bookContent,
            "date" to Timestamp.now(),
            "likes" to 0,
            "likedUsers" to listOf<String>()
        )
        base64Image?.let {
            bookMap["bookPhotoBase64"] = it
        }

        database.collection("BookInformations")
            .add(bookMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
    }

    fun getBooks(): MutableLiveData<List<Book>> {

        val bookList = MutableLiveData<List<Book>>()

        firestore.collection("BookInformations")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    bookList.value = emptyList()

                } else {

                    val books = snapshot?.documents?.map { document ->

                        Book(
                            id = document.id,
                            username = document.getString("username"),
                            bookName = document.getString("bookName"),
                            bookAuthor = document.getString("bookAuthor"),
                            bookContent = document.getString("bookContent"),
                            bookPhotoBase64 = document.getString("bookPhotoBase64"),
                            likes = document.getLong("likes") ?: 0
                        )

                    } ?: emptyList()

                    bookList.value = books
                }
            }

        return bookList
    }
    fun deleteBook(
        bookId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        database.collection("BookInformations")
            .document(bookId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Error")
            }
    }

}