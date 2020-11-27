package com.hkm.flixhub.utils

object Formatter {
    fun dateFormatter(date: String): String {
        val dateArray = date.split("-").toTypedArray()

        return "${getDay(dateArray[2])} ${getMonth(dateArray[1])} ${dateArray[0]}"
    }

    private fun getDay(day: String): String {
        var returnString = ""
        if (day.toInt() < 10) {
            if (day.length > 1)
                returnString = day.substring(1)
        } else
            returnString = day

        return when (returnString) {
            "11", "22" -> {
                "${returnString}th"
            }
            else -> {
                when (returnString[returnString.length - 1]) {
                    '1' -> {
                        "${returnString}st"
                    }
                    '2' -> {
                        "${returnString}nd"
                    }
                    '3' -> {
                        "${returnString}rd"
                    }
                    else -> {
                        "${returnString}th"
                    }
                }
            }
        }
    }

    private fun getMonth(month: String): String? {
        return when (month) {
            "1", "01" -> {
                "January"
            }
            "2", "02" -> {
                "February"
            }
            "3", "03" -> {
                "March"
            }
            "4", "04" -> {
                "April"
            }
            "5", "05" -> {
                "May"
            }
            "6", "06" -> {
                "June"
            }
            "7", "07" -> {
                "July"
            }
            "8", "08" -> {
                "August"
            }
            "9", "09" -> {
                "September"
            }
            "10" -> {
                "October"
            }
            "11" -> {
                "November"
            }
            "12" -> {
                "December"
            }
            else -> null
        }
    }
}