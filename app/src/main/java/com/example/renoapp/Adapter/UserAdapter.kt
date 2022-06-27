package com.example.renoapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.renoapp.Model.User
import com.example.renoapp.R
import com.example.renoapp.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (private var mContext: Context,
                   private var mUser: List<User>,
                   private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int)
    {
        //code to display the retrieved data for a specific user
        val user = mUser[position]
        holder.user_name_search.text = user.getUsername()
        holder.user_full_name_search.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.username_profile_image_search)

        //check following status (comes after following or unfollow)
        checkFollowingStatus(user.getUid(), holder.follow_btn_search)

        //redirect to user profile page
        holder.itemView.setOnClickListener { View.OnClickListener{
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUid())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        } }


        //follow button
        holder.follow_btn_search.setOnClickListener {
            if (holder.follow_btn_search.text.toString() == "Follow") {
                firebaseUser?.uid.let { itl ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow")
                        .child(itl.toString()) //get my user id
                        .child("Following")
                        .child(user.getUid()) //user id of person im following
                        .setValue(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //add to followers list
                                firebaseUser?.uid.let { itl ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow")
                                        .child(user.getUid())
                                        .child("Followers")
                                        .child(itl.toString())
                                        .setValue(true)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else
            {
                //unfollow
                firebaseUser?.uid.let { itl ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow")
                        .child(itl.toString())
                        .child("Following")
                        .child(user.getUid())
                        .removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //remove from followers list
                                firebaseUser?.uid.let { itl ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow")
                                        .child(user.getUid())
                                        .child("Followers")
                                        .child(itl.toString())
                                        .removeValue()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {

                                            }
                                        }
                                }
                            }
                        }
                }
            }

        }
    }


    override fun getItemCount(): Int
    {
        //display total number of user retrieved from the firebase
        return mUser.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var user_name_search:TextView = itemView.findViewById(R.id.user_name_search)
        var user_full_name_search:TextView = itemView.findViewById(R.id.user_full_name_search)
        var username_profile_image_search:CircleImageView = itemView.findViewById(R.id.username_profile_image_search)
        var follow_btn_search:Button = itemView.findViewById(R.id.follow_btn_search)
    }

    private fun checkFollowingStatus(uid: String, followBtnSearch: Button)
    {
        val followingRef = firebaseUser?.uid.let { itl ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(itl.toString())
                .child("Following")
        }

            //check following status and toggle the button

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.child(uid).exists())
                {
                    followBtnSearch.text = "Following"
                }
                else
                {
                    followBtnSearch.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}