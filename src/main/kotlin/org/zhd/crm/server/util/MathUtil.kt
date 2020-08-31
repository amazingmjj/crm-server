package org.zhd.crm.server.util

import java.math.BigDecimal

object MathUtil {
    // 需要精确至小数点后几位
    const val DECIMAL_PLACE_ONE: Int = 1
    const val DECIMAL_PLACE_TWO: Int = 2
    const val DECIMAL_PLACE_THREE: Int = 3

    // 加法运算
    fun add(d1: Double, d2: Double, decimalPoint: Int = DECIMAL_PLACE_ONE): Double = BigDecimal(d1).add(BigDecimal(d2)).setScale(decimalPoint, BigDecimal.ROUND_HALF_UP).toDouble()

    // 减法运算
    fun sub(d1: Double, d2: Double, decimalPoint: Int = DECIMAL_PLACE_ONE): Double = BigDecimal(d1).subtract(BigDecimal(d2)).setScale(decimalPoint, BigDecimal.ROUND_HALF_UP).toDouble()

    // 乘法运算
    fun mul(d1: Double, d2: Double, decimalPoint: Int = DECIMAL_PLACE_ONE): Double = BigDecimal(d1).multiply(BigDecimal(d2)).setScale(decimalPoint, BigDecimal.ROUND_HALF_UP).toDouble()

    // 除法运算
    fun div(d1: Double, d2: Double, decimalPoint: Int = DECIMAL_PLACE_ONE): Double = BigDecimal(d1).divide(BigDecimal(d2)).setScale(decimalPoint, BigDecimal.ROUND_HALF_UP).toDouble()

    // 除法运算
    fun longDataDiv(d1: Long, d2: Long, decimalPoint: Int = DECIMAL_PLACE_ONE): Double = BigDecimal(d1).divide(BigDecimal(d2)).setScale(decimalPoint, BigDecimal.ROUND_HALF_UP).toDouble()
}