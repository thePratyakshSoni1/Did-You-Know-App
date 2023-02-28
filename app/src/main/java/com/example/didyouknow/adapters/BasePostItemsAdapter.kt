package com.example.didyouknow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.didyouknow.data.entities.BlogPost

abstract class BasePostItemsAdapter<BINDING: ViewDataBinding>(
    private val layoutId: Int
): RecyclerView.Adapter<BasePostItemsAdapter.BasePostItemViewHolder<BINDING>>() {

    class BasePostItemViewHolder<VIEWBINDING: ViewDataBinding>(val binding: VIEWBINDING): RecyclerView.ViewHolder(binding.root)

    var blogs:List<BlogPost>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    val diffCallback = object: DiffUtil.ItemCallback<BlogPost>(){
        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract var differ : AsyncListDiffer<BlogPost>

    abstract fun onBindHolder(binding:BINDING, position:Int)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BasePostItemViewHolder<BINDING> {
        val binder = DataBindingUtil.inflate<BINDING>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )

        return BasePostItemViewHolder(binder)
    }

    override fun onBindViewHolder(holder: BasePostItemViewHolder<BINDING>, position: Int) {
       onBindHolder(holder.binding, position)
    }

    override fun getItemCount(): Int {
        return blogs.size
    }

    protected var onItemClickListenr: ((blogId: String) -> Unit)? = null

    fun setItemClickListener(listener:(blogDocId:String)->Unit){
        onItemClickListenr = listener
    }

}