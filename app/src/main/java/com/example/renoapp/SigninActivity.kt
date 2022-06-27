package com.example.renoapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.renoapp.R
import com.google.firebase.auth.FirebaseAuth



class SigninActivity : AppCompatActivity() {

//    //declare
//    private lateinit var binding: SignupActivity

    var signup_link_btn:Button? = null
    var email_login:EditText? = null
    var password_login:EditText? = null
    var login_btn:Button? = null
    var fAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        signup_link_btn = findViewById(R.id.signup_link_btn)
        email_login = findViewById(R.id.email_login)
        password_login = findViewById(R.id.password_login)
        login_btn = findViewById(R.id.login_btn)
        fAuth = FirebaseAuth.getInstance()


        //link to signup activity
        signup_link_btn!!.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        login_btn!!.setOnClickListener {
            var email_login = email_login!!.text.toString().trim()
            var password_login = password_login!!.text.toString().trim()

            if (email_login.isEmpty() || password_login.isEmpty()){
                Toast.makeText(this, "Email and Password is required", Toast.LENGTH_LONG).show()
            }

            var progress: ProgressDialog = ProgressDialog(this@SigninActivity)
            progress.setTitle("Loading")
            progress.setMessage("Please wait...")
            progress.setCanceledOnTouchOutside(false)
            progress.show()

            fAuth!!.signInWithEmailAndPassword(email_login, password_login).addOnCompleteListener { task->
                if (task.isSuccessful){
                    progress.dismiss()

                    var intent = Intent(this@SigninActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else

                {
                    Toast.makeText(this, task.exception!!.toString(), Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progress.dismiss()
                }

            }
        }



    }


    override fun onStart() {
        super.onStart()

        //user already logged in
        if (FirebaseAuth.getInstance().currentUser != null){
            var intent = Intent(this@SigninActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}