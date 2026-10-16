package com.mirellageri.freegames_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirellageri.freegames_android.data.Screenshot
import com.mirellageri.freegames_android.databinding.ItemScreenshotBinding
import com.squareup.picasso.Picasso
import com.mirellageri.freegames_android.R

class ScreenshotAdapter(var items: List<Screenshot>): RecyclerView.Adapter<ScreenshotViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemScreenshotBinding.inflate(layoutInflater, parent, false)
        return ScreenshotViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        val screenshot = items[position]
        holder.render(screenshot)
    }
    override fun getItemCount(): Int = items.size
}

class ScreenshotViewHolder(val binding: ItemScreenshotBinding): RecyclerView.ViewHolder(binding.root){
    fun render(screenshot: Screenshot) {
        Picasso.get().load(screenshot.image).placeholder(R.drawable.ic_launcher_background).into(binding.screenshotImageView)
    }
}