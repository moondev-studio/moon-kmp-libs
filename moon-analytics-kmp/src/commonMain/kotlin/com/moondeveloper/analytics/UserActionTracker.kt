package com.moondeveloper.analytics

/**
 * Tracks user interactions: button clicks, form submissions, dialog responses.
 *
 * @see NoOpUserActionTracker for testing
 */
interface UserActionTracker {
    /** Track a generic user action with optional target and parameters. */
    fun trackAction(action: String, target: String? = null, params: Map<String, Any> = emptyMap())

    /** Track a button click with the button name and containing screen. */
    fun trackButtonClick(buttonName: String, screenName: String)

    /** Track a dialog response (e.g., "confirm", "cancel"). */
    fun trackDialogResponse(dialogName: String, response: String)

    /** Track a form submission with success/failure status. */
    fun trackFormSubmit(formName: String, success: Boolean)
}
