package com.example.networkmonitoring

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import com.example.networkmonitoring.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  var _binding :ActivityMainBinding? = null
    private val binding get()=_binding!!
    private var isNetworkLayoutHide = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        observeNetworkConnection()
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeNetworkConnection(){
        NetworkStatusHelper(this@MainActivity).observe(this, { networkStatus->
            when (networkStatus) {
                is NetworkStatus.Available -> display(networkStatus)
                is NetworkStatus.Unavailable -> display(networkStatus)
            }
        })
    }

    private fun display(status: NetworkStatus) {
        binding.apply {
            when (status) {
                is NetworkStatus.Available -> {
                    button.apply {
                        setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.teal_200))
                        text = getString(R.string.connected_network)
                        animateNetworkLayout(true)
                        visibility = View.GONE
                    }
                    animateNetworkLayout(true)
                    textView.text = status.message
                }
                is NetworkStatus.Unavailable -> {
                    button.apply {
                        setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.purple_200))
                        text = getString(R.string.open_network_connection)
                        setOnClickListener {
                            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                Intent(Settings.ACTION_DATA_USAGE_SETTINGS)
                            } else {
                                Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
                            }
                            startActivity(intent)
                        }
                        visibility = View.VISIBLE
                    }
                    textView.text = status.message
                    animateNetworkLayout(false)
                }
            }
        }
    }

    private fun animateNetworkLayout(hide: Boolean) {
        if (isNetworkLayoutHide && hide || !isNetworkLayoutHide && !hide) return
        isNetworkLayoutHide = hide
        val moveY = if (hide) {
            binding.networkTextView.text = getString(R.string.established_internet_connection)
            binding.networkLayout.
                setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.teal_200))
            -(2 * binding.networkLayout.height)
        } else {
            binding.networkLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
            binding.networkTextView.text = getString(R.string.no_internet_connection)
            0
        }
       binding.networkLayout.animate().translationY(moveY.toFloat())
            .setStartDelay(100).setDuration(600).start()
    }

}