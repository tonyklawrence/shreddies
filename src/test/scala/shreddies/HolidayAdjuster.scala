package shreddies

import java.time.LocalDate
import java.time.temporal.{Temporal, TemporalAdjuster}
import Implicits._

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
