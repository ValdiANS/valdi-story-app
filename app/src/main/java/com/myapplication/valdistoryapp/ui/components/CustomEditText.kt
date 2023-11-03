package com.myapplication.valdistoryapp.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.utils.isEmail

@SuppressLint("ResourceType")
class CustomEditText : TextInputEditText {

    private var errorMessage: String? = null
    private var isRequired: Boolean? = null
    private var requiredErrorMessage: String? = null
    private var minLength: Int = 0
    private var minLengthErrorMessage: String? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupCustomAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupCustomAttributes(context, attrs)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        init()
    }

    private fun init() {
        val parentView = parent.parent as TextInputLayout

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing to do here...
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    isRequired == true && s.toString().isEmpty() -> {
                        parentView.error = requiredErrorMessage
                    }

                    minLength > 0 && s.toString().length < minLength -> {
                        parentView.error = minLengthErrorMessage
                    }

                    inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS &&
                            !s.toString().isEmail() -> {
                        parentView.error = errorMessage
                    }

                    else -> {
                        parentView.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Nothing to do here...
            }
        })
    }

    private fun setupCustomAttributes(context: Context, attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomEditText,
            0, 0
        ).apply {
            try {
                errorMessage = getString(R.styleable.CustomEditText_errorMessage)
                isRequired = getBoolean(R.styleable.CustomEditText_required, false)
                requiredErrorMessage = getString(R.styleable.CustomEditText_requiredErrorMessage)
                minLength = getInt(R.styleable.CustomEditText_minLength, 0)
                minLengthErrorMessage = getString(R.styleable.CustomEditText_minLengthErrorMessage)
            } finally {
                recycle()
            }
        }
    }
}