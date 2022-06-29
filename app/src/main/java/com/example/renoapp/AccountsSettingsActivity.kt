package com.example.renoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.renoapp.Model.User
import com.example.renoapp.databinding.ActivityAccountsSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class AccountsSettingsActivity : AppCompatActivity() {

    //declare the binding variable

    private lateinit var binding: ActivityAccountsSettingsBinding

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
//    var logout_btn:Button? = null
//    var fAuth:FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

//        setContentView(R.layout.activity_accounts_settings)
//        logout_btn = findViewById(R.id.logout_btn)
//        fAuth = FirebaseAuth.getInstance()

        binding.logoutBtn!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            var intent = Intent(this@AccountsSettingsActivity, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }

        binding.saveInfoProfileBtn.setOnClickListener {
            if (checker == "clicked")
            {

            }
            else
            {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun updateUserInfoOnly() {
        when {
            binding.fullNameProfileFrag.getText().toString() == "" -> {
                Toast.makeText(this, "Fullname required", Toast.LENGTH_LONG).show()
            }
            binding.usernameProfileFrag.getText().toString() == "" -> {
                Toast.makeText(this, "Username required", Toast.LENGTH_LONG).show()
            }
            binding.bioProfileFrag.getText().toString() == "" -> {
                Toast.makeText(this, "Bio required", Toast.LENGTH_LONG).show()
            }
            else -> {

                val userRef = FirebaseDatabase.getInstance().reference.child("Users")

                var userMap = HashMap<String, Any>()
                userMap["fullname"] = binding.fullNameProfileFrag.getText().toString()
                userMap["username"] = binding.usernameProfileFrag.getText().toString()
                userMap["bio"] = binding.bioProfileFrag.getText().toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account Updated Successfully", Toast.LENGTH_LONG).show()

                var intent = Intent(this@AccountsSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun userInfo()
    {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(binding.profileImageViewFrag)
                    binding.usernameProfileFrag.setText(user!!.getUsername())
                    binding.fullNameProfileFrag.setText(user!!.getFullname())
                    binding.bioProfileFrag.setText(user!!.getBio())

                }
            }

            override fun onCancelled(error: DatabaseError)
            {
            }
        })
    }
}