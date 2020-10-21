package com.example.recipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_add_ing.*
import kotlinx.android.synthetic.main.activity_add_step.*
import kotlinx.android.synthetic.main.recycle_row_preparation.view.*
import java.util.*

class AddStep : AppCompatActivity() {

    var rc_id : String = ""
    var list = arrayListOf<Steps>()
    var counter = 1


    override fun onStart() {
        super.onStart()
        rc_id = intent.getStringExtra(AddIng.RECIPE_KEY).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_step)

        nr_step_back_button.setOnClickListener {
            val fc = Function()
            fc.delete(rc_id)
            finish()
        }
        nr_step_btn_add.setOnClickListener { add() }

        nr_step_btn_finish.setOnClickListener { insert() }

    }

    private fun add(){
        val step = Steps()

        step.stepID = UUID.randomUUID().toString()
        step.stepNo = counter
        step.desc = nr_step_txt_desc.text.toString()
        step.recipeID = rc_id

        list.add(step)
        display()
        clear()

    }

    private fun display(){
        val adapter = GroupAdapter<GroupieViewHolder>()
        if(list.count() > 0){
            list.forEach {
                adapter.add(bindataStep(it))
            }
            nr_step_ryr_step.adapter = adapter

        }
    }

    private class bindataStep(val steps: Steps): Item<GroupieViewHolder>() {
        override fun bind(viewHolder2: GroupieViewHolder, position: Int) {
            viewHolder2.itemView.counterText.text = steps.stepNo.toString()
            viewHolder2.itemView.prepareText.text = steps.desc
        }
        override fun getLayout(): Int {
            return R.layout.recycle_row_preparation
        }
    }

    private fun clear(){
        nr_step_txt_desc.setText("")
        nr_step_txt_counter.setText(counter.toString())
        counter += 1
    }

    private fun insert(){

        list.forEach {
            val ref = FirebaseDatabase.getInstance().getReference("/Steps/${it.stepID}")
            ref.setValue(it)
        }


    }


}