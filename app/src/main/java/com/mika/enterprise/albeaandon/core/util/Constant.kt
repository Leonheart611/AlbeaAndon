package com.mika.enterprise.albeaandon.core.util

object Constant {
    const val PREFERENCES_FILE_KEY = "ANDON-PREFERENCES"
    const val USER_TOKEN = "USER_TOKEN"
    const val REQUEST_CODE = 1
    const val NEW = "NEW"
    const val ONPROG = "ONPROG"
    const val ESKALASI = "ESKALASI"
    const val ASSIGNED = "ASSIGNED"
    const val SPV_PRODUCTION = "SPV Production"
    const val MECHANIC = "Mechanic"
    const val OPERATOR_BAHAN = "OperatorBahan"

    val userGroups = listOf("A", "B", "C", "D")
    val spvUserDeptFilter = listOf("Mechanic", "OperatorBahan").joinToString(",")


}