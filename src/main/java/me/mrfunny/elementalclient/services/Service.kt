package me.mrfunny.elementalclient.services

abstract class Service {
    companion object {
        val services = arrayOf(CpsService())
    }
}