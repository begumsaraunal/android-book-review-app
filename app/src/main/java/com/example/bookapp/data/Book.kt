/**
 * Data model representing a book shared by a user.
 * Contains book information, optional image data and like count.
 */

package com.example.bookapp.data

data class Book(
    var id: String? = null,
    var username: String? = null,
    var bookName: String? = null,
    var bookAuthor: String? = null,
    var bookContent: String? = null,
    var bookPhotoBase64: String? = null,
    val likes: Long =0
)