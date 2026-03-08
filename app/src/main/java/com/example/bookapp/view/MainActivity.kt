/**
 * MainActivity is the entry screen of the application.
 * It allows users to log in using Firebase Authentication
 * or navigate to the account creation screen.
 */

package com.example.bookapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.begumsaraunal.bookapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity()
{

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    // ViewBinding instance for accessing layout views
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase authentication
        auth = Firebase.auth

        // Check if a user is already logged in
        val user = auth.currentUser

        if(user != null)
        {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Attempt user login using Firebase Authentication
    fun login(view: View)
    {
        val email = binding.mailText.text.toString()
        val password = binding.passwordText.text.toString()

        // Validate email and password fields
        if(email.isNotEmpty() && password.isNotEmpty())
        {
            auth.signInWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener { task ->

                if (task.isSuccessful)
                {
                    val user = auth.currentUser?.displayName.toString()

                    Toast.makeText(
                        applicationContext,
                        "Welcome back $user",
                        Toast.LENGTH_LONG
                    ).show()

                    binding.loading.visibility = View.VISIBLE

                    // Navigate to UserActivity after successful login
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }.addOnFailureListener {

                binding.loading.visibility = View.GONE

                Toast.makeText(
                    applicationContext,
                    it.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        else
        {
            binding.loading.visibility = View.GONE

            Toast.makeText(
                applicationContext,
                "User is not found. Please sign in.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Navigate to CreateAccountActivity
    fun createAccount(view: View)
    {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
        finish()
    }
}