package com.novusvista.omar.iheardapp
import android.view.View
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*


import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)


        signup_link_btn.setOnClickListener {

            startActivity(Intent(this, SignUpActivity::class.java))
        }
        login_button.setOnClickListener {
            loginUser()
        }

        forgot_password_link.setOnClickListener {

            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        email_login.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                    if(email_login.text.toString().trim().matches(emailPattern))
                    {
                          wrong_email.visibility=View.INVISIBLE

                    }else
                    {
                        wrong_email.visibility=View.VISIBLE
                    }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                if(email_login.text.toString().trim().matches(emailPattern))
                {
                    wrong_email.visibility=View.INVISIBLE

                }else
                {
                    wrong_email.visibility=View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(email_login.text.toString().trim().matches(emailPattern))
                {
                    wrong_email.visibility=View.INVISIBLE

                }else
                {
                    wrong_email.visibility=View.VISIBLE
                }
            }
        })




    }
    private fun loginUser() {
        val email=email_login.text.toString()
        val password=password_login.text.toString()
        when{
            TextUtils.isEmpty(email)-> Toast.makeText(this,"email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password)-> Toast.makeText(this,"password name is required", Toast.LENGTH_LONG).show()

            else ->{
                val  progressDialog= ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("SignIn")
                progressDialog.setMessage("Loading please Wait!!!")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()


                val mAuth:FirebaseAuth= FirebaseAuth.getInstance()
                val user=FirebaseAuth.getInstance().currentUser
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {  task ->

                                if (task.isSuccessful) {
                                    textViewResponse.text = "signUp successful.Email"


                                    progressDialog.dismiss()
                                    val intent =
                                        Intent(this@SignInActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()


                                } else {
                                    val message = task.exception!!.toString()
                                    Toast.makeText(this, "Wrong password or Username", Toast.LENGTH_LONG).show()
                                    FirebaseAuth.getInstance().signOut()
                                    progressDialog.dismiss()

                                }

                            }
                    }
                }
            }






    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        val handler = Handler()
        if (user != null) {
            if (user.isEmailVerified) {
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                finish()
                handler.postDelayed({ FirebaseAuth.getInstance().signOut() }, 25 * 60000)
                //25 minutes then signOut current user
                //if you want to delete user call user.delete()


            }


        }
    }
}


