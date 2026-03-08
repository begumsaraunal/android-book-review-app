/**
 * CreateAccountActivity allows new users to register an account.
 * It creates a Firebase Authentication user and stores the username
 * in the user's Firebase profile.
 */

package com.example.bookapp.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.begumsaraunal.bookapp.databinding.ActivityCreateAccountBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class CreateAccountActivity : AppCompatActivity()
{
    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth
    private  lateinit var binding : ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Initializes Firebase
        FirebaseApp.initializeApp(this)

        //Creates the data binding and places it on the screen.
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initializes the Firebase authentication (auth) instance.
        auth = Firebase.auth

        //for automatic login
        val user = auth.currentUser
        //current user is nullable check this
        if(user != null)
        {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    // Register a new user using Firebase Authentication
    fun register(view: View)
    {
        val email = binding.mailText.text.toString().trim()
        val password = binding.passwordText.text.toString().trim()
        val username = binding.usernameText.text.toString().trim()

        if(email.isEmpty() || password.isEmpty() || username.isEmpty())
        {
            Toast.makeText(
                applicationContext,
                "Email, password and username cannot be empty!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // password security check
        if(password.length < 6)
        {
            Toast.makeText(
                applicationContext,
                "Password must be at least 6 characters",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // username check
        if(username.length < 3)
        {
            Toast.makeText(
                applicationContext,
                "Username must be at least 3 characters",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // firebase register
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Update Firebase profile with username
                if (task.isSuccessful)
                {
                    val user = auth.currentUser

                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {

                        Toast.makeText(
                            applicationContext,
                            "Account created successfully 🎉",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun goLogin(view: View)
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}