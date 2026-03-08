/**
 * RecyclerAdapter binds book data to RecyclerView items.
 * Each item represents a book shared by users.
 */

package com.example.bookapp.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.begumsaraunal.bookapp.databinding.RecyclerRowBinding
import com.example.bookapp.data.Book
import kotlin.math.absoluteValue

class RecyclerAdapter(
    private val onItemClick: (Book) -> Unit
) : ListAdapter<Book, RecyclerAdapter.BookHolder>(DiffCallback()) {

    class BookHolder(
        val binding: RecyclerRowBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val binding = RecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookHolder(binding)
    }

    // Bind book data to RecyclerView item views
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookHolder, position: Int) {

        val book = getItem(position)

        holder.binding.recyclerRowUsername.text = book.username
        holder.binding.recyclerRowBookName.text = book.bookName
        holder.binding.recyclerRowBookAuthor.text = book.bookAuthor
        holder.binding.recyclerRowBookContext.text = book.bookContent
        holder.binding.recyclerRowLikes.text = "❤️ ${book.likes}"

        if (book.bookPhotoBase64 != null) {

            val bitmap = decodeBase64ToBitmap(book.bookPhotoBase64!!)
            holder.binding.recyclerRowImageView.setImageBitmap(bitmap)

            holder.binding.recyclerRowImageView.visibility = android.view.View.VISIBLE
            holder.binding.recyclerRowLetter.visibility = android.view.View.GONE

        } else {

            val firstLetter = book.bookName?.firstOrNull()?.uppercaseChar() ?: 'B'
            holder.binding.recyclerRowLetter.text = firstLetter.toString()

            holder.binding.recyclerRowLetter.visibility = android.view.View.VISIBLE
            holder.binding.recyclerRowImageView.visibility = android.view.View.GONE
            holder.binding.recyclerRowCoverContainer.background
                .setTint(getStableColor(book.bookName))
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    private fun getStableColor(key: String?): Int {
        val colors = listOf(
            0xFF2563EB.toInt(),
            0xFF0F766E.toInt(),
            0xFF9333EA.toInt(),
            0xFFEA580C.toInt(),
            0xFFBE123C.toInt()
        )

        return colors[(key?.hashCode() ?: 0).absoluteValue % colors.size]
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    class DiffCallback : DiffUtil.ItemCallback<Book>() {

        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
