package com.example.util

import java.text.DecimalFormat

fun indianNumber(value: Double): String {
    val formatter = DecimalFormat("##,##,##,##,##,##0.00")
    return formatter.format(value)
}
