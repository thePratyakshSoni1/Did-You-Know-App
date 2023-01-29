package com.example.didyouknow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.didyouknow.R
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.databinding.BlogpostLayoutBinding
import javax.inject.Inject

class HomeFeedAdapter @Inject constructor(
    val glide:RequestManager
):RecyclerView.Adapter<HomeFeedAdapter.HomeFeedAdapterViewholder>() {

    inner class HomeFeedAdapterViewholder(val binding: BlogpostLayoutBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<BlogPost>(){
        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer<BlogPost>(this, diffCallback)

    var blogs:List<BlogPost>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFeedAdapterViewholder {
        return HomeFeedAdapterViewholder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.blogpost_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeFeedAdapterViewholder, position: Int) {
        holder.binding.apply {
            postTitle.text = blogs[position].Title
            postContent.text = blogs[position].Content
            glide.load(blogs[position].imageUrl).into(postThumbnail)

        }

        holder.binding.root.setOnClickListener {
            clickListener?.let { click ->
                click( blogs[position].articleId )
            }
        }



    }

    private var clickListener:((blogDocId:String)->Unit)? = null

    override fun getItemCount(): Int {
        return blogs.size
    }

    fun setClickListener( listener:(blogDocId:String)->Unit ){
        clickListener = listener
    }
}