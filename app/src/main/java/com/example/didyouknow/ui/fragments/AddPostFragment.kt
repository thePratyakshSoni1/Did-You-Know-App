package com.example.didyouknow.ui.fragments

import android.content.UriMatcher
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.didyouknow.R
import com.example.didyouknow.databinding.FragmentAddPostBinding
import com.example.didyouknow.other.Status
import com.example.didyouknow.ui.viewmodels.AddpostFragmentViewModel
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto.EnumReservedRangeOrBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddPostFragment : Fragment() {

    @Inject
    lateinit var glide:RequestManager

    private var _binding :FragmentAddPostBinding? = null
    val binding get() = _binding!!

    private val viewModel:AddpostFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_post, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewmodel = viewModel
        setObservers()
        setTextUpdaters()

        binding.postButton.setOnClickListener {
            Toast.makeText(requireContext(), "Posting Blog", Toast.LENGTH_SHORT).show()
            val status = viewModel.postBlog()
            val toastMsg = when(status.status){

                Status.ERROR -> status.message
                Status.LOADING -> "Posting Blog :)"
                Status.SUCCESS ->  "Blog Posted Successfully"

            }
            Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_LONG).show()
            if(status.status == Status.SUCCESS) findNavController().popBackStack()
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setObservers(){
        viewModel.postimgLink.observe(viewLifecycleOwner){
            glide.load(it).into(binding.postImagePrev)

        }
    }

    private fun setTextUpdaters(){

        binding.blogTitle.addTextChangedListener {
            viewModel.updateBlogTitleTxt(it.toString())
        }

        binding.blogImageLink.addTextChangedListener {
            viewModel.updateBlogImageLinkTxt(it.toString())
        }

        binding.blogContent.addTextChangedListener {
            viewModel.updateBlogContentTxt(it.toString())
        }

    }


}