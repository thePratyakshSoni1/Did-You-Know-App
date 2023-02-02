package com.example.didyouknow.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.didyouknow.R
import com.example.didyouknow.databinding.FragmentBlogDetailBinding
import com.example.didyouknow.other.BlogPostEditing
import com.example.didyouknow.other.Status
import com.example.didyouknow.ui.viewmodels.BlogDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
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
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewmodel = viewModel
        setObservers()
        setTextUpdaters()

        viewModel.initializeViewModel(arguments?.getString("blogDocId")!!)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.editPostButton.setOnClickListener {
            viewModel.setEditingMode(true)
        }

        binding.updateButton.setOnClickListener {

            val result = viewModel.updateBlog()
            var toast:String
            viewModel.setEditingMode(false)
            if (result.status == Status.SUCCESS){
                toast = "Blog Successfully updated"
                viewModel.refreshBlog()
            }
            else {
                toast = result.message!!
            }
            Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT ).show()

        }

        binding.cancelButton.setOnClickListener {
            viewModel.setEditingMode(false)
        }

    }

    private fun setTextUpdaters(){

        binding.postTitle.addTextChangedListener {
            viewModel.updateBlogTitleTxt(it.toString())
        }

        binding.postContent.addTextChangedListener {
            viewModel.updateBlogContentTxt(it.toString())
        }

        binding.imgLinkTextView.addTextChangedListener {
            viewModel.updateBlogImageLinkTxt(it.toString())
        }

    }

    private fun setObservers(){

        viewModel.blog.observe(viewLifecycleOwner){

            when(it.status){

                Status.LOADING -> toggleBlogVisibility(setBlogToVisible = false, setloading = true)

                Status.SUCCESS ->{
                    it.data.let{ blog ->
                        if(blog != null){
                            toggleBlogVisibility( setBlogToVisible = true )

                            viewModel.updateBlogTitleTxt(blog.title)
                            viewModel.updateBlogImageLinkTxt(blog.imageUrl)
                            viewModel.updateBlogContentTxt(blog.content)
                            binding.date.text = SimpleDateFormat("dd MMMM yyyy").format( blog.date )
                            binding.dislikeCountTxt.text = blog.totalDislikes.toString()
                            binding.likeCountTxt.text = blog.totalLikes.toString()
                            glide.load(blog.imageUrl).into(binding.postThumbnail)
                            Log.d("BlogDetailsFragmentLogs", "Blogs Update Succesfully & ui is too ${viewModel.postTitle.value}")
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
            date.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            errImageView.visibility = if(setError) View.VISIBLE else View.GONE
            progressBar.visibility = if(setloading) View.VISIBLE else View.GONE

        }

    }

}