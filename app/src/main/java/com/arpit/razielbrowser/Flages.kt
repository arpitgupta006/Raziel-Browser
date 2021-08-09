package com.arpit.razielbrowser

import com.google.gson.annotations.SerializedName

data class Flags(

        @field:SerializedName("sexist")
        val sexist: Boolean? = null,

        @field:SerializedName("explicit")
        val explicit: Boolean? = null,

        @field:SerializedName("religious")
        val religious: Boolean? = null,

        @field:SerializedName("nsfw")
        val nsfw: Boolean? = null,

        @field:SerializedName("political")
        val political: Boolean? = null,

        @field:SerializedName("racist")
        val racist: Boolean? = null
)
