package com.myapplication.valdistoryapp.ui.components

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout : TextInputLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}