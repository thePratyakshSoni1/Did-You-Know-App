package com.example.didyouknow.other

data class Resources<out T>(
    val status:Status,
    val data:T,
    val message:String? = null
) {

    companion object{

        fun <T> loading(data:T) = Resources(status= Status.LOADING, data)
        fun <T> success(data:T) = Resources(status= Status.SUCCESS, data)
        fun <T> error(data:T, message:String?) = Resources(status= Status.ERROR, data, message)

    }

}

enum class Status{
    LOADING,
    ERROR,
    SUCCESS
}