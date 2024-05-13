package com.dicoding.subintermidiatesatu.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.subintermidiatesatu.data.Result
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.subintermidiatesatu.R
import com.dicoding.subintermidiatesatu.adapter.UserAdapter
import com.dicoding.subintermidiatesatu.databinding.ActivityMainBinding
import com.dicoding.subintermidiatesatu.response.ListStoryItem
import com.dicoding.subintermidiatesatu.view.ViewModelFactory
import com.dicoding.subintermidiatesatu.view.upload.UploadActivity
import com.dicoding.subintermidiatesatu.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.topBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.change_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.btn_logout -> {
                    viewModel.logout()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }

        setupView()
        setupAction()
        itemDecoration()
        observeStories()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }

    private fun setupAction() {
        binding.btnUpload.setOnClickListener {
            val intent = (Intent(this, UploadActivity::class.java))
            startActivity(intent)
        }
    }

    private fun itemDecoration() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvMain.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvMain.addItemDecoration(itemDecoration)
    }

    private fun handleStories(stories: Result<List<ListStoryItem>>) {
        when (stories) {
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                val adapter = UserAdapter()
                adapter.submitList(stories.data)
                binding.rvMain.adapter = adapter
            }

            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, stories.error, Toast.LENGTH_SHORT).show()
            }

            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun observeStories() {
        viewModel.getStories().observe(this@MainActivity) { stories ->
            handleStories(stories)
        }
    }

}