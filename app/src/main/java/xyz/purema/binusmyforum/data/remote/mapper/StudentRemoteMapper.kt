package xyz.purema.binusmyforum.data.remote.mapper

import xyz.purema.binusmyforum.data.remote.model.response.BinusStudentProfileResponse
import xyz.purema.binusmyforum.domain.EntityMapper
import xyz.purema.binusmyforum.domain.model.student.Student
import javax.inject.Inject

class StudentRemoteMapper
@Inject constructor() : EntityMapper<BinusStudentProfileResponse, Student> {
    override fun mapFromEntity(entity: BinusStudentProfileResponse): Student {
        return Student(
            nim = entity.nim,
            name = entity.name,
            binusianId = entity.binusianID,
            email = entity.emailAddr,
            acadCareer = entity.acadCareer,
            institution = entity.institution,
            studentType = entity.studentType,
            strm = ""
        )
    }

    override fun mapToEntity(domainModel: Student): BinusStudentProfileResponse {
        return BinusStudentProfileResponse(
            nim = domainModel.nim,
            name = domainModel.name,
            binusianID = domainModel.binusianId,
            emailAddr = domainModel.email,
            acadCareer = domainModel.acadCareer,
            institution = domainModel.institution,
            studentType = domainModel.studentType
        )
    }

    override fun mapFromEntities(entities: List<BinusStudentProfileResponse>): List<Student> {
        return entities.map { mapFromEntity(it) }
    }
}