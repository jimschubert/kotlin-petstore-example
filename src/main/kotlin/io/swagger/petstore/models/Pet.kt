package io.swagger.petstore.models

data class Pet(
        val id: Long,
        val category: Category,
        val name: String = "doggie",
        val photoUrls: List<String> = listOf(),
        val tags: List<Tag> = listOf(),
        val status: Status = Status.pending
)