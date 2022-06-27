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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {
    var email_signup:EditText? = null
    var fullname:EditText? = null
    var username:EditText? = null
    var password_signup:EditText? = null
    var signup_btn:Button? = null
    var signin_link_btn:Button? = null
    var fAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        email_signup = findViewById(R.id.email_signup)
        fullname = findViewById(R.id.fullname_signup)
        username = findViewById(R.id.username_signup)
        password_signup = findViewById(R.id.password_signup)
        signup_btn = findViewById(R.id.signup_btn)
        signin_link_btn = findViewById(R.id.signin_link_btn)
        fAuth = FirebaseAuth.getInstance()



        //link to signin button
        signin_link_btn!!.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))

        }

            signup_btn!!.setOnClickListener {
                val email_signup = email_signup!!.text.toString().trim()
                val fullname = fullname!!.text.toString().trim()
                val username = username!!.text.toString().trim()
                val password_signup = password_signup!!.text.toString().trim()


                //check empty fields

                if (email_signup.isEmpty() || fullname.isEmpty() || username.isEmpty()|| password_signup.isEmpty()){
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
                }

                if (password_signup.length < 8){
                    Toast.makeText(this,"Password must contain at least 8 characters", Toast.LENGTH_LONG).show()
                }

                var progress: ProgressDialog = ProgressDialog(this@SignupActivity)
                progress.setTitle("Loading")
                progress.setMessage("Please wait...")
                progress.setCanceledOnTouchOutside(false)
                progress.show()

                fAuth!!.createUserWithEmailAndPassword(email_signup, password_signup).addOnCompleteListener { task->
                    if (task.isSuccessful)
                    {
                        saveUserInfo(email_signup, fullname, username, password_signup)


                    }else{
                        //signup not successful
                        Toast.makeText(this, task.exception!!.toString(), Toast.LENGTH_LONG).show()
                        fAuth!!.signOut()
                        progress.dismiss()
                    }
                }

            }




    }

    private fun saveUserInfo(emailSignup: String, fullname: String, username: String, passwordSignup: String)
    {
        //save user Id
        var currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        //creating reference for user information
        var usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        var userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["email_signup"] = emailSignup
        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["password_signup"] = currentUserID
        userMap["bio"] = "Hey, Welcome to my InstacloneApp."
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/renoapp-62f59.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=f5c85275-3c1a-464b-a9c0-263636eb2b9d"

        //pass the data

        usersRef.child(currentUserID).setValue(userMap).addOnCompleteListener { task->
                if (task.isSuccessful)
                {
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_LONG).show()
                    //send user to main activity/landingpage
                    var intent = Intent(this@SignupActivity, MainActivity::class.java)

                    //user cant access login or sign up unless they signout.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                }else
                {
                    Toast.makeText(this, task.exception!!.toString(), Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                }
        }
    }


}