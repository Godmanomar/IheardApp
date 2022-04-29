package com.novusvista.omar.iheardapp


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.textViewResponse
import kotlinx.android.synthetic.main.activity_verify_email.*
import java.util.concurrent.Delayed


class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)


        buttonReset.setOnClickListener {
           val handler=Handler()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            if (editTextEmail.text.toString().isNullOrEmpty())
                Toast.makeText(this,"Email Address is not provided",Toast.LENGTH_LONG).show()
               // textViewResponse.text = "Email Address is not provided"

            else {
                auth.sendPasswordResetEmail(
                    editTextEmail.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                          val  user = auth.currentUser
                            textViewResponse.text = "Reset Password Link is mailed"
                        } else
                            textViewResponse.text = "Password Reset mail could not be sent"
                    }

    }
            handler.postDelayed({val intent= Intent(this ,SignInActivity::class.java)
                startActivity(intent)
                finish()},2000)

        }
    }
}