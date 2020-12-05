package xyz.purema.binusmyforum.data.local.mapper

import xyz.purema.binusmyforum.data.local.model.CourseClassDb
import xyz.purema.binusmyforum.domain.EntityMapper
import xyz.purema.binusmyforum.domain.model.course.CourseClass
import javax.inject.Inject

class CourseClassDbMapper
@Inject constructor() : EntityMapper<CourseClassDb, CourseClass> {
    override fun mapFromEntity(entity: CourseClassDb): CourseClass {
        return CourseClass(
            id = entity.id,
            courseId = entity.courseId,
            classSection = entity.classSection,
            classType = entity.classType
        )
    }

    override fun mapToEntity(domainModel: CourseClass): CourseClassDb {
        return CourseClassDb(
            id = domainModel.id,
            courseId = domainModel.courseId,
            classSection = domainModel.classSection,
            classType = domainModel.classType
        )
    }

    override fun mapFromEntities(entities: List<CourseClassDb>): List<CourseClass> {
        return entities.map { mapFromEntity(it) }
    }
}