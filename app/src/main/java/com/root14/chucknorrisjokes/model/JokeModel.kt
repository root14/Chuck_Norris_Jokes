package com.root14.chucknorrisjokes.model

import com.google.gson.annotations.SerializedName


data class JokeModel(
    @SerializedName("icon_url") var iconUrl: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("value") var value: String? = null,
)