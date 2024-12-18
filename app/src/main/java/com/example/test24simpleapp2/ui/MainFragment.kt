package com.example.test24simpleapp2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test24simpleapp2.adapter.RecyclerAdapter
import com.example.test24simpleapp2.databinding.FragmentMainBinding
import com.example.test24simpleapp2.viewModel.MyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.recyclerView.adapter = RecyclerAdapter(it) { postId ->
                val action = MainFragmentDirections.actionMainFragmentToDetailsFragment(postId)
                findNavController().navigate(action)
            }
            binding.progressBar.visibility = View.GONE
        })
        viewModel.fetchAllData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}