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
import org.muellners.finscale.deposit.domain.enumeration.InterestPayable
import org.muellners.finscale.deposit.domain.enumeration.TimeUnit
import org.muellners.finscale.deposit.repository.TermViewRepository
import org.muellners.finscale.deposit.view.TermView
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
 * Integration tests for the [TermResource] REST controller.
 *
 * @see TermResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class TermResourceIT {

    @Autowired
    private lateinit var termViewRepository: TermViewRepository

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

    private lateinit var restTermMockMvc: MockMvc

    private lateinit var termView: TermView

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val termResource = TermResource(termViewRepository)
         this.restTermMockMvc = MockMvcBuilders.standaloneSetup(termResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        termView = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTerm() {
        val databaseSizeBeforeCreate = termViewRepository.findAll().size

        // Create the Term
        restTermMockMvc.perform(
            post("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(termView))
        ).andExpect(status().isCreated)

        // Validate the Term in the database
        val termList = termViewRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeCreate + 1)
        val testTerm = termList[termList.size - 1]
        assertThat(testTerm.period).isEqualTo(DEFAULT_PERIOD)
        assertThat(testTerm.timeUnit).isEqualTo(DEFAULT_TIME_UNIT)
        assertThat(testTerm.interestPayable).isEqualTo(DEFAULT_INTEREST_PAYABLE)
    }

    @Test
    @Transactional
    fun createTermWithExistingId() {
        val databaseSizeBeforeCreate = termViewRepository.findAll().size

        // Create the Term with an existing ID
        termView.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTermMockMvc.perform(
            post("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(termView))
        ).andExpect(status().isBadRequest)

        // Validate the Term in the database
        val termList = termViewRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkInterestPayableIsRequired() {
        val databaseSizeBeforeTest = termViewRepository.findAll().size
        // set the field null
        termView.interestPayable = null

        // Create the Term, which fails.

        restTermMockMvc.perform(
            post("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(termView))
        ).andExpect(status().isBadRequest)

        val termList = termViewRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllTerms() {
        // Initialize the database
        termViewRepository.saveAndFlush(termView)

        // Get all the termList
        restTermMockMvc.perform(get("/api/terms?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(termView.id?.toInt())))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD)))
            .andExpect(jsonPath("$.[*].timeUnit").value(hasItem(DEFAULT_TIME_UNIT.toString())))
            .andExpect(jsonPath("$.[*].interestPayable").value(hasItem(DEFAULT_INTEREST_PAYABLE.toString()))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getTerm() {
        // Initialize the database
        termViewRepository.saveAndFlush(termView)

        val id = termView.id
        assertNotNull(id)

        // Get the term
        restTermMockMvc.perform(get("/api/terms/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(termView.id?.toInt()))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD))
            .andExpect(jsonPath("$.timeUnit").value(DEFAULT_TIME_UNIT.toString()))
            .andExpect(jsonPath("$.interestPayable").value(DEFAULT_INTEREST_PAYABLE.toString())) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingTerm() {
        // Get the term
        restTermMockMvc.perform(get("/api/terms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateTerm() {
        // Initialize the database
        termViewRepository.saveAndFlush(termView)

        val databaseSizeBeforeUpdate = termViewRepository.findAll().size

        // Update the term
        val id = termView.id
        assertNotNull(id)
        val updatedTerm = termViewRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTerm are not directly saved in db
        em.detach(updatedTerm)
        updatedTerm.period = UPDATED_PERIOD
        updatedTerm.timeUnit = UPDATED_TIME_UNIT
        updatedTerm.interestPayable = UPDATED_INTEREST_PAYABLE

        restTermMockMvc.perform(
            put("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedTerm))
        ).andExpect(status().isOk)

        // Validate the Term in the database
        val termList = termViewRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeUpdate)
        val testTerm = termList[termList.size - 1]
        assertThat(testTerm.period).isEqualTo(UPDATED_PERIOD)
        assertThat(testTerm.timeUnit).isEqualTo(UPDATED_TIME_UNIT)
        assertThat(testTerm.interestPayable).isEqualTo(UPDATED_INTEREST_PAYABLE)
    }

    @Test
    @Transactional
    fun updateNonExistingTerm() {
        val databaseSizeBeforeUpdate = termViewRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTermMockMvc.perform(
            put("/api/terms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(termView))
        ).andExpect(status().isBadRequest)

        // Validate the Term in the database
        val termList = termViewRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteTerm() {
        // Initialize the database
        termViewRepository.saveAndFlush(termView)

        val databaseSizeBeforeDelete = termViewRepository.findAll().size

        // Delete the term
        restTermMockMvc.perform(
            delete("/api/terms/{id}", termView.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val termList = termViewRepository.findAll()
        assertThat(termList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_PERIOD: Int = 1
        private const val UPDATED_PERIOD: Int = 2

        private val DEFAULT_TIME_UNIT: TimeUnit = TimeUnit.MONTH
        private val UPDATED_TIME_UNIT: TimeUnit = TimeUnit.YEAR

        private val DEFAULT_INTEREST_PAYABLE: InterestPayable = InterestPayable.MATURITY
        private val UPDATED_INTEREST_PAYABLE: InterestPayable = InterestPayable.ANNUALLY

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): TermView {
            val term = TermView(
                period = DEFAULT_PERIOD,
                timeUnit = DEFAULT_TIME_UNIT,
                interestPayable = DEFAULT_INTEREST_PAYABLE
            )

            return term
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): TermView {
            val term = TermView(
                period = UPDATED_PERIOD,
                timeUnit = UPDATED_TIME_UNIT,
                interestPayable = UPDATED_INTEREST_PAYABLE
            )

            return term
        }
    }
}
