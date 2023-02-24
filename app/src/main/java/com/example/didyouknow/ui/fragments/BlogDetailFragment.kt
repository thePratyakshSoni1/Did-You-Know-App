package com.example.didyouknow.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.didyouknow.R
import com.example.didyouknow.databinding.FragmentBlogDetailBinding
import com.example.didyouknow.other.*
import com.example.didyouknow.ui.viewmodels.BlogDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class BlogDetailFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private var _binding: FragmentBlogDetailBinding? = null
    val binding: FragmentBlogDetailBinding get() = _binding!!

    private val viewModel by viewModels<BlogDetailsViewModel>()

    lateinit var imageImportContract: ActivityResultLauncher<String>
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
        setListeners()
        setObservers()
        setTextUpdaters()


        imageImportContract = registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.postValueToPostImageLink(null)
            binding.postThumbnail.setImageURI(it)
            viewModel.setImageLocalUri(it)
        }


        viewModel.initializeViewModel(
            blogId = arguments?.getString(NavigationKeys.KEY_BLOG_DOC_ID)!!,
            openForEditMode = arguments?.getBoolean(NavigationKeys.KEY_OPEN_BLOG_FOR_EDIT_MODE)!!
        )


    }

    private fun setListeners(){

        val dialogHandler  = DialogHandlers(requireContext())

        binding.toolbarNavButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editPostButton.setOnClickListener {
            viewModel.setEditingMode(true)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshBlog()

        }

        binding.postShareButton.setOnClickListener {
            val actionInten = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://didyouknowthat.onrender.com/article/${viewModel.blog.value?.data?.articleId}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(actionInten, "Did You Know ?")
            startActivity(shareIntent)
        }

        binding.updateButton.setOnClickListener {


            val blogUpdateStatus = MutableLiveData<Resources<Boolean>>()
            blogUpdateStatus.postValue(Resources.loading(false))

            dialogHandler.showProgressDialog(
                viewLifecycleOwner,
                blogUpdateStatus,
                onDoneClick = { Unit },
                dialogErrorTxt = "Can't Post Blog",
                dialogLoadingTxt = "Posting Blog...",
                dialogSuccessTxt = "Blog Posted Successfully",
            )
            CoroutineScope(Dispatchers.IO).launch{
                val result = viewModel.updateBlog()

                var toast: String
                viewModel.setEditingMode(false)
                if (result.status == Status.SUCCESS) {
                    toast = "Blog Successfully updated"
                    blogUpdateStatus.postValue(Resources.success(true))
                    viewModel.refreshBlog()
                } else if (result.status == Status.PARTIAL_SUCCESS) {
                    toast = "Some fields aren't updated"
                    blogUpdateStatus.postValue(Resources.partialSuccess(true, result.message!!))
                    viewModel.refreshBlog()
                } else {
                    toast = result.message!!
                    blogUpdateStatus.postValue(Resources.error(false, "Can't post blog !"))
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.chooseImgButton.setOnClickListener {

            if( viewModel.isLocalImage.value == true ){
                viewModel.setImageLocalUri(null)
                binding.postThumbnail.setImageURI(null)
                viewModel.postValueToPostImageLink(null)
            }else{
                imageImportContract.launch("image/*")
            }

        }

        binding.cancelButton.setOnClickListener {
            dialogHandler.showWarningDialog(
                "Do you wan't to discard changes to the blog ?",
                "Keep",
                "Discard",
                onPositiveButtonClick = {
                    Unit
                },
                onNegativeButtonClick = {
                    viewModel.setEditingMode(false)
                },
                buttonColorResId = R.color.button_color_green
            )

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
            viewModel.updateBlogImageLinkTxt(
                if(viewModel.isLocalImage.value  == true ) "LOCAL IMAGE ADDED" else it.toString()
            )
        }

    }

    private fun setObservers(){

        viewModel.blog.observe(viewLifecycleOwner){

            when(it.status){

                Status.LOADING -> toggleBlogVisibility(setBlogToVisible = false )

                Status.SUCCESS , Status.PARTIAL_SUCCESS ->{
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

        viewModel.postimgLink.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty() ){
                viewModel.postImageLinkUpdateState(true)
                Log.d("BlogDetailsFragment", "postImgLink Updated the value to $it")
                if(viewModel.isLocalImage.value == false){
                    glide.load(viewModel.postimgLink.value).into(binding.postThumbnail)
                }
            }
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner){
            binding.swipeRefreshLayout.isRefreshing = it
        }

    }

    private fun toggleBlogVisibility(
        setBlogToVisible:Boolean,
        setError:Boolean = false
    ){

        binding.apply{

            postTitle.visibility = if( setBlogToVisible ) View.VISIBLE else View.GONE
            postContent.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            postThumbnail.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            blogAnalyticLayout.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            date.visibility = if(setBlogToVisible) View.VISIBLE else View.GONE
            errImageView.visibility = if(setError) View.VISIBLE else View.GONE

        }

    }

}