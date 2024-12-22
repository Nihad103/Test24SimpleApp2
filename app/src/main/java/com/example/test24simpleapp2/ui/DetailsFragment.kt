package com.example.test24simpleapp2.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test24simpleapp2.R
import com.example.test24simpleapp2.adapter.RecyclerAdapter
import com.example.test24simpleapp2.databinding.FragmentDetailsBinding
import com.example.test24simpleapp2.viewModel.MyViewModel
import org.koin.android.ext.android.inject

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyViewModel by inject()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

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

        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                activity?.runOnUiThread {
                    binding.errorTextView.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    fetchData()
                }
            }

            override fun onLost(network: Network) {
                activity?.runOnUiThread {
                    showError("No internet connection")
                }
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        fetchData()

        binding.backBtn.setOnClickListener {
            val action =
                DetailsFragmentDirections.actionDetailsFragmentToMainFragment()
            findNavController().navigate(action)
        }
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE
            binding.errorTextView.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            binding.backBtn.visibility = View.GONE
        try {
            val postId = args.postId
            viewModel.data.observe(viewLifecycleOwner, Observer { data ->
                if (data.isEmpty()) {
                    binding.cardView.visibility = View.GONE
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.backBtn.visibility = View.GONE
                    Log.d("DetailsFragment", "data.isEmpty()")
                    binding.errorTextView.text = "No data found"
                } else {
                    val post = data.find { it.id == postId }
                    binding.cardView.visibility = View.VISIBLE
                    binding.titleTextView.text = post!!.title
                    binding.bodyTextView.text = post.body
                    binding.errorTextView.visibility = View.GONE
                    binding.backBtn.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            })
            viewModel.errorMsg.observe(viewLifecycleOwner, Observer { error ->
                binding.progressBar.visibility = View.VISIBLE
                error?.let {
                    binding.progressBar.visibility = View.GONE
                    binding.cardView.visibility = View.GONE
                    binding.backBtn.visibility = View.GONE
                    binding.errorTextView.text = "No data found"
                    binding.errorTextView.visibility = View.VISIBLE
                }
            })
            Log.d("DetailsFragment", "fetchData")
            viewModel.fetchData(postId, postId)
        } catch (e: Exception) {
            binding.errorTextView.text = "Exception. Something went wrong"
            binding.errorTextView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            Log.e("DetailsFragment", "Exception. Something went wrong")
        }
    }

    private fun showError(message: String) {
        try {
            binding.progressBar.visibility = View.GONE
            binding.errorTextView.text = message
            binding.errorTextView.visibility = View.VISIBLE
            binding.cardView.visibility = View.GONE
            binding.backBtn.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}