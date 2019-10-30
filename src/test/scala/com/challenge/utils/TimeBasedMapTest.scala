package com.challenge.utils

import java.time.{Duration, ZonedDateTime}
import java.time.temporal.ChronoUnit

import org.scalatest.FlatSpec

class TimeBasedMapTest extends FlatSpec {

  it should "be able to add element onto the map and get it back" in {
    val ts1 = ZonedDateTime.now()
    val ts2 = ts1.minusMinutes(1)
    val ts3 = ts1.minusMinutes(10)
    val item1 = "item1"
    val item2 = "item2"
    val item3 = "item3"
    val item4 = "item4"

    val map = TimeBasedMap.empty[String](ChronoUnit.MINUTES).add(ts1, item1).add(ts1, item2).add(ts2, item3).add(ts3, item4)
    assert(map.get(ts1) == List(item2, item1))
    assert(map.get(ts2) == List(item3))
    assert(map.get(ts3) == List(item4))

    assert(map.getInInterval(ts2, ts1) == List(item3, item2, item1))
    assert(map.getInInterval(ts1.minusMinutes(100), ts1.minusMinutes(90)) == List())
    assert(map.sliding(ts2, ts1, Duration.ofMinutes(2)).toList == List(List(item2, item1), List(item3, item2, item1)))

    //ensures interval chronological order
    assert(map.getInInterval(ts1, ts2) == List())

  }
}
