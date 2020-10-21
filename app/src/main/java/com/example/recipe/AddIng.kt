package com.example.recipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_add_ing.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.recycle_row_ingredients.view.*
import java.util.*

class AddIng : AppCompatActivity() {

    var rc_id : String = ""
    var list = arrayListOf<Ingredients>()


    override fun onStart() {
        super.onStart()
        rc_id = intent.getStringExtra(NewRecipe.RECIPE_KEY).toString()
        initSpinner()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ing)

        nr_ing_back_button.setOnClickListener {
            val fc = Function()
            fc.delete(rc_id)
            finish()
        }
        nr_ing_btn_add.setOnClickListener { add() }
        nr_ing_btn_next.setOnClickListener { insert() }


    }

    private fun add(){

        if(nr_ing_txt_ingname.text.isEmpty())
            return

        if (nr_ing_txt_qty.text.isEmpty())
            return

        val ing = Ingredients()
        ing.ingredientID = UUID.randomUUID().toString()
        ing.ingredientName = nr_ing_txt_ingname.text.toString()
        ing.quantity = nr_ing_txt_qty.text.toString().toDouble()
        ing.recipeID = rc_id
        ing.unit = nr_ing_spin_unit.selectedItem.toString()

        list.add(ing)
        display()
        cleartxt()

    }

    private fun display(){
        val adapter = GroupAdapter<GroupieViewHolder>()
        if(list.count() > 0){
            list.forEach {
                adapter.add(bindataIng(it))
            }
            nr_ing_ryr_ing.adapter = adapter

        }
    }


    private class bindataIng(val ingredients: Ingredients): Item<GroupieViewHolder>() {
        override fun bind(viewHolder1: GroupieViewHolder, position: Int) {
            viewHolder1.itemView.ing_qty.text = ingredients.quantity.toString()
            viewHolder1.itemView.ing_name.text = ingredients.ingredientName
            viewHolder1.itemView.ing_desc.text = ingredients.unit
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_ingredients
        }
    }


    private fun initSpinner(){
        val unit = resources.getStringArray(R.array.ing_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unit)
        nr_ing_spin_unit.adapter = adapter

    }

    private fun cleartxt(){
        nr_ing_txt_ingname.setText("")
        nr_ing_txt_qty.setText("")
    }

    private fun insert(){

        list.forEach {
            val ref = FirebaseDatabase.getInstance().getReference("/Ingredient/${it.ingredientID}")
            ref.setValue(it)
        }

        //jump
        val i = Intent(this, AddStep::class.java)
        i.putExtra(RECIPE_KEY, rc_id)
        finish()
        startActivity(i)

    }

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }


}