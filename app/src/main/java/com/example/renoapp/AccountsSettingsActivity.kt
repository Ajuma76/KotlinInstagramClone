package com.example.renoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.renoapp.databinding.ActivityAccountsSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class AccountsSettingsActivity : AppCompatActivity() {

    //declare the binding variable

    private lateinit var binding: ActivityAccountsSettingsBinding

//    var logout_btn:Button? = null
//    var fAuth:FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
}