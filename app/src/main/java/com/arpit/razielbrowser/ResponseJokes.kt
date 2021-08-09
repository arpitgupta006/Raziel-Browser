package com.arpit.razielbrowser

import com.google.gson.annotations.SerializedName

data class ResponseJokes(

	@field:SerializedName("delivery")
	val delivery: String? = null,

	@field:SerializedName("flags")
	val flags: Flags? = null,

	@field:SerializedName("safe")
	val safe: Boolean? = null,

	@field:SerializedName("setup")
	val setup: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("lang")
	val lang: String? = null
)

