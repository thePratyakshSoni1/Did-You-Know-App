package com.example.didyouknow.adapters

import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.example.didyouknow.R
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.databinding.SearchFragmentItemBinding
import java.text.SimpleDateFormat
import javax.inject.Inject


class BlogsSearchAdapter @Inject constructor(
    private val glide: RequestManager
) : BasePostItemsAdapter<SearchFragmentItemBinding>(R.layout.search_fragment_item) {

    override var differ: AsyncListDiffer<BlogPost> = AsyncListDiffer(this, diffCallback)

    override fun onBindHolder(binding: SearchFragmentItemBinding, position: Int) {

        binding.apply{

            mainCard.setOnClickListener {
                onItemClickListenr?.let {click ->
                    click(blogs[position].articleId)
                }
            }

            postTitle.text = blogs[position].title
            dateText.text = SimpleDateFormat("dd MMMM yyyy").format( blogs[position].date )
            glide.load(blogs[position].imageUrl).into(binding.imageView)

        }

    }


}