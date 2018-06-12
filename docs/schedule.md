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