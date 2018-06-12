# Schedule Generation

### Product fields

* Trade Date
* Issue delay (in business days)
* Currency
* Underlyings
  * Currency
  * Trading region
* Tenor (in months)
* Autocall frequency (in months)
* Coupon frequency (in months)

###  Date terminology

* Effective date 
  * or Issue date for Notes - date the note is issued and paid by client
  * or Settlement date if not Notes - date the premium is paid by client
* Trade date
  * or Strike date or T - date on which spot prices are determined)
* Start date
  * functional date used not in settlement but in aligning UOB style valuation dates or regular payment dates
* Payment dates
  * fixed coupon / rebate coupon / repayment of notional
* Valuation date
  * or Observation date - date KI/KO are observed

### Schedules required

* Autocall schedule
  * Observation date
  * Payment date
* Coupon schedule
  * Observation date
  * Payment date
  
### Dates required

* Effective date
* Maturity date
* Trade date
* Valudation date (final?)

### Example

* Trade date = 15 June 2018
* Tenor = 36 months
* Autocall frequency = 6 months
* Coupon frequency = 6 months
* Non-call periods = 0
* Memory coupon = false

#### Output

Business Day convention = Following
Calendars for payments = NYC, LDN

Strike date     = 15 June 2018
Trade date      = 29 June 2018 (+ 10 business days - issue delay?)
Issue date      = 29 June 2018 (same)
Settlement date = 29 June 2018 (same)
Final Valuation = 15 June 2021 (+ 3 years)
Maturity date   = 29 June 2021 (Trade/Issue/Settlement + 3 years)

Autocall schedule = 
    17 Dec 2018     02 Jan 2019
    17 Jun 2019     01 Jul 2019
    16 Dec 2019     30 Dec 2019
    15 Jun 2020     29 Jun 2020
    15 Dec 2020     29 Dec 2020
    15 Jun 2021     29 Jun 2021
    
Coupon schedule =
    17 Dec 2018     02 Jan 2019
    17 Jun 2019     01 Jul 2019
    16 Dec 2019     30 Dec 2019
    15 Jun 2020     29 Jun 2020
    15 Dec 2020     29 Dec 2020
    15 Jun 2021     29 Jun 2021
