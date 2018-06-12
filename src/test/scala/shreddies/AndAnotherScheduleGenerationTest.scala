package shreddies

import java.time.LocalDate
import java.time.temporal.ChronoUnit._

import org.scalatest.FunSuite
import shreddies.ScheduleGenerator._

class AndAnotherScheduleGenerationTest extends FunSuite {
  private val tradeDate = LocalDate.of(2018, 1, 9) // strike / pricing
  private val effectiveDate = LocalDate.of(2018, 1, 24) // issue date / settlement date
  private val finalValuation = LocalDate.of(2024, 1, 9)
  private val maturityDate = LocalDate.of(2024, 1, 24)

  private val tenorInMonths = 72
  private val autocallFrequency = 3

  private val expectedObservations = List(
    LocalDate.of(2018,4,9),
    LocalDate.of(2018,7,9),
    LocalDate.of(2018,10,9),

    LocalDate.of(2019,1,9),
    LocalDate.of(2019,4,9),
    LocalDate.of(2019,7,9),
    LocalDate.of(2019,10,9),

    LocalDate.of(2020,1,9),
    LocalDate.of(2020,4,9),
    LocalDate.of(2020,7,9),
    LocalDate.of(2020,10,9),

    LocalDate.of(2021,1,11),
    LocalDate.of(2021,4,9),
    LocalDate.of(2021,7,9),
    LocalDate.of(2021,10,11),

    LocalDate.of(2022,1,10),
    LocalDate.of(2022,4,11),
    LocalDate.of(2022,7,11),
    LocalDate.of(2022,10,10),

    LocalDate.of(2023,1,9),
    LocalDate.of(2023,4,11),
    LocalDate.of(2023,7,10),
    LocalDate.of(2023,10,9),

    LocalDate.of(2024,1,9)
  )

  private val expectedPayments = List(
    LocalDate.of(2018,4,24),
    LocalDate.of(2018,7,24),
    LocalDate.of(2018,10,24),

    LocalDate.of(2019,1,24),
    LocalDate.of(2019,4,24),
    LocalDate.of(2019,7,24),
    LocalDate.of(2019,10,24),

    LocalDate.of(2020,1,24),
    LocalDate.of(2020,4,24),
    LocalDate.of(2020,7,24),
    LocalDate.of(2020,10,26),

    LocalDate.of(2021,1,25),
    LocalDate.of(2021,4,26),
    LocalDate.of(2021,7,26),
    LocalDate.of(2021,10,25),

    LocalDate.of(2022,1,24),
    LocalDate.of(2022,4,25),
    LocalDate.of(2022,7,25),
    LocalDate.of(2022,10,24),

    LocalDate.of(2023,1,24),
    LocalDate.of(2023,4,24),
    LocalDate.of(2023,7,24),
    LocalDate.of(2023,10,24),

    LocalDate.of(2024,1,24)
  )

  test("can calculate dates") {
    assert(tradeDate.plusBusinessDays(10).`with`(holidays) == effectiveDate)
    assert(tradeDate.plusMonths(tenorInMonths).`with`(weekdays).`with`(holidays) == finalValuation)
    assert(effectiveDate.plusMonths(tenorInMonths).`with`(weekdays).`with`(holidays) == maturityDate)
  }

  private val numberOfPeriods = tenorInMonths / autocallFrequency
  private val everyThreeMonthsFrom = every(autocallFrequency, MONTHS)_

  test("can generate an observation schedule") {
    val schedule = everyThreeMonthsFrom(tradeDate).take(numberOfPeriods).toList
    assert(schedule == expectedObservations)
  }

  test("can generate a payment schedule") {
    val schedule = everyThreeMonthsFrom(effectiveDate).take(numberOfPeriods).toList
    assert(schedule == expectedPayments)
  }
}