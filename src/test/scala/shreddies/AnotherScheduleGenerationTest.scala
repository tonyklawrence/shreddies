package shreddies

import java.time.LocalDate
import java.time.temporal.ChronoUnit._
import java.time.temporal.TemporalUnit

import org.scalatest.FunSuite
import shreddies.Implicits._

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
    assert(tradeDate.plusBusinessDays(10).`with`(new HolidayAdjuster) == effectiveDate)
    assert(tradeDate.plusMonths(tenorInMonths).`with`(new WeekDayAdjuster).`with`(new HolidayAdjuster) == finalValuation)
    assert(unadjustedEffectiveDate.plusMonths(tenorInMonths).`with`(new WeekDayAdjuster).`with`(new HolidayAdjuster) == maturityDate)
  }

  private def from(startDate: LocalDate)(amountToAdd: Long, unit: TemporalUnit): Stream[LocalDate] = {
    val nextDate = startDate.plus(amountToAdd, unit)
    nextDate.`with`(new WeekDayAdjuster).`with`(new HolidayAdjuster) #:: from(nextDate)(amountToAdd, unit)
  }

  private val numberOfPeriods = tenorInMonths / autocallFrequency

  test("can generate an observation schedule") {
    val schedule = from(tradeDate)(autocallFrequency, MONTHS).take(numberOfPeriods).toList
    assert(schedule == expectedObservations)
  }

  test("can generate a payment schedule") {
    val schedule = from(unadjustedEffectiveDate)(autocallFrequency, MONTHS).take(numberOfPeriods).toList
    assert(schedule == expectedPayments)
  }
}