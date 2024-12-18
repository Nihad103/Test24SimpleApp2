package com.example.test24simpleapp2.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
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

        val postId = args.postId

        if (isOnline(requireContext())) {
            binding.progressBar.visibility = View.VISIBLE
            binding.errorTextView.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            viewModel.data.observe(viewLifecycleOwner, Observer { data ->
                val post = data.find { it.id == postId }
                if (post != null) {
                    binding.cardView.visibility = View.VISIBLE
                    binding.titleTextView.text = post.title
                    binding.bodyTextView.text = post.body
                    binding.errorTextView.visibility = View.GONE
                } else {
                    binding.cardView.visibility = View.GONE
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.errorTextView.text = "No data found"
                }
                binding.progressBar.visibility = View.GONE
            })
            viewModel.errorMsg.observe(viewLifecycleOwner, Observer { error ->
                error?.let {
                    binding.progressBar.visibility = View.GONE
                    binding.linerLayout.visibility = View.GONE
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.errorTextView.text = it
                }
            })
            viewModel.fetchData(postId, postId)
        } else {
            binding.errorTextView.text = "No internet connection"
            binding.errorTextView.visibility = View.VISIBLE
            binding.titleTextView.visibility = View.GONE
            binding.bodyTextView.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}