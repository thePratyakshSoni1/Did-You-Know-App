package com.example.didyouknow.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.didyouknow.R
import com.example.didyouknow.adapters.HomeFeedAdapter
import com.example.didyouknow.databinding.FragmentHomeFeedBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(),"You Are in HomeFrag", Toast.LENGTH_SHORT).show()
        binding.recyclerView.adapter = blogsAdapter

        viewModel.blogPosts.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),"Updating blogs", Toast.LENGTH_SHORT).show()
            blogsAdapter.blogs = it.data
            Log.d("HomeFeedViewModelLogs","Blogs Updated")
        }


    }


}