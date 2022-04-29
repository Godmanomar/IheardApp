package com.novusvista.omar.iheardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_verify_email.*

class VerifyEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        buttonVerify.setOnClickListener{
            val intent= Intent(this ,SignInActivity::class.java)
            startActivity(intent)
            finish()

        }
    }


}
