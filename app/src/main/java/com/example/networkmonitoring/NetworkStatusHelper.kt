package com.example.networkmonitoring

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkStatusHelper(context: Context) : LiveData<NetworkStatus>() {

    //available,unavailable message
    private var availableMessage = context.getString(R.string.established_internet_connection)
    private var unAvailableMessage = context.getString(R.string.no_internet_connection)

    //established network list
    val validatedNetworkConnections: ArrayList<Network> = ArrayList()
    var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    fun announceStatus() {
        if (!validatedNetworkConnections.isNullOrEmpty()) {
            postValue(NetworkStatus.Available(availableMessage))
        } else {
            postValue(NetworkStatus.Unavailable(unAvailableMessage))
        }
    }

    private fun getConnectivityManagerCallback() =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val networkCapability = connectivityManager.getNetworkCapabilities(network)
                val hasNetworkConnection =
                    networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        ?: false
                if (hasNetworkConnection) {
                    validatedNetworkConnections.add(network)
                    announceStatus()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                validatedNetworkConnections.remove(network)
                announceStatus()
            }

//            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
//                super.onCapabilitiesChanged(network, networkCapabilities)
//                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
//                    validatedNetworkConnections.add(network)
//                } else {
//                    validatedNetworkConnections.remove(network)
//                }
//                announceStatus()
//            }
        }

    override fun onActive() {
        super.onActive()
        announceStatus()
        connectivityManagerCallback = getConnectivityManagerCallback()
        val networkRequest = NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }


}