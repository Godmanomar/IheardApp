package com.novusvista.omar.iheardapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.emoji.widget.EmojiButton
import androidx.emoji.widget.EmojiEditText
import androidx.emoji.widget.EmojiTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {

    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)
        setContentView(R.layout.activity_sign_up)



        signin_link_btn.setOnClickListener {

            startActivity(Intent(this,SignInActivity::class.java))


        //   val navView: BottomNavigationView = findViewById(R.id.nav_view)

        }
        signup_button.setOnClickListener{
            createAccount()
        }
        email_signup.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if(email_signup.text.toString().trim().matches(emailPattern))
                {
                    wrong2_email.visibility= View.INVISIBLE

                }else
                {
                    wrong2_email.visibility= View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                if(email_signup.text.toString().trim().matches(emailPattern))
                {
                    wrong2_email.visibility= View.INVISIBLE

                }else
                {
                    wrong2_email.visibility= View.VISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(email_signup.text.toString().trim().matches(emailPattern))
                {
                    wrong2_email.visibility= View.INVISIBLE

                }else
                {
                    wrong2_email.visibility= View.VISIBLE
                }
            }
        })




    }
     private fun createAccount()
     {

         val fullName=fullname_signup.text.toString()
         val userName=username_signup.text.toString()
         val email=email_signup.text.toString()
         val password=password_signup.text.toString()

         when{
             TextUtils.isEmpty(userName)->Toast.makeText(this,"full name is required",Toast.LENGTH_LONG).show()
             TextUtils.isEmpty(fullName)->Toast.makeText(this,"user name is required",Toast.LENGTH_LONG).show()
             TextUtils.isEmpty(email)->Toast.makeText(this,"email is required",Toast.LENGTH_LONG).show()
             TextUtils.isEmpty(password)->Toast.makeText(this,"password name is required",Toast.LENGTH_LONG).show()

             else ->{
                 val  progressDialog=ProgressDialog(this@SignUpActivity)
                 progressDialog.setTitle("SignUp")
                 progressDialog.setMessage("Loading please Wait!!!")
                 progressDialog.setCanceledOnTouchOutside(false)
                 progressDialog.show()


                 val mAuth:FirebaseAuth= FirebaseAuth.getInstance()
                 mAuth.createUserWithEmailAndPassword(email,password)
                     .addOnCompleteListener{
                         task->
                         if(task.isSuccessful){

                             saveUserInfo(fullName,userName,email,progressDialog)

                         }
                         else{
                             val message= task.exception!!.toString()
                   Toast.makeText(this,"Error:$message",Toast.LENGTH_LONG).show()
                             mAuth.signOut()
                             progressDialog.dismiss()


                         }
                     }
             }

         }

    }

    private fun saveUserInfo(fullName: String, userName: String, email: String,progressDialog:ProgressDialog) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "welcome to the Iheard App were you can get your trending social news"
        userMap["image"] = "gs://iheardapp-mobile.appspot.com/Default Images/profile.png"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

// Below three lines added for sending the authentication link to the user's email
                    val user=FirebaseAuth.getInstance().currentUser
                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Account has been created successfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(currentUserID)
                                    .child("Following").child(currentUserID)
                                    .setValue(true)


                                updateUI(user, email_signup.text.toString() )

//                                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                                startActivity(intent)
//                                finish()

                            } else {
                                val message = task.exception!!.toString()
                                Toast.makeText(this, "Error enter valid email: $message", Toast.LENGTH_LONG).show()
                                FirebaseAuth.getInstance().signOut()
                                progressDialog.dismiss()

                            }
                        }


                }


            }
    }
    private fun updateUI(currentUser: FirebaseUser?, emailAdd: String) {
        if(currentUser !=null){

// Below  if statement is added to check if email is verified
            if(currentUser.isEmailVerified){
                val intent = Intent(this, VerifyEmailActivity::class.java)
                intent.putExtra("emailAddress", emailAdd);
                startActivity(intent)

// add finish() function to terminate the Sign In activity
                finish()

//adding else with toast to display the message if email is not verified
            }else
                Toast.makeText(this,"Email Address Is not Verified. Please verify your email address",Toast.LENGTH_LONG).show()
            val intent = Intent(this, VerifyEmailActivity::class.java)
            intent.putExtra("emailAddress", emailAdd);
            startActivity(intent)

        }
    }
    }