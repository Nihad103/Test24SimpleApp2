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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test24simpleapp2.adapter.RecyclerAdapter
import com.example.test24simpleapp2.databinding.FragmentMainBinding
import com.example.test24simpleapp2.viewModel.MyViewModel
import org.koin.android.ext.android.inject

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyViewModel by inject()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

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
        if (isOnline(requireContext())) {
            fetchData()
        } else {
            showError("No internet connection")
        }
    }

    private fun fetchData() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.progressBar.visibility = View.VISIBLE
        binding.errorTextView.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        try {
            viewModel.data.observe(viewLifecycleOwner, Observer { data ->
                if (data.isEmpty()) {
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.recyclerView.adapter = RecyclerAdapter(data) { postId ->
                        val action =
                            MainFragmentDirections.actionMainFragmentToDetailsFragment(postId)
                        findNavController().navigate(action)
                    }
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.errorTextView.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            })
//            viewModel.errorMsg.observe(viewLifecycleOwner, Observer { error ->
//                binding.progressBar.visibility = View.VISIBLE
//                error?.let {
//                    binding.progressBar.visibility = View.GONE
//                    binding.recyclerView.visibility = View.GONE
//                    binding.errorTextView.visibility = View.VISIBLE
//                    binding.errorTextView.text = "Something went wrong"
//                }
//            })
            Log.d("MainFragment", "fetchAllData")
            viewModel.fetchAllData()
        } catch (e: Exception) {
            binding.errorTextView.text = "Exception. Something went wrong"
            binding.errorTextView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            Log.e("MainFragment", "Exception. Something went wrong")
        }
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}