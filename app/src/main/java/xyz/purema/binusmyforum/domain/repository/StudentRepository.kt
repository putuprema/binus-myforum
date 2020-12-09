package xyz.purema.binusmyforum.domain.repository

import xyz.purema.binusmyforum.domain.model.student.Student

interface StudentRepository {
    suspend fun logout()
    suspend fun login(email: String, password: String): Student
    suspend fun refreshToken()
    suspend fun getProfile(): Student
    suspend fun syncStudentData(student: Student)
}