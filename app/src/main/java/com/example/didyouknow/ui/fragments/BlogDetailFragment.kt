package com.example.didyouknow.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.example.didyouknow.R
import com.example.didyouknow.databinding.FragmentBlogDetailBinding
import com.example.didyouknow.databinding.FragmentHomeFeedBinding
import com.example.didyouknow.other.Status
import com.example.didyouknow.ui.viewmodels.BlogDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class BlogDetailFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentBlogDetailBinding? = null
    val binding: FragmentBlogDetailBinding get() = _binding!!

    private val viewModel by viewModels<BlogDetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blog_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initializeViewModel(arguments?.getString("blogDocId")!!)
        viewModel.blog.observe(viewLifecycleOwner){

            when(it.status){

                Status.LOADING -> toggleBlogVisibility(setBlogToVisible = false, setloading = true)

                Status.SUCCESS ->{
                    it.data.let{ blog ->
                        if(blog != null){

                            toggleBlogVisibility( setBlogToVisible = true )
                            binding.postTitle.text = blog.Title
                            binding.postContent.text = blog.Content
                            binding.date.text = SimpleDateFormat("dd M yyyy").format( blog.Date )

                            glide.load(blog.imageUrl).into(binding.postThumbnail)
                            Log.d("BlogDetailsFragmentLogs", "Blogs Update Succesfully & ui is too")
                        }else{
                            toggleBlogVisibility(setBlogToVisible = false, setError = true)
                        }
                    }
                }

                Status.ERROR -> toggleBlogVisibility(setBlogToVisible = false, setError = true)

            }

        }

    }

    private fun toggleBlogVisibility(
        setBlogToVisible:Boolean,
        setError:Boolean = false,
        setloading:Boolean= false
    ){

        binding.apply{

            postTitle.visibility = if( setBlogToVisible ) View.VISIBLE else View.GONE
            postContent.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            postThumbnail.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            blogAnalyticLayout.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            errImageView.visibility = if(setError) View.VISIBLE else View.GONE
            progressBar.visibility = if(setloading) View.VISIBLE else View.GONE

        }

    }

}