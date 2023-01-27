package com.example.didyouknow.other

class Event<out T>( private val data:T) {

    var hasBeenHandled = false
        private set

    private fun getContentIfNotHandled():T? {

        return if(hasBeenHandled) null else{
            hasBeenHandled = true
            data
        }

    }

    fun peekContent() = data


}
