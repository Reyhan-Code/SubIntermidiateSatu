package com.dicoding.subintermidiatesatu.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.subintermidiatesatu.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra(ID) ?: ""
        name = intent.getStringExtra(NAME_USER) ?: ""
        description = intent.getStringExtra(DESCRIPTION) ?: ""
        picture = intent.getStringExtra(PICTURE) ?: ""

        binding.userName.text = name
        binding.description.text = description

        Glide.with(this).load(picture).into(binding.storyPhoto)

        supportActionBar?.hide()
    }

    companion object {
        const val ID = "ID"
        const val NAME_USER = "NAME_USER"
        const val DESCRIPTION = "DESCRIPTION"
        const val PICTURE = "PICTURE"

        var id: String = ""
        var name: String = ""
        var description: String? = null
        var picture: String? = null
    }
}