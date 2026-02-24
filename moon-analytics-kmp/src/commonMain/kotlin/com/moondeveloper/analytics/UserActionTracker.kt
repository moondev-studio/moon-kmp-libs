package com.moondeveloper.analytics

interface UserActionTracker {
    fun trackAction(action: String, target: String? = null, params: Map<String, Any> = emptyMap())
    fun trackButtonClick(buttonName: String, screenName: String)
    fun trackDialogResponse(dialogName: String, response: String)
    fun trackFormSubmit(formName: String, success: Boolean)
}
