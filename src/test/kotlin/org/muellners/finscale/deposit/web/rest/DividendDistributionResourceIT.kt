package org.muellners.finscale.deposit.web.rest

import org.muellners.finscale.deposit.DepositAccountManagementApp
import org.muellners.finscale.deposit.config.SecurityBeanOverrideConfiguration
import org.muellners.finscale.deposit.domain.DividendDistribution
import org.muellners.finscale.deposit.repository.DividendDistributionRepository
import org.muellners.finscale.deposit.web.rest.errors.ExceptionTranslator

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import javax.persistence.EntityManager
import java.time.LocalDate
import java.time.ZoneId

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


/**
 * Integration tests for the [DividendDistributionResource] REST controller.
 *
 * @see DividendDistributionResource
 */
@SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
@AutoConfigureMockMvc
@WithMockUser
class DividendDistributionResourceIT  {

    @Autowired
    private lateinit var dividendDistributionRepository: DividendDistributionRepository

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


    private lateinit var restDividendDistributionMockMvc: MockMvc

    private lateinit var dividendDistribution: DividendDistribution

    
    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val dividendDistributionResource = DividendDistributionResource(dividendDistributionRepository)		
         this.restDividendDistributionMockMvc = MockMvcBuilders.standaloneSetup(dividendDistributionResource)		
             .setCustomArgumentResolvers(pageableArgumentResolver)		
             .setControllerAdvice(exceptionTranslator)		
             .setConversionService(createFormattingConversionService())		
             .setMessageConverters(jacksonMessageConverter)		
             .setValidator(validator).build()
    }


    @BeforeEach
    fun initTest() {
        dividendDistribution = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createDividendDistribution() {
        val databaseSizeBeforeCreate = dividendDistributionRepository.findAll().size

        // Create the DividendDistribution
        restDividendDistributionMockMvc.perform(
            post("/api/dividend-distributions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dividendDistribution))
        ).andExpect(status().isCreated)

        // Validate the DividendDistribution in the database
        val dividendDistributionList = dividendDistributionRepository.findAll()
        assertThat(dividendDistributionList).hasSize(databaseSizeBeforeCreate + 1)
        val testDividendDistribution = dividendDistributionList[dividendDistributionList.size - 1]
        assertThat(testDividendDistribution.dueDate).isEqualTo(DEFAULT_DUE_DATE)
        assertThat(testDividendDistribution.dividendRate).isEqualTo(DEFAULT_DIVIDEND_RATE)
    }

    @Test
    @Transactional
    fun createDividendDistributionWithExistingId() {
        val databaseSizeBeforeCreate = dividendDistributionRepository.findAll().size

        // Create the DividendDistribution with an existing ID
        dividendDistribution.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restDividendDistributionMockMvc.perform(
            post("/api/dividend-distributions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dividendDistribution))
        ).andExpect(status().isBadRequest)

        // Validate the DividendDistribution in the database
        val dividendDistributionList = dividendDistributionRepository.findAll()
        assertThat(dividendDistributionList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    @Transactional
    fun checkDueDateIsRequired() {
        val databaseSizeBeforeTest = dividendDistributionRepository.findAll().size
        // set the field null
        dividendDistribution.dueDate = null

        // Create the DividendDistribution, which fails.

        restDividendDistributionMockMvc.perform(
            post("/api/dividend-distributions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dividendDistribution))
        ).andExpect(status().isBadRequest)

        val dividendDistributionList = dividendDistributionRepository.findAll()
        assertThat(dividendDistributionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDividendRateIsRequired() {
        val databaseSizeBeforeTest = dividendDistributionRepository.findAll().size
        // set the field null
        dividendDistribution.dividendRate = null

        // Create the DividendDistribution, which fails.

        restDividendDistributionMockMvc.perform(
            post("/api/dividend-distributions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dividendDistribution))
        ).andExpect(status().isBadRequest)

        val dividendDistributionList = dividendDistributionRepository.findAll()
        assertThat(dividendDistributionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllDividendDistributions() {
        // Initialize the database
        dividendDistributionRepository.saveAndFlush(dividendDistribution)
        
        // Get all the dividendDistributionList
        restDividendDistributionMockMvc.perform(get("/api/dividend-distributions?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dividendDistribution.id?.toInt())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].dividendRate").value(hasItem(DEFAULT_DIVIDEND_RATE)))    }
    
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getDividendDistribution() {
        // Initialize the database
        dividendDistributionRepository.saveAndFlush(dividendDistribution)

        val id = dividendDistribution.id
        assertNotNull(id)

        // Get the dividendDistribution
        restDividendDistributionMockMvc.perform(get("/api/dividend-distributions/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dividendDistribution.id?.toInt()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.dividendRate").value(DEFAULT_DIVIDEND_RATE))    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingDividendDistribution() {
        // Get the dividendDistribution
        restDividendDistributionMockMvc.perform(get("/api/dividend-distributions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateDividendDistribution() {
        // Initialize the database
        dividendDistributionRepository.saveAndFlush(dividendDistribution)

        val databaseSizeBeforeUpdate = dividendDistributionRepository.findAll().size

        // Update the dividendDistribution
        val id = dividendDistribution.id
        assertNotNull(id)
        val updatedDividendDistribution = dividendDistributionRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedDividendDistribution are not directly saved in db
        em.detach(updatedDividendDistribution)
        updatedDividendDistribution.dueDate = UPDATED_DUE_DATE
        updatedDividendDistribution.dividendRate = UPDATED_DIVIDEND_RATE

        restDividendDistributionMockMvc.perform(
            put("/api/dividend-distributions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedDividendDistribution))
        ).andExpect(status().isOk)

        // Validate the DividendDistribution in the database
        val dividendDistributionList = dividendDistributionRepository.findAll()
        assertThat(dividendDistributionList).hasSize(databaseSizeBeforeUpdate)
        val testDividendDistribution = dividendDistributionList[dividendDistributionList.size - 1]
        assertThat(testDividendDistribution.dueDate).isEqualTo(UPDATED_DUE_DATE)
        assertThat(testDividendDistribution.dividendRate).isEqualTo(UPDATED_DIVIDEND_RATE)
    }

    @Test
    @Transactional
    fun updateNonExistingDividendDistribution() {
        val databaseSizeBeforeUpdate = dividendDistributionRepository.findAll().size


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDividendDistributionMockMvc.perform(
            put("/api/dividend-distributions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dividendDistribution))
        ).andExpect(status().isBadRequest)

        // Validate the DividendDistribution in the database
        val dividendDistributionList = dividendDistributionRepository.findAll()
        assertThat(dividendDistributionList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteDividendDistribution() {
        // Initialize the database
        dividendDistributionRepository.saveAndFlush(dividendDistribution)

        val databaseSizeBeforeDelete = dividendDistributionRepository.findAll().size

        // Delete the dividendDistribution
        restDividendDistributionMockMvc.perform(
            delete("/api/dividend-distributions/{id}", dividendDistribution.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val dividendDistributionList = dividendDistributionRepository.findAll()
        assertThat(dividendDistributionList).hasSize(databaseSizeBeforeDelete - 1)
    }


    companion object {

        private val DEFAULT_DUE_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_DUE_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())

        private const val DEFAULT_DIVIDEND_RATE = "AAAAAAAAAA"
        private const val UPDATED_DIVIDEND_RATE = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): DividendDistribution {
            val dividendDistribution = DividendDistribution(
                dueDate = DEFAULT_DUE_DATE,
                dividendRate = DEFAULT_DIVIDEND_RATE
            )

            return dividendDistribution
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): DividendDistribution {
            val dividendDistribution = DividendDistribution(
                dueDate = UPDATED_DUE_DATE,
                dividendRate = UPDATED_DIVIDEND_RATE
            )

            return dividendDistribution
        }
    }
}
