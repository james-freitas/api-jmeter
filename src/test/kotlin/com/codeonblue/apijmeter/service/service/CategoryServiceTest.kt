package com.codeonblue.apijmeter.service.service

import com.codeonblue.apijmeter.exception.ResourceAlreadyExistsException
import com.codeonblue.apijmeter.exception.ResourceNotFoundException
import com.codeonblue.apijmeter.model.Category
import com.codeonblue.apijmeter.repository.CategoryEntity
import com.codeonblue.apijmeter.repository.CategoryRepository
import com.codeonblue.apijmeter.service.CategoryService
import de.huxhorn.sulky.ulid.ULID
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class CategoryServiceTest {

    private val categoryRepository = mockk<CategoryRepository>()
    private val service = CategoryService(categoryRepository)

    @Nested
    inner class FindAll {

        @Test
        fun `should find all categories`() {

            // given
            val categoryEntityList = listOf(
                CategoryEntity(ULID().nextULID(), "Jewelry"),
                CategoryEntity(ULID().nextULID(), "Fitness clothing"),
                CategoryEntity(ULID().nextULID(), "Cosmetics")
            )

            // when
            every { categoryRepository.findAll() } returns categoryEntityList

            val categories = service.findAll()

            assertEquals(3, categories.size)
            assertThat(categories[1].name).isEqualTo("Fitness clothing")
        }

        @Test
        fun `should return null when there are no categories`() {

            // when
            every { categoryRepository.findAll() } returns emptyList()

            val categories = service.findAll()

            assertThat(categories.size).isEqualTo(0)
        }
    }

    @Nested
    inner class Create {

        @Test
        fun `should create a category successfully when it does not exist`() {

            // given
            val categoryEntity = CategoryEntity(ULID().nextULID(), "Cosmetics")

            every { categoryRepository.findByName("COSMETICS") } returns null

            val categorySlot = slot<CategoryEntity>()
            every { categoryRepository.save(capture(categorySlot)) } returns categoryEntity

            val categoryCreated = service.create(Category(name = "Cosmetics"))

            assertThat(categoryCreated.name).isEqualTo(categoryEntity.name)
        }

        @Test
        fun `should fail to create a category when it does exist`() {

            // given
            val categoryEntity = CategoryEntity(ULID().nextULID(), "Cosmetics")

            // when
            every { categoryRepository.findByName("COSMETICS") } returns categoryEntity

            assertThrows<ResourceAlreadyExistsException> {
                service.create(Category(name = "Cosmetics"))
            }
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should delete a category successfully when it exists`() {

            // given
            val categoryId = ULID().nextULID()
            val categoryEntity = CategoryEntity(categoryId, "Cosmetics")

            // when
            every { categoryRepository.findById(categoryId) } returns Optional.of(categoryEntity)
            every { categoryRepository.delete(categoryEntity) } just Runs

            assertDoesNotThrow { service.delete(categoryId) }
        }

        @Test
        fun `should throw an exception when category is not found to delete`() {

            // given
            val categoryId = ULID().nextULID()

            // when
            every { categoryRepository.findById(categoryId) } returns Optional.empty()

            assertThrows<ResourceNotFoundException> {
                service.delete(categoryId)
            }
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `should update a category successfully`() {

            val categoryId = ULID().nextULID()

            // given
            val categoryToUpdate = Category(
                id = categoryId,
                name = "Updated category name"
            )

            val categoryExistent = Optional.of(
                CategoryEntity(
                    id = categoryId,
                    name = "Existent category name"
                )
            )

            val categoryEntityUpdated = CategoryEntity(
                id = categoryId,
                name = "Updated category name"
            )

            every { categoryRepository.findById(categoryToUpdate.id!!) } returns categoryExistent
            val categoryEntitySlot = slot<CategoryEntity>()
            every { categoryRepository.save(capture(categoryEntitySlot)) } returns categoryEntityUpdated

            assertDoesNotThrow {
                service.update(categoryToUpdate)
            }
        }

        @Test
        fun `should fail to update category when it does not exist`() {

            // given
            val categoryToUpdate = Category(
                id = ULID().nextULID(),
                name = "Updated category name"
            )

            every { categoryRepository.findById(categoryToUpdate.id!!) } returns Optional.empty()
            assertThrows<ResourceNotFoundException> {
                service.update(categoryToUpdate)
            }
        }
    }
}