package org.ligi.passandroid.ui.edit

fun getRandomITF() = (0..11).map { (Math.random() * 9).toInt() }.joinToString(separator = "")