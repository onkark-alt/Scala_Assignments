package utils

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeMapper {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit val localDateTimeColumnType: BaseColumnType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, String](
      ldt => ldt.format(formatter),
      str => LocalDateTime.parse(str, formatter)
    )
}
