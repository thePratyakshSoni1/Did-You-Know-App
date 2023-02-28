package com.example.didyouknow.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.didyouknow.R
import com.example.didyouknow.adapters.BlogsSearchAdapter
import com.example.didyouknow.databinding.FragmentSearchBinding
import com.example.didyouknow.ui.viewmodels.SearchFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.Provider.Service
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    val binding get() = _binding!!

    private val viewModel by viewModels<SearchFragmentViewModel>()

    @Inject
    lateinit var searchAdapter:BlogsSearchAdapter

    private lateinit var inputMethodManager: InputMethodManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        _binding?.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputMethodManager = requireContext().getSystemService(android.app.Service.INPUT_METHOD_SERVICE) as InputMethodManager
        initializeScreen()
        initializeRecyclerView()
        setObservers()

    }

    private fun initializeScreen(){

        binding.viewModel = viewModel
        if(binding.blogSearchbar.requestFocus()){
            inputMethodManager.showSoftInput(binding.blogSearchbar, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.blogSearchbar.apply {
            setOnEditorActionListener { v, actionId, event ->
                if(actionId == IME_ACTION_SEARCH){
                    viewModel.query()
                    hideSoftKeyboard()
                }else{
                    false
                }
            }
        }

        binding.backButton.setOnClickListener {
            hideSoftKeyboard()
            findNavController().popBackStack()
        }


    }

    private fun initializeRecyclerView(){

        binding.recyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        searchAdapter.setItemClickListener { docId ->
            val action = SearchFragmentDirections.actionSearchFragmentToBlogDetailFragment(docId)
            findNavController().navigate(action)
        }

    }

    private fun setObservers(){
        viewModel.blogsResults.observe(viewLifecycleOwner){
            searchAdapter.blogs = it
            Log.d("SearchFragmentLogs", "Blogs search updated: ${it.size} blogs found")
        }

        viewModel.queryString.observe(viewLifecycleOwner){
            Log.d("SearchFragmentLogs", "Query: $it, queried: ${viewModel.queryString.value}")
            viewModel.query()
        }

        binding.blogSearchbar.addTextChangedListener { queryString ->
            viewModel.updateQueryString(queryString.toString())
        }


    }

    private fun hideSoftKeyboard():Boolean{
        return inputMethodManager.hideSoftInputFromWindow(
            view?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }


}