package com.example.didyouknow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.didyouknow.R
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.databinding.BlogpostLayoutBinding
import javax.inject.Inject

class HomeFeedAdapter (
    val glide:RequestManager,
    val context:Context
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
            postTitle.text = blogs[position].title
            postContent.text = blogs[position].content
            glide.load(blogs[position].imageUrl).into(postThumbnail)

        }

        holder.binding.root.setOnClickListener {
            clickListener?.let { click ->
                click( blogs[position].articleId )
            }
        }

        holder.binding.postOptions.setOnClickListener{

            val popUpMenu = PopupMenu(context, it).apply {
                inflate(R.menu.blogpost_menu)
                setOnMenuItemClickListener {

                    when(it.itemId){

                        R.id.postEditMenuItem -> {
                            onEditListener?.let {click ->
                                click(blogs[position].articleId)
                            }
                            true
                        }

                        R.id.postShareMenuItem -> {
                            onShareListener?.let {click ->
                                click(blogs[position].articleId)
                            }
                            true
                        }

                        R.id.postDeleteMenuItem -> {
                            onDeleteListener?.let {click ->
                                click(blogs[position].articleId, blogs[position].imageName)
                            }
                            true
                        }

                        else -> false
                    }

                }

                show()
            }

        }



    }

    private var clickListener:((blogDocId:String)->Unit)? = null
    private var onShareListener:((blogDocId:String)->Unit)? = null
    private var onEditListener:((blogDocId:String)->Unit)? = null
    private var onDeleteListener:((blogDocId:String, imageName:String? )->Unit)? = null

    override fun getItemCount(): Int {
        return blogs.size
    }

    fun setClickListeners(
        postClickListener:(blogDocId:String)->Unit,
        onShareMenuClick:( blogDocId:String ) -> Unit,
        onDeleteMenuClick:( blogDocId:String, imageName:String? ) -> Unit,
        onEditMenuClick:( blogDocId:String ) -> Unit

    ){
        clickListener = postClickListener

        onShareListener = onShareMenuClick
        onEditListener = onEditMenuClick
        onDeleteListener = onDeleteMenuClick
    }
}