package com.example.test24simpleapp2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.test24simpleapp2.databinding.FragmentDetailsBinding
import com.example.test24simpleapp2.viewModel.MyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyViewModel by viewModel()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        val postId = args.postId
        viewModel.data.observe(viewLifecycleOwner, Observer { data ->
            val post = data.find { it.id == postId }
            post?.let {
                binding.titleTextView.text = it.title
                binding.bodyTextView.text = it.body
            }
            binding.progressBar.visibility = View.GONE
        })
        viewModel.fetchData(postId, postId)
    }
}