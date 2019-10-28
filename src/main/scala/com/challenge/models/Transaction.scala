package com.challenge.models

import java.time.ZonedDateTime

final case class Transaction(merchant: String, amount: Int, time: ZonedDateTime)
