package com.example.gohyongwonton.ui.screen

fun formatRupiah(amount: Int): String =
    "%,d".format(amount).replace(",", ".")