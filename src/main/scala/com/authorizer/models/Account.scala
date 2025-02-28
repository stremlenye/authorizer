package com.authorizer.models

import cats.Eq
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import io.circe.{Decoder, Encoder}

final case class Account(activeCard: Boolean, availableLimit: Int)

object Account {

  implicit val customConfig: Configuration = Configuration.default.withKebabCaseMemberNames

  implicit val encoder: Encoder[Account] = deriveEncoder
  implicit val decoder: Decoder[Account] = deriveDecoder
  implicit val eq: Eq[Account] = Eq.fromUniversalEquals
}
