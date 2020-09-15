package org.muellners.finscale.deposit.web.rest

import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.muellners.finscale.deposit.DepositAccountManagementApp
import org.muellners.finscale.deposit.config.SecurityBeanOverrideConfiguration
import org.muellners.finscale.deposit.domain.AvailableTransactionType
import org.muellners.finscale.deposit.repository.AvailableTransactionTypeRepository
import org.muellners.finscale.deposit.web.rest.errors.ExceptionTranslator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [AvailableTransactionTypeResource] REST controller.
 *
 * @see AvailableTransactionTypeResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class AvailableTransactionTypeResourceIT {

    @Autowired
    private lateinit var availableTransactionTypeRepository: AvailableTransactionTypeRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restAvailableTransactionTypeMockMvc: MockMvc

    private lateinit var availableTransactionType: AvailableTransactionType

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val availableTransactionTypeResource = AvailableTransactionTypeResource(availableTransactionTypeRepository)
         this.restAvailableTransactionTypeMockMvc = MockMvcBuilders.standaloneSetup(availableTransactionTypeResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        availableTransactionType = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createAvailableTransactionType() {
        val databaseSizeBeforeCreate = availableTransactionTypeRepository.findAll().size

        // Create the AvailableTransactionType
        restAvailableTransactionTypeMockMvc.perform(
            post("/api/available-transaction-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(availableTransactionType))
        ).andExpect(status().isCreated)

        // Validate the AvailableTransactionType in the database
        val availableTransactionTypeList = availableTransactionTypeRepository.findAll()
        assertThat(availableTransactionTypeList).hasSize(databaseSizeBeforeCreate + 1)
        val testAvailableTransactionType = availableTransactionTypeList[availableTransactionTypeList.size - 1]
        assertThat(testAvailableTransactionType.transactionType).isEqualTo(DEFAULT_TRANSACTION_TYPE)
    }

    @Test
    @Transactional
    fun createAvailableTransactionTypeWithExistingId() {
        val databaseSizeBeforeCreate = availableTransactionTypeRepository.findAll().size

        // Create the AvailableTransactionType with an existing ID
        availableTransactionType.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restAvailableTransactionTypeMockMvc.perform(
            post("/api/available-transaction-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(availableTransactionType))
        ).andExpect(status().isBadRequest)

        // Validate the AvailableTransactionType in the database
        val availableTransactionTypeList = availableTransactionTypeRepository.findAll()
        assertThat(availableTransactionTypeList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkTransactionTypeIsRequired() {
        val databaseSizeBeforeTest = availableTransactionTypeRepository.findAll().size
        // set the field null
        availableTransactionType.transactionType = null

        // Create the AvailableTransactionType, which fails.

        restAvailableTransactionTypeMockMvc.perform(
            post("/api/available-transaction-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(availableTransactionType))
        ).andExpect(status().isBadRequest)

        val availableTransactionTypeList = availableTransactionTypeRepository.findAll()
        assertThat(availableTransactionTypeList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllAvailableTransactionTypes() {
        // Initialize the database
        availableTransactionTypeRepository.saveAndFlush(availableTransactionType)

        // Get all the availableTransactionTypeList
        restAvailableTransactionTypeMockMvc.perform(get("/api/available-transaction-types?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(availableTransactionType.id?.toInt())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAvailableTransactionType() {
        // Initialize the database
        availableTransactionTypeRepository.saveAndFlush(availableTransactionType)

        val id = availableTransactionType.id
        assertNotNull(id)

        // Get the availableTransactionType
        restAvailableTransactionTypeMockMvc.perform(get("/api/available-transaction-types/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(availableTransactionType.id?.toInt()))
            .andExpect(jsonPath("$.transactionType").value(DEFAULT_TRANSACTION_TYPE)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingAvailableTransactionType() {
        // Get the availableTransactionType
        restAvailableTransactionTypeMockMvc.perform(get("/api/available-transaction-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateAvailableTransactionType() {
        // Initialize the database
        availableTransactionTypeRepository.saveAndFlush(availableTransactionType)

        val databaseSizeBeforeUpdate = availableTransactionTypeRepository.findAll().size

        // Update the availableTransactionType
        val id = availableTransactionType.id
        assertNotNull(id)
        val updatedAvailableTransactionType = availableTransactionTypeRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedAvailableTransactionType are not directly saved in db
        em.detach(updatedAvailableTransactionType)
        updatedAvailableTransactionType.transactionType = UPDATED_TRANSACTION_TYPE

        restAvailableTransactionTypeMockMvc.perform(
            put("/api/available-transaction-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedAvailableTransactionType))
        ).andExpect(status().isOk)

        // Validate the AvailableTransactionType in the database
        val availableTransactionTypeList = availableTransactionTypeRepository.findAll()
        assertThat(availableTransactionTypeList).hasSize(databaseSizeBeforeUpdate)
        val testAvailableTransactionType = availableTransactionTypeList[availableTransactionTypeList.size - 1]
        assertThat(testAvailableTransactionType.transactionType).isEqualTo(UPDATED_TRANSACTION_TYPE)
    }

    @Test
    @Transactional
    fun updateNonExistingAvailableTransactionType() {
        val databaseSizeBeforeUpdate = availableTransactionTypeRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvailableTransactionTypeMockMvc.perform(
            put("/api/available-transaction-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(availableTransactionType))
        ).andExpect(status().isBadRequest)

        // Validate the AvailableTransactionType in the database
        val availableTransactionTypeList = availableTransactionTypeRepository.findAll()
        assertThat(availableTransactionTypeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteAvailableTransactionType() {
        // Initialize the database
        availableTransactionTypeRepository.saveAndFlush(availableTransactionType)

        val databaseSizeBeforeDelete = availableTransactionTypeRepository.findAll().size

        // Delete the availableTransactionType
        restAvailableTransactionTypeMockMvc.perform(
            delete("/api/available-transaction-types/{id}", availableTransactionType.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val availableTransactionTypeList = availableTransactionTypeRepository.findAll()
        assertThat(availableTransactionTypeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TRANSACTION_TYPE = "AAAAAAAAAA"
        private const val UPDATED_TRANSACTION_TYPE = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): AvailableTransactionType {
            val availableTransactionType = AvailableTransactionType(
                transactionType = DEFAULT_TRANSACTION_TYPE
            )

            return availableTransactionType
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): AvailableTransactionType {
            val availableTransactionType = AvailableTransactionType(
                transactionType = UPDATED_TRANSACTION_TYPE
            )

            return availableTransactionType
        }
    }
}
