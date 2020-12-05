package xyz.purema.binusmyforum.domain.model.course

import xyz.purema.binusmyforum.domain.model.ClassType

data class CourseClass(
    var id: String,
    var courseId: String,
    var classSection: String,
    var classType: ClassType
)