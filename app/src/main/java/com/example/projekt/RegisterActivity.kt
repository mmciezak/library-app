package com.example.projekt

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.projekt.databinding.ActivityMainBinding
import com.example.projekt.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebase:FirebaseAuth
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebase = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.registerButton.setOnClickListener{
            //register new user
            validateData()
        }
    }

    var name=""
    var email=""
    var password=""
    var passwordConfirm=""
    private fun validateData()
    {
        name = binding.usernameInput.text.toString().trim()
        email = binding.emailInput.text.toString().trim()
        password = binding.passwordInput.text.toString().trim()
        passwordConfirm = binding.passwordConfirmInput.text.toString().trim()

        val countDigits = password.count { it.isDigit() }
        val containsSpecialCharacter = !password.matches("[A-Za-z0-9 ]*".toRegex())


        if(name.isEmpty())
        {
            Toast.makeText(this,"Invalid username", Toast.LENGTH_SHORT).show()
        }
        else if(email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email).matches()))
        {
            Toast.makeText(this,"Invalid email", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty() || passwordConfirm.isEmpty())
        {
            Toast.makeText(this,"Invalid password", Toast.LENGTH_SHORT).show()
        }
        else if(password!=passwordConfirm)
        {
            Toast.makeText(this,"Password doesn't match", Toast.LENGTH_SHORT).show()
        }
        else if(countDigits<=0 || !containsSpecialCharacter)
        {
            Toast.makeText(this,"Password must contain at least one number and one special character", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this,"Creating account...", Toast.LENGTH_SHORT).show()
            createAccount()

        }


    }

    fun createAccount()
    {
        progressDialog.setMessage("Loading")
        progressDialog.show()

        firebase.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val userInfo: HashMap<String,Any?> = HashMap()
                userInfo["uid"] = firebase.uid
                userInfo["username"] = name
                userInfo["email"] = email
                userInfo["profileImage"] = ""
                userInfo["admin"] = 0
                userInfo["timestamp"] = System.currentTimeMillis()

                FirebaseDatabase.getInstance().getReference("Users").child(firebase.uid!!).setValue(userInfo)
                progressDialog.dismiss()
                Toast.makeText(this,"Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, AdminPage::class.java))

            }
            .addOnFailureListener {
                Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

            }
    }
}