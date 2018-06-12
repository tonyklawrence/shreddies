package shreddies

import java.time.temporal.ChronoUnit._
import java.time.temporal.{Temporal, TemporalAdjuster}
import java.time.{DayOfWeek, LocalDate}

import org.scalatest.FunSuite

class ScheduleGenerationTest extends FunSuite {
  private val strikeDate = LocalDate.of(2018, 6, 15)
  private val effectiveDate = LocalDate.of(2018, 6, 29)

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
    val expectedDate = LocalDate.of(2018, 12, 17) // Monday after
    val strikeDate = LocalDate.of(2018, 6, 15) // Friday

    val addedTenor = strikeDate.plus(6, MONTHS) // Saturday
    val rollWeekend = addedTenor.`with`(new WeekDayAdjuster)

    assert(rollWeekend == expectedDate)
  }

  test("can roll another date") {
    val expectedDate = LocalDate.of(2019, 6, 17)
    val strikeDate = LocalDate.of(2018, 12, 17)

    val addedTenor = strikeDate.plus(6, MONTHS)
    val rollWeekend = addedTenor.`with`(new WeekDayAdjuster)

    assert(rollWeekend == expectedDate)
  }

  def from(startDate: LocalDate): Stream[LocalDate] = {
    val nextDate = startDate.plus(6, MONTHS)
    from(nextDate).#::(nextDate.`with`(new WeekDayAdjuster).`with`(new HolidayAdjuster))
  }

  test("can generate an observation schedule") {
    val schedule = from(strikeDate).take(6).toList
    assert(schedule == expectedObservations)
  }

  test("can generate a payment schedule") {
    val schedule = from(effectiveDate).take(6).toList
    assert(schedule == expectedPayments)
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
//    LocalDate.of(2019, 9, 2),         // NY - Labour Day
    LocalDate.of(2020, 8, 31)         // UK - Bank holiday
  )

  override def adjustInto(input: Temporal): Temporal = {
    val date = LocalDate.from(input)
    if (holidays.contains(date)) adjustInto(date.plusDays(1)) else date
  }
}