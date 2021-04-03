package com.example.commanderscontracts.contracts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.commanderscontracts.R
import com.example.commanderscontracts.models.UserContract
import com.example.commanderscontracts.views.ContractListRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_existing_contracts.*

class ExistingContractsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_existing_contracts)

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)

        //====Set title
        supportActionBar?.title = "Existing  Contracts"



        fetchUserContracts()
    }


    fun finishMyActivity(){
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    override fun onBackPressed() {

        finishMyActivity()
    }


    private fun fetchUserContracts() {

        val currentUserId  = FirebaseAuth.getInstance().uid ?: return
        val reference = FirebaseDatabase.getInstance().getReference("/user-signatures/$currentUserId")

        reference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val adapter =  GroupAdapter<GroupieViewHolder>()

                //====will get called every time we call a user
                snapshot.children.forEach{
                    Log.d("ExistingContract", it.toString() )

                    //====convert the value to UserContract object====

                    val userContract = it.getValue(UserContract::class.java)


                    if(userContract != null) {

                        adapter.add(ContractListRow(userContract))

                    }


                    recycler_view_existing_contracts.adapter = adapter

                    recycler_view_existing_contracts.addItemDecoration(DividerItemDecoration(this@ExistingContractsActivity, DividerItemDecoration.VERTICAL))



                }



            }

            override fun onCancelled(error: DatabaseError) {

            }
        })






    }
}