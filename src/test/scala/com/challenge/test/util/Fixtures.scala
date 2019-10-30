package com.challenge.test.util

import java.time.ZonedDateTime

import com.challenge.models.{Account, AccountInformation, Transaction}

object Fixtures {
  object account {
    val activeCardZeroLimit = Account(activeCard = true, availableLimit = 0)
    val activeCard1KLimit = Account(activeCard = true, availableLimit = 1000)
    val activeCardMaxLimit = Account(activeCard = true, availableLimit = Int.MaxValue)
    val inactiveCard1KLimit = Account(activeCard = false, availableLimit = 1000)
    val inactiveCardZeroLimit = Account(activeCard = false, availableLimit = 0)
  }

  object state {
    def from(a: Account): Option[AccountInformation] = Some(AccountInformation(a))
    val empty: Option[AccountInformation] = None
    val activeCard1KLimit = Some(AccountInformation(account.activeCard1KLimit))
    val activeCardMaxLimit = Some(AccountInformation(account.activeCardMaxLimit))
    val inactiveCard1KLimit = Some(AccountInformation(account.inactiveCard1KLimit))
    val activeCardZeroLimit = Some(AccountInformation(account.activeCardZeroLimit))
    val inactiveCardZeroLimit = Some(AccountInformation(account.inactiveCardZeroLimit))
  }

  object transaction {
    val time1 = ZonedDateTime.parse("2019-02-13T10:00:00.000Z")
    val merchant1 = "merchant1"
    val merchant2 = "merchant2"
    val merchant3 = "merchant3"
    val merchant4 = "merchant4"
    val merchantOneP100 = Transaction(merchant1, 100, time1)
    val merchantOneM100 = Transaction(merchant1, -100, time1)
    val merchantTwoM100 = Transaction(merchant2, -100, time1)
    val merchantThreeM100 = Transaction(merchant3, -100, time1)
    val merchantFourM100 = Transaction(merchant4, -100, time1)
  }
}
