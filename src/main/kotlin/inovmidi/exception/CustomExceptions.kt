package inovmidi.exception


class ResourceNotFoundException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class ArticleGenerationException(message: String) : RuntimeException(message)
