package com.company.carryon.util

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

fun Double.formatDecimal(digits: Int): String {
    val isNeg = this < 0
    val absVal = abs(this)
    val factor = 10.0.pow(digits)
    val rounded = round(absVal * factor).toLong()
    val intPart = rounded / factor.toLong()
    val fracPart = rounded % factor.toLong()
    return if (digits == 0) {
        "${if (isNeg) "-" else ""}$intPart"
    } else {
        val fracStr = fracPart.toString().padStart(digits, '0')
        "${if (isNeg) "-" else ""}$intPart.$fracStr"
    }
}
