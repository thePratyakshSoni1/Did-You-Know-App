package com.example.didyouknow.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.didyouknow.R
import com.example.didyouknow.databinding.FragmentAddPostBinding
import com.example.didyouknow.other.DialogHandlers
import com.example.didyouknow.other.Resources
import com.example.didyouknow.ui.viewmodels.AddpostFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddPostFragment : Fragment() {

    @Inject
    lateinit var glide:RequestManager

    private var _binding :FragmentAddPostBinding? = null
    val binding get() = _binding!!

    private val viewModel:AddpostFragmentViewModel by viewModels()
    lateinit private var dialoghandler:DialogHandlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_post, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        dialoghandler = DialogHandlers(requireContext())
        setTextUpdaters()
        setPostButtonClickListener()
        setChooseImgButtonClickListener()
        Log.d("AddPostFragmentLogs", "Localimage at create() is ${viewModel.isLocalImage.value}")

        viewModel.postimgLink.observe( viewLifecycleOwner ){

            if(!it.isNullOrEmpty() && viewModel.isLocalImage.value == false){
                viewModel.postImageLinkUpdateState(true)
                Log.d("AddPostFragmentLogs", "postImgLink Updated the value to $it")
                glide.load(viewModel.postimgLink.value).into(binding.postImagePrev)
            }
            Log.d("AddPostFragmentLogs","ImgLinkValue Changed: $it")
        }

        viewModel.isLocalImage.observe(viewLifecycleOwner){
            Log.d("AddPostFragmentLogs", "isLocalImagge updated to: $it with uri: ${viewModel.imageUri}")
        }



        binding.cancelButton.setOnClickListener {
            dialoghandler.showWarningDialog(
                "Discard posting blog ?",
                positiveButtonTxt = "Keep",
                negativeButtonTxt = "Discard",
                onPositiveButtonClick = { Unit },
                onNegativeButtonClick = { findNavController().popBackStack() },
                buttonColorResId = R.color.button_color_green
            )
        }

    }

    private fun setChooseImgButtonClickListener(){

        if(viewModel.isLocalImage.value == true){
            viewModel.setImageLocalUri(null)
            binding.postImagePrev.setImageURI(null)
            viewModel.postValueToPostImageLink(null)
        }else{
            val imageChooserContract = registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) {
                viewModel.postValueToPostImageLink(null)
                viewModel.setImageLocalUri(it)
                binding.postImagePrev.setImageURI(it)
            }

            binding.imageChooseButotn.setOnClickListener {
                imageChooserContract.launch("image/*")
            }
        }

    }

    private fun setPostButtonClickListener(){

        val onPost = {
            val postingStats = MutableLiveData(Resources.loading(false))
            DialogHandlers(requireContext()).showProgressDialog(
                viewLifecycleOwner,
                postingStats,
                onDoneClick = { findNavController().popBackStack() },
                dialogErrorTxt = postingStats.value?.message ?: "Can't post blog",
                dialogLoadingTxt = "Posting Blog...",
                dialogSuccessTxt = "Blog Posted Successfully"
            )

            //Post blog to firebase
            CoroutineScope(Dispatchers.IO).launch{
                postingStats.postValue(viewModel.postBlog())
            }

        }

        binding.postButton.setOnClickListener {

            dialoghandler.showWarningDialog(
                diaogText = "Do you want to post this blog?:\n${viewModel.postTitle.value}",
                positiveButtonTxt = "Post",
                onPositiveButtonClick = {
                    onPost()
                },
                onNegativeButtonClick = {
                    findNavController().popBackStack()
                },
                dialogImgRes = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_placeholder)!!,
                buttonColorResId = R.color.button_color_green
            )

        }
    }

    private fun setTextUpdaters(){

        binding.blogTitle.addTextChangedListener {
            viewModel.updateBlogTitleTxt(it.toString())
        }

        binding.blogImageLink.addTextChangedListener {

            viewModel.updateBlogImageLinkTxt(
                if(viewModel.isLocalImage.value == true) "LOCAL IMAGE ADDED" else it.toString()
            )
            Log.d("AddPostFragmentLogs","Changing img link to: ${it.toString()}")
        }

        binding.blogContent.addTextChangedListener {
            viewModel.updateBlogContentTxt(it.toString())
        }

    }


}