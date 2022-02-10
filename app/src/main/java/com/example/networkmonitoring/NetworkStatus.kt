package com.example.networkmonitoring

sealed class NetworkStatus{
    data class Available(val message:String):NetworkStatus()
    data class Unavailable(val message:String):NetworkStatus()
}
