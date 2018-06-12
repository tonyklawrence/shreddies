package shreddies

import java.time.temporal.{Temporal, TemporalAdjuster}
import java.time.{DayOfWeek, LocalDate}

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
