package ru.dbuzin.dev.sbertestapp.domain.model

import com.google.gson.annotations.SerializedName

data class LatestRates(
    val status: Int,
    val message: String,
    @SerializedName("data") val data: HashMap<String, Double>
)
