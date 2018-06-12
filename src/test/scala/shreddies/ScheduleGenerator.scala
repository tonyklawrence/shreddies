package shreddies

import java.time.DayOfWeek.{SATURDAY, SUNDAY}
import java.time.LocalDate
import java.time.temporal.{Temporal, TemporalAdjuster, TemporalUnit}

object ScheduleGenerator {
  lazy val weekdays = new WeekDayAdjuster
  lazy val holidays = new HolidayAdjuster

  implicit def dateSmarts(date: LocalDate): SmartLocalDate = new SmartLocalDate(date)

  def every(amountToAdd: Long, unit: TemporalUnit)(startDate: LocalDate): Stream[LocalDate] = {
    val nextDate = startDate.plus(amountToAdd, unit)
    nextDate.`with`(weekdays).`with`(holidays) #:: every(amountToAdd, unit)(nextDate)
  }

  class SmartLocalDate(date: LocalDate) {
    def plusBusinessDays(days: Long): LocalDate = days match {
      case 0 => date
      case n => date.plusDays(1).`with`(weekdays).`with`(holidays).plusBusinessDays(n - 1)
    }
  }

  class WeekDayAdjuster extends TemporalAdjuster {
    override def adjustInto(input: Temporal): Temporal = {
      val date = LocalDate.from(input)

      date.getDayOfWeek match {
        case SATURDAY => date.plusDays(2)
        case SUNDAY   => date.plusDays(1)
        case _        => date
      }
    }
  }

  class HolidayAdjuster extends TemporalAdjuster {
    private val holidays = List(
      LocalDate.of(2018, 1, 15),        // NY - Martin Luther King's Birthday
      LocalDate.of(2018, 2, 19),        // NY - Washington's Birthday
      LocalDate.of(2018, 12, 31),       // UK - New Years Eve
      LocalDate.of(2019, 1, 1),         // UK - New Years Day
      LocalDate.of(2020, 8, 31),        // UK - August Bank holiday
      LocalDate.of(2023, 4, 10)         // UK - Easter Monday
    )

    override def adjustInto(input: Temporal): Temporal = {
      val date = LocalDate.from(input)
      if (holidays.contains(date)) adjustInto(date.plusBusinessDays(1)) else date
    }
  }
}