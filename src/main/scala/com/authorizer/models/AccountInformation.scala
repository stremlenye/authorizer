package com.authorizer.models

import java.time.temporal.ChronoUnit

import com.authorizer.utils.TimeBasedMap

final case class AccountInformation(account: Account, transactions: TimeBasedMap[Transaction]) {

  def submitTransaction(t: Transaction): AccountInformation =
    this.copy(
      transactions = transactions.add(t.time, t),
      account = account.copy(
        availableLimit = account.availableLimit + t.amount
      )
    )
}

object AccountInformation {
  def apply(account: Account): AccountInformation =
    new AccountInformation(account, TimeBasedMap.empty(ChronoUnit.MINUTES))
}
