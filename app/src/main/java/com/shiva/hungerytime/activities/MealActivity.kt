package com.shiva.hungerytime.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.shiva.hungerytime.R
import com.shiva.hungerytime.database.MealDatabase
import com.shiva.hungerytime.databinding.ActivityMealBinding
import com.shiva.hungerytime.fragment.HomeFragment
import com.shiva.hungerytime.pojo.Meal
import com.shiva.hungerytime.viewModel.MealViewModel
import com.shiva.hungerytime.viewModel.MealViewModelFactory

class MealActivity : AppCompatActivity() {
    private lateinit var mealId:String
    private lateinit var mealName: String
    private lateinit var mealThumb:String
    private lateinit var youtubeLink:String
    private lateinit var binding: ActivityMealBinding
    private lateinit var mealMvvm: MealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val mealDatabase = MealDatabase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        mealMvvm = ViewModelProvider(this , viewModelFactory)[MealViewModel::class.java]
//        mealMvvm = ViewModelProvider(this)[MealViewModel::class.java]



        getMealInformationFromIntent()

        setInformationInViews()
        loadingCase()
        mealMvvm.getMEalDeatil(mealId)
        observeMealDetailsLivedata()
        onYoutubeImgClick()


        onFavoriteClick()


    }

    private fun onFavoriteClick() {
        binding.btnSave.setOnClickListener {
            mealToSave?.let {
                mealMvvm.insertMeal(it)
                Toast.makeText(this , "Meal id added to Favorite" , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onYoutubeImgClick() {
             binding.imgYoutube.setOnClickListener {
                 val intent =  Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
                 startActivity(intent)
             }
    }
    private var mealToSave :Meal?=null

    private fun observeMealDetailsLivedata() {
        mealMvvm.observeMealDetailLivedata().observe(this , object :Observer<Meal>{
            override fun onChanged(t: Meal) {
                onResponseCase()

                mealToSave = t
                binding.tvCategoryInfo.text = "Category : ${t!!.strCategory}"
                binding.tvAreaInfo.text = "Area : ${t!!.strArea}"
                binding.tvInstructions.text = t.strInstructions
                youtubeLink = t.strYoutube
            }
        })
    }

    private fun setInformationInViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imgMealDetail)
        binding.collapsingToolbar.title = mealName
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
    }

    private fun getMealInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!

    }
    private fun loadingCase(){
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.visibility = View.INVISIBLE
        binding.tvInstructions.visibility = View.INVISIBLE
        binding.tvCategoryInfo.visibility = View.INVISIBLE
        binding.tvAreaInfo.visibility = View.INVISIBLE
        binding.imgYoutube.visibility = View.INVISIBLE
    }

    private fun onResponseCase(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnSave.visibility = View.VISIBLE
        binding.tvInstructions.visibility = View.VISIBLE
        binding.tvCategoryInfo.visibility = View.VISIBLE
        binding.tvAreaInfo.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE

    }
}