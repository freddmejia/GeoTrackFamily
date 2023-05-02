package com.example.geotrackfamily.observer

interface UIObserverGeneric<T> {
    fun onOkButton(data: T)
    fun onCancelButton(data: T)
}