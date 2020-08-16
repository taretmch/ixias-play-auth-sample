package mvc.filter

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.api.http.EnabledFilters
import play.filters.gzip.GzipFilter

class Filters @Inject() (
  defaultFilters: EnabledFilters,
  error:          ErrorFilter
) extends DefaultHttpFilters(defaultFilters.filters :+ error: _*)

