package shreddies

import java.time.temporal.ChronoUnit._
import java.time.temporal.{Temporal, TemporalAdjuster}
import java.time.temporal.TemporalAdjusters.next
import java.time.{DayOfWeek, LocalDate}

import org.scalatest.FunSuite

class ScheduleGenerationTest extends FunSuite {

  test("can generate a schedule of dates") {
    val expectedDate = LocalDate.of(2018, 12, 17)
    val strikeDate = LocalDate.of(2018, 6, 15) // Friday

    val addedTenor = strikeDate.plus(6, MONTHS) // Saturday
    val result = addedTenor.`with`(new WeekDayAdjuster)

    assert(result == expectedDate)
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