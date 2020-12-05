package xyz.purema.binusmyforum.data.local.converter

import androidx.room.TypeConverter
import xyz.purema.binusmyforum.domain.model.ClassType

class ClassTypeConverter {
    @TypeConverter
    fun fromClassType(classType: ClassType): String = classType.name

    @TypeConverter
    fun toClassType(value: String) = ClassType.valueOf(value)
}