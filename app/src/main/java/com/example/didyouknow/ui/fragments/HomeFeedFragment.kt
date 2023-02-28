package com.example.didyouknow.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.didyouknow.R
import com.example.didyouknow.adapters.HomeFeedAdapter
import com.example.didyouknow.databinding.FragmentHomeFeedBinding
import com.example.didyouknow.other.DialogHandlers
import com.example.didyouknow.other.Resources
import com.example.didyouknow.other.Status
import com.example.didyouknow.ui.viewmodels.HomeFeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFeedFragment : Fragment() {

    @Inject
    lateinit var blogsAdapter:HomeFeedAdapter

    private var _binding: FragmentHomeFeedBinding? = null
    val binding: FragmentHomeFeedBinding get() = _binding!!

    private val viewModel: HomeFeedViewModel by viewModels()
    private lateinit var dialogHandlers:DialogHandlers


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_feed, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogHandlers = DialogHandlers(requireContext())
//        Toast.makeText(requireContext(),"You Are in HomeFrag", Toast.LENGTH_SHORT).show()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = blogsAdapter

        binding.blogSearchbar.setOnClickListener {
            val action:NavDirections = HomeFeedFragmentDirections.actionHomeFeedFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        refreshBlogs()
        addListenerstoAdapter()

        binding.refreshLayout.setOnRefreshListener{
            viewModel.refreshBlogs{
                binding.refreshLayout.isRefreshing = false
            }
        }

        binding.addPostButton.setOnClickListener {
            val action = HomeFeedFragmentDirections.actionHomeFeedFragmentToAddPostFragment()
            findNavController().navigate(action)
        }

        binding.toolbar.setOnLongClickListener {
            showMyDialog()
            true
        }

        viewModel.blogPosts.observe(viewLifecycleOwner){
//            Toast.makeText(requireContext(),"Updating blogs", Toast.LENGTH_SHORT).show()
            blogsAdapter.blogs = it.data
            Log.d("HomeFeedViewModelLogs","Blogs Updated ${blogsAdapter.blogs}")
        }


    }

    private fun refreshBlogs(){
        binding.refreshLayout.isRefreshing = true
        viewModel.refreshBlogs {
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun showMyDialog(){
        DialogHandlers(requireContext()).showWarningDialog(
            "Developer: PRATYAKSH SONI\n\nApp's Pulbic Site: \"didyouknowthat.onrender.com\"\n\nDev. Contact: pratyakshsoni2004@gmail.com",
            positiveButtonTxt = "View Site",
            negativeButtonTxt = "Back",
            onPositiveButtonClick = {
                startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://didyouknowthat.onrender.com/"))
                )
            },
            onNegativeButtonClick = { Unit },
            dialogImgRes = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_my_logo)!!,
            buttonColorResId = R.color.button_color_green
        )
    }

    private fun addListenerstoAdapter(){

        blogsAdapter.setClickListeners(
            postClickListener = {
                val action = HomeFeedFragmentDirections.actionHomeFeedFragmentToBlogDetailFragment3(it)
                findNavController().navigate(action)
            },
            onShareMenuClick = { blogDocId ->
                val actionInten = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "https://didyouknowthat.onrender.com/article/${blogDocId}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(actionInten, "Did You Know ?")
                startActivity(shareIntent)
            },
            onEditMenuClick = {
                val action = HomeFeedFragmentDirections.actionHomeFeedFragmentToBlogDetailFragment3(it, true)
                findNavController().navigate(action)
            },
            onDeleteMenuClick = { blogDocId, imgName ->

                val blogTitle = blogsAdapter.blogs.find {
                    it.articleId == blogDocId
                }?.title

                dialogHandlers.showWarningDialog(

                    "Delete blog: \n${blogTitle} ?",
                    "Delete",
                    negativeButtonTxt = "Cancel",
                    onPositiveButtonClick = {
                        val deletionStatus = MutableLiveData<Resources<Boolean>>()
                        dialogHandlers.showProgressDialog(
                            viewLifecycleOwner,
                            deletionStatus,
                            onDoneClick = {
                                refreshBlogs()
                            },
                            dialogSuccessTxt = "Blog Deleted",
                            dialogLoadingTxt = "Deleting your blog",
                            dialogErrorTxt = "Error while deleting !"
                        )
                        deletionStatus.postValue(viewModel.deleteBlog(blogDocId, imgName))
                    },
                    onNegativeButtonClick = {
                        Unit
                    },
                    dialogImgRes = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete)!!
                )
            }
        ) }


}