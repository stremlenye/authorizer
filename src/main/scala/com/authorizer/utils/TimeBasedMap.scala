package com.authorizer.utils

import java.time.{Duration, ZonedDateTime}
import java.time.temporal.ChronoUnit

final class TimeBasedMap[A] private (granularity: ChronoUnit, internal: Map[ZonedDateTime, List[A]]) {

  def add(key: ZonedDateTime, a: A): TimeBasedMap[A] = {
    val k = truncateToGranularity(key)
    new TimeBasedMap(granularity, internal.updated(k, a :: internal.getOrElse(k, Nil)))
  }

  def get(key: ZonedDateTime): List[A] =
    internal.getOrElse(truncateToGranularity(key), Nil)

  def getInInterval(from: ZonedDateTime, to: ZonedDateTime): List[A] = chronologicalIteration(from, to).foldLeft(List.empty[A])(_ ++ get(_))

  def sliding(from: ZonedDateTime, to: ZonedDateTime, interval: Duration): Stream[List[A]] =
    chronologicalIteration(from, to).foldLeft(Stream.empty[List[A]])((a, f) => getInInterval(f, f.plus(interval)) #:: a)

  private def chronologicalIteration(from: ZonedDateTime, to: ZonedDateTime): Stream[ZonedDateTime] = {
    val t = truncateToGranularity(to)
    Stream.iterate(truncateToGranularity(from))(_.plus(1L, granularity)).takeWhile(_.compareTo(t) <= 0)
  }

  private def truncateToGranularity(d: ZonedDateTime): ZonedDateTime =
    d.truncatedTo(granularity)
}

object TimeBasedMap {
  def empty[A](granularity: ChronoUnit): TimeBasedMap[A] = new TimeBasedMap(granularity, Map.empty)
}
