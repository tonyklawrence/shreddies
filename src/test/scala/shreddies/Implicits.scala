package shreddies

import java.time.LocalDate

object Implicits {
  implicit def dateSmarts(date: LocalDate): SmartLocalDate = new SmartLocalDate(date)

  class SmartLocalDate(localDate: LocalDate) {
    def plusBusinessDays(days: Long): LocalDate = days match {
      case 0 => localDate
      case n => localDate.plusDays(1).
        `with`(new WeekDayAdjuster).
        `with`(new HolidayAdjuster).
        plusBusinessDays(n - 1)
    }
  }
}
