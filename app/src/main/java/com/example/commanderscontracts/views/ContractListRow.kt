package com.example.commanderscontracts.views

import com.example.commanderscontracts.R
import com.example.commanderscontracts.models.UserContract
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.contract_row.view.*

class ContractListRow(val userContract: UserContract): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.client_name_contract.text = userContract.clientName

    }
    override fun getLayout(): Int {
        return R.layout.contract_row
    }
}