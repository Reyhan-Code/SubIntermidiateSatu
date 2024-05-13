package com.dicoding.subintermidiatesatu.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.subintermidiatesatu.databinding.ItemListBinding
import com.dicoding.subintermidiatesatu.response.ListStoryItem
import com.dicoding.subintermidiatesatu.view.detail.DetailActivity

class UserAdapter : ListAdapter<ListStoryItem, UserAdapter.MyViewHolder>(DIFF_CALLBACK) {

    inner class MyViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: ListStoryItem?) {
            binding.namaUser.text = itemName?.name
            Glide.with(binding.root)
                .load(itemName?.photoUrl)
                .into(binding.userPhoto)
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.ID, itemName?.id)
                intentDetail.putExtra(DetailActivity.NAME_USER, itemName?.name)
                intentDetail.putExtra(DetailActivity.DESCRIPTION, itemName?.description)
                intentDetail.putExtra(DetailActivity.PICTURE, itemName?.photoUrl)
                binding.root.context.startActivity(intentDetail)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean =
                oldItem == newItem
        }
    }
}