package com.example.myfirstapp.utils

import android.location.Location

interface LocationChangedListener {
    fun locationChanged(location: Location?)
}