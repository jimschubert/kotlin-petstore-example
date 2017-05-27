package io.swagger.petstore.functional

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.should
import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.specs.ShouldSpec
import io.swagger.petstore.apis.PetApi
import io.swagger.petstore.models.Category
import io.swagger.petstore.models.Pet
import io.swagger.petstore.models.Status
import io.swagger.petstore.models.Tag

class EvaluateTest : ShouldSpec() {
    init {
        should("query against pet statuses") {
            val api = PetApi()
            val results = api.findPetsByStatus(listOf(Status.available, Status.pending))

            results.size should beGreaterThan(1)
        }

        should("post data (new pet)") {
            val api = PetApi()
            val pet = Pet(
                    id = 0,
                    name = "kotlin client test",
                    category = Category(0, "string"),
                    tags = listOf(Tag(0, "string"))
            )
            val result = api.addPet(pet)

            result.name shouldBe(pet.name)
            result.category shouldBe(pet.category)
            result.tags shouldBe(pet.tags)
        }
    }
}