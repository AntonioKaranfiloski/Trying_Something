package com.example.tryingsomething.dto

data class ControlPoint(
        var latitude: String = "",
        var longitude: String = "",
        var date: String = "",
        var cpId: String = ""
) {
    override fun toString(): String {
        return " $latitude $longitude $date $cpId"
    }
}