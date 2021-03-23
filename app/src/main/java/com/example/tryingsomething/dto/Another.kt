package com.example.tryingsomething.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Another(
    @PrimaryKey @SerializedName("id") var bib: Int = 0,
    @SerializedName("genus") var name: String,
    @SerializedName("species") var surName: String,
    @SerializedName("common") var common: String
){
    override fun toString(): String {
        return surName
    }
}