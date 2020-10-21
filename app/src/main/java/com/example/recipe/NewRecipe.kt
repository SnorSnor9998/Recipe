package com.example.recipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_recipe.*
import java.util.*

class NewRecipe : AppCompatActivity() {

    var recipe = Recipe()
    var selectedPhotoUri: Uri? = null
    var uuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)
        spinnerinit()
        nr_back_button.setOnClickListener {
            val fc = Function()
            fc.delete(uuid)
            finish() }

        nr_btn_pickimage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        nr_btn_next.setOnClickListener {
            //no validation is done here so..... :E
            if(selectedPhotoUri != null){
                savePhoto()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            nr_btn_pickimage.setImageBitmap(bitmap)
        }

    }

    private fun savePhoto(){
        val recipeImgID = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/RecipeImages/$recipeImgID")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                insert(it.toString())
            }

        }
    }


    private fun insert(url : String){
        recipe.recipeID = uuid
        recipe.recipeImage = url
        recipe.recipeTitle = nr_txt_title.text.toString()
        recipe.calories = nr_txt_calories.text.toString().toDouble()
        recipe.totalFat = nr_txt_totalfat.text.toString().toDouble()
        recipe.saturatedFat = nr_txt_saturatedfat.text.toString().toDouble()
        recipe.fibre = nr_txt_fibre.text.toString().toDouble()
        recipe.protein = nr_txt_protein.text.toString().toDouble()
        recipe.cholesterol = nr_txt_calories.text.toString().toDouble()
        recipe.category = nr_spinner.selectedItem.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/Recipe/$uuid")
        ref.setValue(recipe)

        //jump
        val i = Intent(this, AddIng::class.java)
        i.putExtra(RECIPE_KEY,uuid)
        finish()
        startActivity(i)

    }




    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    private fun spinnerinit(){
        val cat = resources.getStringArray(R.array.pick_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cat)
        nr_spinner.adapter = adapter
    }





}