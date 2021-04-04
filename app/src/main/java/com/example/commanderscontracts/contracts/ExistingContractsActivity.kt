package com.example.commanderscontracts.contracts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class ExistingContractsActivity : AppCompatActivity() {

    var userContract: UserContract? = null
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
        val reference = FirebaseDatabase.getInstance().getReference("/user-signatures/$currentUserId").orderByChild("clientName")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var adapter = GroupAdapter<GroupieViewHolder>()

                //====will get called every time we call a user
                snapshot.children.forEach {
                    Log.d("ExistingContract", it.toString())

                    //====convert the value to UserContract object====

                    userContract = it.getValue(UserContract::class.java)

//





                    if (userContract != null) {

                        adapter.add(ContractListRow(userContract!!))

                    }


//

//                    adapter.setOnItemClickListener { item, view ->
//
//                        //=====casting as userItem to access the name
//                        val userItemContract = item as ContractListRow
//
//                        showPopupMenu(view)
//
//
//
//                    }


                    recycler_view_existing_contracts.adapter = adapter

                    recycler_view_existing_contracts.addItemDecoration(DividerItemDecoration(this@ExistingContractsActivity, DividerItemDecoration.VERTICAL))


                }



            }

            override fun onCancelled(error: DatabaseError) {

            }
        })






    }

    //=====

//    private fun showPopupMenu(view:View){
//        val pop_up_menu:PopupMenu = PopupMenu(view.context, view)
//
//        pop_up_menu.inflate(R.menu.pop_up_menu)
//
//        pop_up_menu.setOnMenuItemClickListener {
//            item ->
//
//            when(item.itemId) {
//                R.id.action_pop_up_view_pdf -> {
//
//                    Log.d("Menu","ViewPDF tapped: ${userContract!!.clientName}")
//
//                }
//
//                R.id.action_pop_up_share -> {
//
//                }
//            }
//
//            true
//
//        }
//        pop_up_menu.show()
//
//
//
//
//
//
//
//    }
}