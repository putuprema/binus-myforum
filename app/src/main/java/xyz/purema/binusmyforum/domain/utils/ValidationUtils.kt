package xyz.purema.binusmyforum.domain.utils

import org.apache.commons.lang3.StringUtils
import xyz.purema.binusmyforum.domain.exception.EmptyFieldException

object ValidationUtils {
    fun throwIfEmptyField(fields: Array<Array<String?>>, defaultMessage: String? = "harus diisi") {
        for (field in fields) {
            val fieldName = field[0]
            val fieldValue = field[1]
            if (StringUtils.isEmpty(fieldValue)) {
                throw EmptyFieldException(String.format("%s %s", fieldName, defaultMessage))
            }
        }
    }
}