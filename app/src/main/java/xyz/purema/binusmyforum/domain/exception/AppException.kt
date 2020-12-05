package xyz.purema.binusmyforum.domain.exception

class AppException(message: String, val code: String) : RuntimeException(message)