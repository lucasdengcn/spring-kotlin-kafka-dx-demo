package com.example.demo.order.integration.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

data class StockSpotInfo (
    val symbol : String,
    val code : String,
    val name : String,
    val trade : String,

    @JsonProperty("pricechange")
    val priceChange : Number,

    @JsonProperty("changepercent")
    val changePercent : Number,

    val buy : Int,
    val sell : Int,
    val settlement : String,
    val open : Number,
    val high : Number,
    val low : Number,
    val volume : Int,
    val amount : Long,

    @JsonProperty("ticktime")
    val tickTime : String,

    val per : Double,
    val pb : Double,
    val mktcap : Double,
    val nmc : Double,

    @JsonProperty("turnoverratio")
    val turnOverRatio : Double
)
