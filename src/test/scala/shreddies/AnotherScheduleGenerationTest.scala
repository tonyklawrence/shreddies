package shreddies

import java.time.LocalDate
import java.time.temporal.ChronoUnit._

import org.scalatest.FunSuite
import ScheduleGenerator._

class AnotherScheduleGenerationTest extends FunSuite {
  private val tradeDate = LocalDate.of(2018, 2, 15) // strike
  private val effectiveDate = LocalDate.of(2018, 3, 2) // issue date / settlement date
  private val finalValuation = LocalDate.of(2021, 2, 15)
  private val maturityDate = LocalDate.of(2021, 3, 1)

  private val tenorInMonths = 36
  private val autocallFrequency = 6

  private val expectedObservations = List(
    LocalDate.of(2018,8,15),
    LocalDate.of(2019,2,15),
    LocalDate.of(2019,8,15),
    LocalDate.of(2020,2,17),
    LocalDate.of(2020,8,17),
    LocalDate.of(2021,2,15)
  )

  private val expectedPayments = List(
    LocalDate.of(2018,9,3),
    LocalDate.of(2019,3,1),
    LocalDate.of(2019,9,2),
    LocalDate.of(2020,3,2),
    LocalDate.of(2020,9,1),
    LocalDate.of(2021,3,1)
  )

  // Why do we need the unadjusted date ???
  private val unadjustedEffectiveDate = tradeDate.plusDays(14)

  test("can calculate dates") {
    assert(tradeDate.plusBusinessDays(10).`with`(holidays) == effectiveDate)
    assert(tradeDate.plusMonths(tenorInMonths).`with`(weekdays).`with`(holidays) == finalValuation)
    assert(unadjustedEffectiveDate.plusMonths(tenorInMonths).`with`(weekdays).`with`(holidays) == maturityDate)
  }

  private val numberOfPeriods = tenorInMonths / autocallFrequency
  private val everySixMonthsFrom = every(autocallFrequency, MONTHS)_

  test("can generate an observation schedule") {
    val schedule = everySixMonthsFrom(tradeDate).take(numberOfPeriods).toList
    assert(schedule == expectedObservations)
  }

  test("can generate a payment schedule") {
    val schedule = everySixMonthsFrom(unadjustedEffectiveDate).take(numberOfPeriods).toList
    assert(schedule == expectedPayments)
  }
}