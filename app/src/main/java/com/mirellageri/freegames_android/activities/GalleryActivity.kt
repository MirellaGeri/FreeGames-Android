package com.mirellageri.freegames_android.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.mirellageri.freegames_android.R
import com.mirellageri.freegames_android.adapters.GalleryAdapter
import com.mirellageri.freegames_android.adapters.GalleryPagerAdapter
import com.mirellageri.freegames_android.data.Screenshot
import com.mirellageri.freegames_android.databinding.ActivityGalleryBinding

class GalleryActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SCREENSHOT_INDEX = "SCREENSHOT_INDEX"
        const val EXTRA_SCREENSHOTS_ARRAY = "SCREENSHOTS_ARRAY"
    }
    lateinit var binding : ActivityGalleryBinding
    lateinit var adapter : GalleryAdapter
    lateinit var pagerAdapter : GalleryPagerAdapter
    lateinit var screenshots : List<Screenshot>
    var selectedPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //MENU POR DEFECTO
        supportActionBar?.hide()
        //GET EXTRAS
        selectedPosition = intent.getIntExtra(EXTRA_SCREENSHOT_INDEX,0)
        screenshots = intent.getStringArrayExtra(EXTRA_SCREENSHOTS_ARRAY)!!.map { Screenshot(it) }
        //VIEW PAGER
        pagerAdapter = GalleryPagerAdapter(screenshots)
        binding.galleryViewPager.adapter = pagerAdapter
        binding.galleryViewPager.currentItem = selectedPosition
        binding.galleryViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectedPosition = position
                adapter.changeSelectedPosition(selectedPosition)
            }
        })
        //RECYCLER VIEW
        adapter = GalleryAdapter(screenshots,selectedPosition){position ->
            selectedPosition = position
            binding.galleryViewPager.currentItem = selectedPosition
        }
        binding.galleryRecyclerView.adapter = adapter
        binding.galleryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.galleryRecyclerView.scrollToPosition(selectedPosition)
    }
}