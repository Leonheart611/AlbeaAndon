package com.mika.enterprise.albeaandon.core.util

object Constant {
    const val PREFERENCES_FILE_KEY = "ANDON-PREFERENCES"
    const val USER_TOKEN = "USER_TOKEN"
    const val USER_NIK = "USER_NIK"
    const val KEY_LANGUAGE = "KEY_LANGUAGE"
    const val REQUEST_CODE = 1
    const val NEW = "NEW"
    const val ONPROG = "ONPROG"
    const val ESKALASI = "ESKALASI"
    const val ASSIGNED = "ASSIGNED"
    const val ALL = "ALL"
    const val SPV_PRODUCTION = "SPV Production"
    const val MECHANIC = "Mechanic"
    const val OPERATOR_BAHAN = "OperatorBahan"
    const val NOTIFICATION_CHANEL_ID = "NOTIFICATION_CHANEL_ID"
    const val UNIQUE_WORK_NAME = "UNIQUE_WORK_NAME"
    const val FRAGMENT_KEY_SKIP_SPLASH = "FRAGMENT_KEY_SKIP_SPLASH"

    val userGroups = listOf("A", "B", "C", "D")
    val spvUserDeptFilter = listOf("Mechanic", "OperatorBahan").joinToString(",")

    const val IS_INTERNAL_TEST = false

    const val PROD_URL_ID = "http://dmksrv02:443/andon/"
    const val PROD_URL_ZH = "http://192.168.0.10:80/andon/"
}