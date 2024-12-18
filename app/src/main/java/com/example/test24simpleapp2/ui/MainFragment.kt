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

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        if (isOnline(requireContext())) {
            binding.progressBar.visibility = View.VISIBLE
            binding.errorTextView.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            viewModel.data.observe(viewLifecycleOwner, Observer { data ->
                if (data.isEmpty()) {
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.recyclerView.adapter = RecyclerAdapter(data) { postId ->
                        val action = MainFragmentDirections.actionMainFragmentToDetailsFragment(postId)
                        findNavController().navigate(action)
                    }
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.errorTextView.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            })
            viewModel.errorMsg.observe(viewLifecycleOwner, Observer { error ->
                error?.let {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.errorTextView.text = it
                }
            })
            Log.d("MainFragment", "fetchAllData")
            viewModel.fetchAllData()
        } else {
            binding.errorTextView.text = "No internet connection"
            binding.errorTextView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun isOnline(context: Context): Boolean {
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