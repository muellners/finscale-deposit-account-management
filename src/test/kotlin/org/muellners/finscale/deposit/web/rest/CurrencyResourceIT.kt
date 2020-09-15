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
import org.muellners.finscale.deposit.domain.Currency
import org.muellners.finscale.deposit.repository.CurrencyRepository
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
 * Integration tests for the [CurrencyResource] REST controller.
 *
 * @see CurrencyResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class CurrencyResourceIT {

    @Autowired
    private lateinit var currencyRepository: CurrencyRepository

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

    private lateinit var restCurrencyMockMvc: MockMvc

    private lateinit var currency: Currency

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val currencyResource = CurrencyResource(currencyRepository)
         this.restCurrencyMockMvc = MockMvcBuilders.standaloneSetup(currencyResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        currency = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCurrency() {
        val databaseSizeBeforeCreate = currencyRepository.findAll().size

        // Create the Currency
        restCurrencyMockMvc.perform(
            post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(currency))
        ).andExpect(status().isCreated)

        // Validate the Currency in the database
        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeCreate + 1)
        val testCurrency = currencyList[currencyList.size - 1]
        assertThat(testCurrency.code).isEqualTo(DEFAULT_CODE)
        assertThat(testCurrency.name).isEqualTo(DEFAULT_NAME)
        assertThat(testCurrency.sign).isEqualTo(DEFAULT_SIGN)
        assertThat(testCurrency.scale).isEqualTo(DEFAULT_SCALE)
    }

    @Test
    @Transactional
    fun createCurrencyWithExistingId() {
        val databaseSizeBeforeCreate = currencyRepository.findAll().size

        // Create the Currency with an existing ID
        currency.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCurrencyMockMvc.perform(
            post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(currency))
        ).andExpect(status().isBadRequest)

        // Validate the Currency in the database
        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkCodeIsRequired() {
        val databaseSizeBeforeTest = currencyRepository.findAll().size
        // set the field null
        currency.code = null

        // Create the Currency, which fails.

        restCurrencyMockMvc.perform(
            post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(currency))
        ).andExpect(status().isBadRequest)

        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = currencyRepository.findAll().size
        // set the field null
        currency.name = null

        // Create the Currency, which fails.

        restCurrencyMockMvc.perform(
            post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(currency))
        ).andExpect(status().isBadRequest)

        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkSignIsRequired() {
        val databaseSizeBeforeTest = currencyRepository.findAll().size
        // set the field null
        currency.sign = null

        // Create the Currency, which fails.

        restCurrencyMockMvc.perform(
            post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(currency))
        ).andExpect(status().isBadRequest)

        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkScaleIsRequired() {
        val databaseSizeBeforeTest = currencyRepository.findAll().size
        // set the field null
        currency.scale = null

        // Create the Currency, which fails.

        restCurrencyMockMvc.perform(
            post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(currency))
        ).andExpect(status().isBadRequest)

        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCurrencies() {
        // Initialize the database
        currencyRepository.saveAndFlush(currency)

        // Get all the currencyList
        restCurrencyMockMvc.perform(get("/api/currencies?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(currency.id?.toInt())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sign").value(hasItem(DEFAULT_SIGN)))
            .andExpect(jsonPath("$.[*].scale").value(hasItem(DEFAULT_SCALE))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getCurrency() {
        // Initialize the database
        currencyRepository.saveAndFlush(currency)

        val id = currency.id
        assertNotNull(id)

        // Get the currency
        restCurrencyMockMvc.perform(get("/api/currencies/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(currency.id?.toInt()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sign").value(DEFAULT_SIGN))
            .andExpect(jsonPath("$.scale").value(DEFAULT_SCALE)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingCurrency() {
        // Get the currency
        restCurrencyMockMvc.perform(get("/api/currencies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCurrency() {
        // Initialize the database
        currencyRepository.saveAndFlush(currency)

        val databaseSizeBeforeUpdate = currencyRepository.findAll().size

        // Update the currency
        val id = currency.id
        assertNotNull(id)
        val updatedCurrency = currencyRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCurrency are not directly saved in db
        em.detach(updatedCurrency)
        updatedCurrency.code = UPDATED_CODE
        updatedCurrency.name = UPDATED_NAME
        updatedCurrency.sign = UPDATED_SIGN
        updatedCurrency.scale = UPDATED_SCALE

        restCurrencyMockMvc.perform(
            put("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCurrency))
        ).andExpect(status().isOk)

        // Validate the Currency in the database
        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeUpdate)
        val testCurrency = currencyList[currencyList.size - 1]
        assertThat(testCurrency.code).isEqualTo(UPDATED_CODE)
        assertThat(testCurrency.name).isEqualTo(UPDATED_NAME)
        assertThat(testCurrency.sign).isEqualTo(UPDATED_SIGN)
        assertThat(testCurrency.scale).isEqualTo(UPDATED_SCALE)
    }

    @Test
    @Transactional
    fun updateNonExistingCurrency() {
        val databaseSizeBeforeUpdate = currencyRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCurrencyMockMvc.perform(
            put("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(currency))
        ).andExpect(status().isBadRequest)

        // Validate the Currency in the database
        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteCurrency() {
        // Initialize the database
        currencyRepository.saveAndFlush(currency)

        val databaseSizeBeforeDelete = currencyRepository.findAll().size

        // Delete the currency
        restCurrencyMockMvc.perform(
            delete("/api/currencies/{id}", currency.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val currencyList = currencyRepository.findAll()
        assertThat(currencyList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_CODE = "AAAAAAAAAA"
        private const val UPDATED_CODE = "BBBBBBBBBB"

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_SIGN = "AAAAAAAAAA"
        private const val UPDATED_SIGN = "BBBBBBBBBB"

        private const val DEFAULT_SCALE: Int = 1
        private const val UPDATED_SCALE: Int = 2

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Currency {
            val currency = Currency(
                code = DEFAULT_CODE,
                name = DEFAULT_NAME,
                sign = DEFAULT_SIGN,
                scale = DEFAULT_SCALE
            )

            return currency
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Currency {
            val currency = Currency(
                code = UPDATED_CODE,
                name = UPDATED_NAME,
                sign = UPDATED_SIGN,
                scale = UPDATED_SCALE
            )

            return currency
        }
    }
}
