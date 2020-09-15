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
import org.muellners.finscale.deposit.domain.Charge
import org.muellners.finscale.deposit.repository.ChargeRepository
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
 * Integration tests for the [ChargeResource] REST controller.
 *
 * @see ChargeResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ChargeResourceIT {

    @Autowired
    private lateinit var chargeRepository: ChargeRepository

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

    private lateinit var restChargeMockMvc: MockMvc

    private lateinit var charge: Charge

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val chargeResource = ChargeResource(chargeRepository)
         this.restChargeMockMvc = MockMvcBuilders.standaloneSetup(chargeResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        charge = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCharge() {
        val databaseSizeBeforeCreate = chargeRepository.findAll().size

        // Create the Charge
        restChargeMockMvc.perform(
            post("/api/charges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(charge))
        ).andExpect(status().isCreated)

        // Validate the Charge in the database
        val chargeList = chargeRepository.findAll()
        assertThat(chargeList).hasSize(databaseSizeBeforeCreate + 1)
        val testCharge = chargeList[chargeList.size - 1]
        assertThat(testCharge.actionIdentifier).isEqualTo(DEFAULT_ACTION_IDENTIFIER)
        assertThat(testCharge.incomeAccountIdentifier).isEqualTo(DEFAULT_INCOME_ACCOUNT_IDENTIFIER)
        assertThat(testCharge.name).isEqualTo(DEFAULT_NAME)
        assertThat(testCharge.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testCharge.proportional).isEqualTo(DEFAULT_PROPORTIONAL)
        assertThat(testCharge.amount).isEqualTo(DEFAULT_AMOUNT)
    }

    @Test
    @Transactional
    fun createChargeWithExistingId() {
        val databaseSizeBeforeCreate = chargeRepository.findAll().size

        // Create the Charge with an existing ID
        charge.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restChargeMockMvc.perform(
            post("/api/charges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(charge))
        ).andExpect(status().isBadRequest)

        // Validate the Charge in the database
        val chargeList = chargeRepository.findAll()
        assertThat(chargeList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = chargeRepository.findAll().size
        // set the field null
        charge.name = null

        // Create the Charge, which fails.

        restChargeMockMvc.perform(
            post("/api/charges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(charge))
        ).andExpect(status().isBadRequest)

        val chargeList = chargeRepository.findAll()
        assertThat(chargeList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCharges() {
        // Initialize the database
        chargeRepository.saveAndFlush(charge)

        // Get all the chargeList
        restChargeMockMvc.perform(get("/api/charges?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(charge.id?.toInt())))
            .andExpect(jsonPath("$.[*].actionIdentifier").value(hasItem(DEFAULT_ACTION_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].incomeAccountIdentifier").value(hasItem(DEFAULT_INCOME_ACCOUNT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].proportional").value(hasItem(DEFAULT_PROPORTIONAL)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.toDouble()))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getCharge() {
        // Initialize the database
        chargeRepository.saveAndFlush(charge)

        val id = charge.id
        assertNotNull(id)

        // Get the charge
        restChargeMockMvc.perform(get("/api/charges/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(charge.id?.toInt()))
            .andExpect(jsonPath("$.actionIdentifier").value(DEFAULT_ACTION_IDENTIFIER))
            .andExpect(jsonPath("$.incomeAccountIdentifier").value(DEFAULT_INCOME_ACCOUNT_IDENTIFIER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.proportional").value(DEFAULT_PROPORTIONAL))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.toDouble())) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingCharge() {
        // Get the charge
        restChargeMockMvc.perform(get("/api/charges/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCharge() {
        // Initialize the database
        chargeRepository.saveAndFlush(charge)

        val databaseSizeBeforeUpdate = chargeRepository.findAll().size

        // Update the charge
        val id = charge.id
        assertNotNull(id)
        val updatedCharge = chargeRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCharge are not directly saved in db
        em.detach(updatedCharge)
        updatedCharge.actionIdentifier = UPDATED_ACTION_IDENTIFIER
        updatedCharge.incomeAccountIdentifier = UPDATED_INCOME_ACCOUNT_IDENTIFIER
        updatedCharge.name = UPDATED_NAME
        updatedCharge.description = UPDATED_DESCRIPTION
        updatedCharge.proportional = UPDATED_PROPORTIONAL
        updatedCharge.amount = UPDATED_AMOUNT

        restChargeMockMvc.perform(
            put("/api/charges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCharge))
        ).andExpect(status().isOk)

        // Validate the Charge in the database
        val chargeList = chargeRepository.findAll()
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate)
        val testCharge = chargeList[chargeList.size - 1]
        assertThat(testCharge.actionIdentifier).isEqualTo(UPDATED_ACTION_IDENTIFIER)
        assertThat(testCharge.incomeAccountIdentifier).isEqualTo(UPDATED_INCOME_ACCOUNT_IDENTIFIER)
        assertThat(testCharge.name).isEqualTo(UPDATED_NAME)
        assertThat(testCharge.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testCharge.proportional).isEqualTo(UPDATED_PROPORTIONAL)
        assertThat(testCharge.amount).isEqualTo(UPDATED_AMOUNT)
    }

    @Test
    @Transactional
    fun updateNonExistingCharge() {
        val databaseSizeBeforeUpdate = chargeRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChargeMockMvc.perform(
            put("/api/charges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(charge))
        ).andExpect(status().isBadRequest)

        // Validate the Charge in the database
        val chargeList = chargeRepository.findAll()
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteCharge() {
        // Initialize the database
        chargeRepository.saveAndFlush(charge)

        val databaseSizeBeforeDelete = chargeRepository.findAll().size

        // Delete the charge
        restChargeMockMvc.perform(
            delete("/api/charges/{id}", charge.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val chargeList = chargeRepository.findAll()
        assertThat(chargeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_ACTION_IDENTIFIER = "AAAAAAAAAA"
        private const val UPDATED_ACTION_IDENTIFIER = "BBBBBBBBBB"

        private const val DEFAULT_INCOME_ACCOUNT_IDENTIFIER = "AAAAAAAAAA"
        private const val UPDATED_INCOME_ACCOUNT_IDENTIFIER = "BBBBBBBBBB"

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private const val DEFAULT_PROPORTIONAL: Boolean = false
        private const val UPDATED_PROPORTIONAL: Boolean = true

        private const val DEFAULT_AMOUNT: Double = 1.0
        private const val UPDATED_AMOUNT: Double = 2.0

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Charge {
            val charge = Charge(
                actionIdentifier = DEFAULT_ACTION_IDENTIFIER,
                incomeAccountIdentifier = DEFAULT_INCOME_ACCOUNT_IDENTIFIER,
                name = DEFAULT_NAME,
                description = DEFAULT_DESCRIPTION,
                proportional = DEFAULT_PROPORTIONAL,
                amount = DEFAULT_AMOUNT
            )

            return charge
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Charge {
            val charge = Charge(
                actionIdentifier = UPDATED_ACTION_IDENTIFIER,
                incomeAccountIdentifier = UPDATED_INCOME_ACCOUNT_IDENTIFIER,
                name = UPDATED_NAME,
                description = UPDATED_DESCRIPTION,
                proportional = UPDATED_PROPORTIONAL,
                amount = UPDATED_AMOUNT
            )

            return charge
        }
    }
}
