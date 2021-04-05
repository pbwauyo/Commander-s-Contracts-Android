package com.example.commanderscontracts.views

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.example.commanderscontracts.CreatePDFActivity
import com.example.commanderscontracts.R
import com.example.commanderscontracts.models.UserContract
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.contract_row.view.*
import java.security.AccessController.getContext



class ContractListRow(var userContract: UserContract): Item<GroupieViewHolder>() {

    companion object {
        val USER_CONTRACT_KEY = "USER_CONTRACT_KEY"
    }



    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.client_name_contract.text = userContract.clientName


        viewHolder.itemView.image_btn.setOnClickListener { v ->
            //Creating the instance of PopupMenu
            val popup = PopupMenu(v.context, v)
            //Inflating the Popup using xml file
            popup.menuInflater.inflate(R.menu.pop_up_menu, popup.menu)

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener { item ->

                when(item.itemId){
                    R.id.action_pop_up_view_pdf -> {

                        val intent = Intent(v.context, CreatePDFActivity::class.java)
                        intent.putExtra(USER_CONTRACT_KEY, userContract)

                        Log.d("Menu","User ${userContract.clientName}")

                        Log.d("Menu","User ${userContract.companyName}")
                        Log.d("Menu","User ${userContract.companyAddress}")
                        Log.d("Menu","User ${userContract.companyEmail}")


                        v.context.startActivity(intent)

                    }


                    R.id.action_pop_up_share -> {

                        Log.d("Menu","Share tapped")

                    }
                }
//                    Toast.makeText(item.view.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                true
            }
            popup.show() //showing popup menu
        } //closing the setOnClickListener method


    }
    override fun getLayout(): Int {
        return R.layout.contract_row
    }

//    private fun showpopupMenu(view: View) {
//
//
//
//
//        val pop_up_menu: PopupMenu = PopupMenu(this, view)
//
//        pop_up_menu.inflate(R.menu.pop_up_menu)
//        pop_up_menu.show()
//
//    }
}