package com.mirellageri.freegames_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirellageri.freegames_android.data.Screenshot
import com.squareup.picasso.Picasso
import com.mirellageri.freegames_android.R
import com.mirellageri.freegames_android.databinding.ItemGalleryBinding

class GalleryAdapter(
    var items: List<Screenshot>,
    var selectedPosition: Int,
    val onClickListener: (Int)-> Unit
): RecyclerView.Adapter<GalleryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGalleryBinding.inflate(layoutInflater, parent, false)
        return GalleryViewHolder(binding)
    }
    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val screenshot = items[position]
        holder.render(screenshot)
        holder.setSelected(position == selectedPosition)
        holder.itemView.setOnClickListener {
            onClickListener(position)
        }
    }
    override fun getItemCount(): Int = items.size

    fun changeSelectedPosition(position:Int){
        val oldSelected = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldSelected)
        notifyItemChanged(selectedPosition)
    }
}

class GalleryViewHolder(val binding: ItemGalleryBinding): RecyclerView.ViewHolder(binding.root){
    fun render(screenshot: Screenshot) {
        Picasso.get().load(screenshot.image).placeholder(R.drawable.ic_launcher_background)
            .into(binding.screenshotImageView)
    }
    fun setSelected(selected: Boolean){
        if(selected) itemView.setBackgroundResource(R.color.md_theme_primary)
        else itemView.setBackgroundResource(0)
    }
}