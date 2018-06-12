package shreddies

import java.time.LocalDate
import java.time.temporal.ChronoUnit._

import org.scalatest.FunSuite
import shreddies.ScheduleGenerator._

class ScheduleGenerationTest extends FunSuite {
  private val strikeDate = LocalDate.of(2018, 6, 15)
  private val effectiveDate = LocalDate.of(2018, 6, 29)
  private val finalValuation = LocalDate.of(2021, 6, 15)
  private val maturityDate = LocalDate.of(2021, 6, 29)

  private val tenorInMonths = 36
  private val autocallFrequency = 6

  private val expectedObservations = List(
    LocalDate.of(2018,12,17),
    LocalDate.of(2019,6,17),
    LocalDate.of(2019,12,16),
    LocalDate.of(2020,6,15),
    LocalDate.of(2020,12,15),
    LocalDate.of(2021,6,15)
  )

  private val expectedPayments = List(
    LocalDate.of(2019,1,2),
    LocalDate.of(2019,7,1),
    LocalDate.of(2019,12,30),
    LocalDate.of(2020,6,29),
    LocalDate.of(2020,12,29),
    LocalDate.of(2021,6,29)
  )

  test("can roll a date") {
    val saturday = LocalDate.of(2018, 6, 16)
    val monday = LocalDate.of(2018, 6, 18)

    assert(saturday.`with`(weekdays) == monday)
  }

  test("can calculate dates") {
    assert(strikeDate.plusBusinessDays(10).`with`(holidays) == effectiveDate)
    assert(strikeDate.plusMonths(tenorInMonths).`with`(weekdays).`with`(holidays) == finalValuation)
    assert(effectiveDate.plusMonths(tenorInMonths).`with`(weekdays).`with`(holidays) == maturityDate)
  }

  private val numberOfPeriods = tenorInMonths / autocallFrequency
  private val everySixMonthsFrom = every(autocallFrequency, MONTHS)_

  test("can generate an observation schedule") {
    val schedule = everySixMonthsFrom(strikeDate).take(numberOfPeriods).toList
    assert(schedule == expectedObservations)
  }

  test("can generate a payment schedule") {
    val schedule = everySixMonthsFrom(effectiveDate).take(numberOfPeriods).toList
    assert(schedule == expectedPayments)
  }
}