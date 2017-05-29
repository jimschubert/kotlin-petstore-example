package io.swagger.petstore.apis

import io.swagger.petstore.infrastructure.*
import io.swagger.petstore.models.Pet
import io.swagger.petstore.models.Status

class PetApi : ApiClient("http://petstore.swagger.io:80/v2") {

    @Suppress("UNCHECKED_CAST")
    fun findPetsByStatus(statuses: List<Status>): List<Pet> {
        val body: Any? = null
        val contentType = "application/json"
        val accept = "application/json"

        val config = RequestConfig(
                RequestMethod.GET,
                "/pet/findByStatus",
                query = mapOf("status" to statuses.joinToString(separator = ",")),
                headers = mapOf("Content-Type" to contentType, "Accept" to accept)
        )

        val response = request<List<Pet>>(config, body)

        when (response.responseType) {
            ResponseType.Success -> return (response as Success<*>).data as List<Pet>
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
        }
    }

    fun addPet(pet: Pet): Pet {
        val response: ApiResponse<Pet?> = request(
                RequestConfig(
                        RequestMethod.POST,
                        "/pet"
                ),
                pet)

        when(response.responseType)
        {
            ResponseType.Success -> return (response as Success<*>).data as Pet
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
        }
    }

    fun collectionDelimiter(collectionFormat: String) = when(collectionFormat) {
        "csv" -> ","
        "tsv" -> "\t"
        "pipes" -> "|"
        "ssv" -> " "
        else -> ""
    }
}