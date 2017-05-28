package io.swagger.petstore.apis

import io.swagger.petstore.infrastructure.*
import io.swagger.petstore.models.Pet
import io.swagger.petstore.models.Status

class PetApi : ApiClient("http://petstore.swagger.io:80/v2") {

    @Suppress("UNCHECKED_CAST")
    fun findPetsByStatus(statuses: List<Status>): List<Pet> {
        val response = json<List<Pet>>(
                RequestConfig(
                        RequestMethod.GET,
                        "/pet/findByStatus",
                        query = mapOf("status" to statuses.joinToString(separator = ","))
                )
        )

        when (response.responseType) {
            ResponseType.Success -> return (response as Success<*>).data as List<Pet>
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
        }
    }

    fun addPet(pet: Pet): Pet {
        val response: ApiResponse<Pet?> = jsonWithBody(
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
}