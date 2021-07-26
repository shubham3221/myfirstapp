package com.example.myfirstapp.extra

import android.location.Location

interface LocationChangedListener {
    fun locationChanged(location: Location?)
}