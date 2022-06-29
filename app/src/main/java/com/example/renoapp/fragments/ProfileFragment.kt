package com.example.renoapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renoapp.AccountsSettingsActivity
import com.example.renoapp.Model.User
import com.example.renoapp.R
import com.example.renoapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser:FirebaseUser

    private lateinit var _binding : FragmentProfileBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            //if user tries to redirect to his own profile
            this.profileId = pref.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid)
        {
            binding.editAccountSettingsBtn.text = "Edit Profile"
        }
        else if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }

        binding.editAccountSettingsBtn.setOnClickListener {
             val getButtonText = binding.editAccountSettingsBtn.text.toString()

            when
            {
                getButtonText == "Edit Profile" -> startActivity(Intent(context, AccountsSettingsActivity::class.java))

                //follow
                getButtonText == "Follow" -> {
                    firebaseUser.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow")
                            .child(itl.toString())
                            .child("Following")
                            .child(profileId)
                            .setValue(true)
                    }
                    //add to followers list
                    firebaseUser.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow")
                            .child(profileId)
                            .child("Following")
                            .child(itl.toString())
                            .setValue(true)
                    }
                }

                //unfollow
                getButtonText == "Following" -> {
                    firebaseUser.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow")
                            .child(itl.toString())
                            .child("Following")
                            .child(profileId)
                            .removeValue()
                    }
                    //remove from follow list
                    firebaseUser.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow")
                            .child(profileId)
                            .child("Following")
                            .child(itl.toString())
                            .removeValue()
                    }
                }
            }


        }

        getFollower()
        getFollowings()
        userInfo()


        return binding.root

    }

    private fun checkFollowAndFollowingButtonStatus()
    {
        val followingRef = firebaseUser.uid.let { itl ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(itl.toString())
                .child("Following")
        }

        if (followingRef != null)
        {
            followingRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot)
                {
                    if (snapshot.child(profileId).exists())
                    {
                        binding.editAccountSettingsBtn.text = "Following"
                    }
                    else
                    {
                        binding.editAccountSettingsBtn.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun getFollower()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(profileId)
                .child("Followers")


        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    binding.totalFollowers.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(profileId)
                .child("Following")


        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    binding.totalFollowing.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo()
    {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(binding.proImageProfileFrag)
                    binding.profileFragmentUsername.text = user!!.getUsername()
                    binding.fullNameProfileFrag.text = user!!.getFullname()
                    binding.bioProfileFrag.text = user!!.getBio()

                }
            }

            override fun onCancelled(error: DatabaseError)
            {
            }
        })
    }

    override fun onStop() {
        super.onStop()
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref?.putString("profileId", firebaseUser.uid)
            pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }


    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()


    }
}

