package com.example.recipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*


class MainActivity : AppCompatActivity() {

    val list = arrayListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        spinnerinit()

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, v: View?, i: Int, lng: Long) {
                filter()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        main_btn_newrec.setOnClickListener {
            val i = Intent(this,NewRecipe::class.java)
            startActivity(i)
        }

        main_btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, Login::class.java)
            finish()
            startActivity(i)
        }

    }


    private fun init(){
        val adapter = GroupAdapter<GroupieViewHolder>()

            val ref = FirebaseDatabase.getInstance().getReference("/Recipe")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rep = it.getValue(Recipe::class.java)
                    if (rep != null) {
                        adapter.add(bindata(rep))
                        list.add(rep)
                    }
                }

                ryr_view.adapter = adapter
            }
        })
    }

    private class bindata(val recipe: Recipe): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.ryr_title.text = recipe.recipeTitle
            Picasso.get().load(recipe.recipeImage).into(viewHolder.itemView.ryr_image)

            viewHolder.itemView.ryr_root.setOnClickListener {
                val intent = Intent(it.context, Detail::class.java)
                intent.putExtra(RECIPE_KEY, recipe.recipeID)
                (it.context as Activity).startActivity(intent)

            }

        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_item
        }
    }

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    private fun spinnerinit(){
        val cat = resources.getStringArray(R.array.text_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cat)
        spinner.adapter = adapter
    }

    private fun filter(){
        val search = spinner.selectedItem.toString()
        val adapter = GroupAdapter<GroupieViewHolder>()

        list.forEach {
            if(!(search.equals("All"))){
                if(it.category.toUpperCase().contains(search.toUpperCase())){
                    adapter.add(bindata(it))
                }
            }else{
                adapter.add(bindata(it))
            }

        }

        ryr_view.adapter = adapter

    }


}