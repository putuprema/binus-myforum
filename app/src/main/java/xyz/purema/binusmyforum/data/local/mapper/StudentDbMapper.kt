package xyz.purema.binusmyforum.data.local.mapper

import xyz.purema.binusmyforum.data.local.model.StudentDb
import xyz.purema.binusmyforum.domain.EntityMapper
import xyz.purema.binusmyforum.domain.model.student.Student
import javax.inject.Inject

class StudentDbMapper
@Inject constructor() : EntityMapper<StudentDb, Student> {
    override fun mapFromEntity(entity: StudentDb): Student {
        return Student(
            nim = entity.nim,
            name = entity.name,
            binusianId = entity.binusianId,
            email = entity.binusianId,
            acadCareer = entity.acadCareer,
            institution = entity.institution,
            studentType = entity.studentType,
            strm = entity.strm
        )
    }

    override fun mapToEntity(domainModel: Student): StudentDb {
        return StudentDb(
            nim = domainModel.nim,
            name = domainModel.name,
            binusianId = domainModel.binusianId,
            email = domainModel.email,
            acadCareer = domainModel.acadCareer,
            institution = domainModel.institution,
            studentType = domainModel.studentType,
            strm = domainModel.strm
        )
    }

    override fun mapFromEntities(entities: List<StudentDb>): List<Student> {
        return entities.map { mapFromEntity(it) }
    }
}