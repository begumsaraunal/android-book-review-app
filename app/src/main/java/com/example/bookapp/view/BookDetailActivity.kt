/**
 * BookDetailActivity displays detailed information about a selected book.
 * Users can view the book cover, author, review and interact by liking
 * or deleting the book if they are the owner.
 */

package com.example.bookapp.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Base64
import android.view.View
import com.begumsaraunal.bookapp.databinding.ActivityBookDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.begumsaraunal.bookapp.R
import android.view.GestureDetector
import android.view.MotionEvent
class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private val database = Firebase.firestore

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth

        val bookId = intent.getStringExtra("id")
        val userId = auth.currentUser?.uid
        val bookName = intent.getStringExtra("bookName")
        val bookAuthor = intent.getStringExtra("bookAuthor")
        val bookContent = intent.getStringExtra("bookContent")
        val username = intent.getStringExtra("username")
        val image = intent.getStringExtra("bookPhotoBase64")

        binding.bookName.text = bookName
        binding.bookAuthor.text = bookAuthor
        binding.bookContent.text = bookContent
        binding.bookUser.text = "posted by $username"

        if (!image.isNullOrEmpty()) {

            val decodedBytes = Base64.decode(image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes,0,decodedBytes.size)

            binding.bookImage.setImageBitmap(bitmap)
            binding.backgroundImage.setImageBitmap(bitmap)


        } else {

            binding.bookImage.setImageResource(R.drawable.book_placeholder)
            binding.backgroundImage.setImageResource(R.drawable.book_placeholder)
        }

        val currentUser = auth.currentUser?.displayName

        // Handle like interaction and update like count in Firestore
        val likes = intent.getLongExtra("likes",0)
        binding.likeCount.text ="$likes likes"

        if (bookId != null) {

            database.collection("BookInformations")
                .document(bookId)
                .addSnapshotListener { snapshot, _ ->

                    val likedUsers = snapshot?.get("likedUsers") as? List<String> ?: listOf()

                    val userId = auth.currentUser?.uid

                    if (likedUsers.contains(userId)) {
                        binding.likeIcon.setImageResource(R.drawable.ic_heart_filled)
                    } else {
                        binding.likeIcon.setImageResource(R.drawable.outline_favorite_24)
                    }

                    val likes = snapshot?.getLong("likes") ?: 0
                    binding.likeCount.text = "$likes likes"
                }
        }

        binding.likeIcon.setOnClickListener {

            animateLike()

            if (bookId != null) {

                val ref = database.collection("BookInformations").document(bookId)
                val userId = auth.currentUser?.uid ?: return@setOnClickListener

                database.runTransaction { transaction ->

                    val snapshot = transaction.get(ref)

                    val likedUsers =
                        snapshot.get("likedUsers") as? MutableList<String> ?: mutableListOf()

                    var likes = snapshot.getLong("likes") ?: 0

                    val isLiked = likedUsers.contains(userId)

                    if (isLiked) {

                        likedUsers.remove(userId)
                        likes -= 1

                    } else {

                        likedUsers.add(userId)
                        likes += 1
                    }

                    transaction.update(ref, "likes", likes)
                    transaction.update(ref, "likedUsers", likedUsers)

                    Pair(likes, !isLiked)

                }.addOnSuccessListener { result ->

                    val newLikes = result.first
                    val nowLiked = result.second

                    binding.likeCount.text = "$newLikes likes"

                    if (nowLiked) {
                        binding.likeIcon.setImageResource(R.drawable.ic_heart_filled)
                    } else {
                        binding.likeIcon.setImageResource(R.drawable.outline_favorite_24)
                    }
                }
            }
        }

        val gestureDetector = GestureDetector(this,
            object : GestureDetector.SimpleOnGestureListener() {

                override fun onDoubleTap(e: MotionEvent): Boolean {

                    performLike()

                    showBigHeart()

                    return true
                }
            })

        binding.bookImage.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        if (currentUser == username) {
            binding.deleteButton.visibility = View.VISIBLE
        }
        else {
            binding.deleteButton.visibility = View.GONE
        }


        // Delete book if the current user is the owner
        binding.deleteButton.setOnClickListener {

            if (bookId != null) {

                database.collection("BookInformations")
                    .document(bookId)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(this,"Book deleted",Toast.LENGTH_LONG).show()
                        finish()

                    }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun animateLike() {
        binding.likeIcon.animate()
            .scaleX(1.4f)
            .scaleY(1.4f)
            .setDuration(150)
            .withEndAction {
                binding.likeIcon.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }.start()
    }
    // Handle like interaction and update like count in Firestore
    private fun performLike() {

        val bookId = intent.getStringExtra("id") ?: return
        val userId = auth.currentUser?.uid ?: return

        val ref = database.collection("BookInformations").document(bookId)

        database.runTransaction { transaction ->

            val snapshot = transaction.get(ref)

            val likedUsers =
                snapshot.get("likedUsers") as? MutableList<String> ?: mutableListOf()

            var likes = snapshot.getLong("likes") ?: 0

            if (likedUsers.contains(userId)) {

                likedUsers.remove(userId)
                likes -= 1

            } else {

                likedUsers.add(userId)
                likes += 1
            }

            transaction.update(ref,"likes",likes)
            transaction.update(ref,"likedUsers",likedUsers)

        }
    }

    private fun showBigHeart() {

        binding.bigHeart.bringToFront()
        binding.bigHeart.visibility = View.VISIBLE


        binding.bigHeart.animate()
            .alpha(1f)
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(200)
            .withEndAction {

                binding.bigHeart.animate()
                    .alpha(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .withEndAction {

                        binding.bigHeart.visibility = View.GONE

                    }.start()
            }.start()
    }


}