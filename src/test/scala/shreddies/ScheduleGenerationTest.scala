package shreddies

import java.time.temporal.ChronoUnit._
import java.time.temporal.{Temporal, TemporalAdjuster, TemporalUnit}
import java.time.{DayOfWeek, LocalDate}
import SmartLocalDate._

import org.scalatest.FunSuite

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

    assert(saturday.`with`(new WeekDayAdjuster) == monday)
  }

  test("can calculate dates") {
    assert(strikeDate.plusBusinessDays(10).`with`(new HolidayAdjuster) == effectiveDate)
    assert(strikeDate.plusMonths(tenorInMonths).`with`(new WeekDayAdjuster).`with`(new HolidayAdjuster) == finalValuation)
    assert(effectiveDate.plusMonths(tenorInMonths).`with`(new WeekDayAdjuster).`with`(new HolidayAdjuster) == maturityDate)
  }

  private def from(startDate: LocalDate)(amountToAdd: Long, unit: TemporalUnit): Stream[LocalDate] = {
    val nextDate = startDate.plus(amountToAdd, unit)
    nextDate.`with`(new WeekDayAdjuster).`with`(new HolidayAdjuster) #:: from(nextDate)(amountToAdd, unit)
  }

  private val numberOfPeriods = tenorInMonths / autocallFrequency

  test("can generate an observation schedule") {
    val schedule = from(strikeDate)(autocallFrequency, MONTHS).take(numberOfPeriods).toList
    assert(schedule == expectedObservations)
  }

  test("can generate a payment schedule") {
    val schedule = from(effectiveDate)(autocallFrequency, MONTHS).take(numberOfPeriods).toList
    assert(schedule == expectedPayments)
  }
}

object SmartLocalDate {

  implicit class SmartLocalDate(localDate: LocalDate) {
    def plusBusinessDays(days: Long): LocalDate = days match {
      case 0 => localDate
      case n => localDate.plusDays(1).`with`(new WeekDayAdjuster).plusBusinessDays(n - 1)
    }
  }
}

class WeekDayAdjuster extends TemporalAdjuster {
  override def adjustInto(input: Temporal): Temporal = {
    val date = LocalDate.from(input)

    date.getDayOfWeek match {
      case DayOfWeek.SATURDAY => date.plusDays(2)
      case DayOfWeek.SUNDAY => date.plusDays(1)
      case _ => date
    }
  }
}

class HolidayAdjuster extends TemporalAdjuster {
  private val holidays = List(
    LocalDate.of(2018, 12, 31),       // UK - New Years Eve
    LocalDate.of(2019, 1, 1),         // UK - New Years Day
    LocalDate.of(2019, 12, 17),       // NY - Pan American Aviation Day
    LocalDate.of(2020, 8, 31)         // UK - Bank holiday
  )

  override def adjustInto(input: Temporal): Temporal = {
    val date = LocalDate.from(input)
    if (holidays.contains(date)) adjustInto(date.plusBusinessDays(1)) else date
  }
}