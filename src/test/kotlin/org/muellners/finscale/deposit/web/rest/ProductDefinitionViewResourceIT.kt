package org.muellners.finscale.deposit.web.rest
//
// import javax.persistence.EntityManager
// import kotlin.test.assertNotNull
// import org.assertj.core.api.Assertions.assertThat
// import org.hamcrest.Matchers.hasItem
// import org.junit.jupiter.api.BeforeEach
// import org.junit.jupiter.api.Test
// import org.mockito.MockitoAnnotations
// import org.muellners.finscale.deposit.DepositAccountManagementApp
// import org.muellners.finscale.deposit.config.SecurityBeanOverrideConfiguration
// import org.muellners.finscale.deposit.domain.Currency
// import org.muellners.finscale.deposit.domain.enumeration.Type
// import org.muellners.finscale.deposit.repository.ProductDefinitionRepository
// import org.muellners.finscale.deposit.view.ProductDefinitionView
// import org.muellners.finscale.deposit.view.TermView
// import org.muellners.finscale.deposit.web.rest.errors.ExceptionTranslator
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
// import org.springframework.boot.test.context.SpringBootTest
// import org.springframework.data.web.PageableHandlerMethodArgumentResolver
// import org.springframework.http.MediaType
// import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
// import org.springframework.security.test.context.support.WithMockUser
// import org.springframework.test.web.servlet.MockMvc
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
// import org.springframework.test.web.servlet.setup.MockMvcBuilders
// import org.springframework.transaction.annotation.Transactional
// import org.springframework.validation.Validator
//
// /**
// * Integration tests for the [ProductDefinitionResource] REST controller.
// *
// * @see ProductDefinitionResource
// */
// @SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
// @AutoConfigureMockMvc
// @WithMockUser
// class ProductDefinitionViewResourceIT {
//
//    @Autowired
//    private lateinit var productDefinitionRepository: ProductDefinitionRepository
//
//    @Autowired
//    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter
//
//    @Autowired
//    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver
//
//    @Autowired
//    private lateinit var exceptionTranslator: ExceptionTranslator
//
//    @Autowired
//    private lateinit var validator: Validator
//
//    @Autowired
//    private lateinit var em: EntityManager
//
//    private lateinit var restProductDefinitionMockMvc: MockMvc
//
//    private lateinit var productDefinitionView: ProductDefinitionView
//
//    @BeforeEach
//    fun setup() {
//        MockitoAnnotations.initMocks(this)
//        val productDefinitionResource = ProductDefinitionResource(productDefinitionRepository)
//         this.restProductDefinitionMockMvc = MockMvcBuilders.standaloneSetup(productDefinitionResource)
//             .setCustomArgumentResolvers(pageableArgumentResolver)
//             .setControllerAdvice(exceptionTranslator)
//             .setConversionService(createFormattingConversionService())
//             .setMessageConverters(jacksonMessageConverter)
//             .setValidator(validator).build()
//    }
//
//    @BeforeEach
//    fun initTest() {
//        productDefinitionView = createEntity(em)
//    }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun createProductDefinition() {
//        val databaseSizeBeforeCreate = productDefinitionRepository.findAll().size
//
//        // Create the ProductDefinition
//        restProductDefinitionMockMvc.perform(
//            post("/api/product-definitions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(productDefinitionView))
//        ).andExpect(status().isCreated)
//
//        // Validate the ProductDefinition in the database
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeCreate + 1)
//        val testProductDefinition = productDefinitionList[productDefinitionList.size - 1]
//        assertThat(testProductDefinition.type).isEqualTo(DEFAULT_TYPE)
//        assertThat(testProductDefinition.identifier).isEqualTo(DEFAULT_IDENTIFIER)
//        assertThat(testProductDefinition.name).isEqualTo(DEFAULT_NAME)
//        assertThat(testProductDefinition.description).isEqualTo(DEFAULT_DESCRIPTION)
//        assertThat(testProductDefinition.minimumBalance).isEqualTo(DEFAULT_MINIMUM_BALANCE)
//        assertThat(testProductDefinition.equityLedgerIdentifier).isEqualTo(DEFAULT_EQUITY_LEDGER_IDENTIFIER)
//        assertThat(testProductDefinition.cashAccountIdentifier).isEqualTo(DEFAULT_CASH_ACCOUNT_IDENTIFIER)
//        assertThat(testProductDefinition.expenseAccountIdentifier).isEqualTo(DEFAULT_EXPENSE_ACCOUNT_IDENTIFIER)
//        assertThat(testProductDefinition.accrueAccountIdentifier).isEqualTo(DEFAULT_ACCRUE_ACCOUNT_IDENTIFIER)
//        assertThat(testProductDefinition.interest).isEqualTo(DEFAULT_INTEREST)
//        assertThat(testProductDefinition.flexible).isEqualTo(DEFAULT_FLEXIBLE)
//        assertThat(testProductDefinition.active).isEqualTo(DEFAULT_ACTIVE)
//    }
//
//    @Test
//    @Transactional
//    fun createProductDefinitionWithExistingId() {
//        val databaseSizeBeforeCreate = productDefinitionRepository.findAll().size
//
//        // Create the ProductDefinition with an existing ID
//        productDefinitionView.id = 1L
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restProductDefinitionMockMvc.perform(
//            post("/api/product-definitions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(productDefinitionView))
//        ).andExpect(status().isBadRequest)
//
//        // Validate the ProductDefinition in the database
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeCreate)
//    }
//
//    @Test
//    @Transactional
//    fun checkNameIsRequired() {
//        val databaseSizeBeforeTest = productDefinitionRepository.findAll().size
//        // set the field null
//        productDefinitionView.name = null
//
//        // Create the ProductDefinition, which fails.
//
//        restProductDefinitionMockMvc.perform(
//            post("/api/product-definitions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(productDefinitionView))
//        ).andExpect(status().isBadRequest)
//
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeTest)
//    }
//
//    @Test
//    @Transactional
//    fun checkMinimumBalanceIsRequired() {
//        val databaseSizeBeforeTest = productDefinitionRepository.findAll().size
//        // set the field null
//        productDefinitionView.minimumBalance = null
//
//        // Create the ProductDefinition, which fails.
//
//        restProductDefinitionMockMvc.perform(
//            post("/api/product-definitions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(productDefinitionView))
//        ).andExpect(status().isBadRequest)
//
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeTest)
//    }
//
//    @Test
//    @Transactional
//    fun checkFlexibleIsRequired() {
//        val databaseSizeBeforeTest = productDefinitionRepository.findAll().size
//        // set the field null
//        productDefinitionView.flexible = null
//
//        // Create the ProductDefinition, which fails.
//
//        restProductDefinitionMockMvc.perform(
//            post("/api/product-definitions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(productDefinitionView))
//        ).andExpect(status().isBadRequest)
//
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeTest)
//    }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun getAllProductDefinitions() {
//        // Initialize the database
//        productDefinitionRepository.saveAndFlush(productDefinitionView)
//
//        // Get all the productDefinitionList
//        restProductDefinitionMockMvc.perform(get("/api/product-definitions?sort=id,desc"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(productDefinitionView.id?.toInt())))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
//            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER)))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
//            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
//            .andExpect(jsonPath("$.[*].minimumBalance").value(hasItem(DEFAULT_MINIMUM_BALANCE.toDouble())))
//            .andExpect(jsonPath("$.[*].equityLedgerIdentifier").value(hasItem(DEFAULT_EQUITY_LEDGER_IDENTIFIER)))
//            .andExpect(jsonPath("$.[*].cashAccountIdentifier").value(hasItem(DEFAULT_CASH_ACCOUNT_IDENTIFIER)))
//            .andExpect(jsonPath("$.[*].expenseAccountIdentifier").value(hasItem(DEFAULT_EXPENSE_ACCOUNT_IDENTIFIER)))
//            .andExpect(jsonPath("$.[*].accrueAccountIdentifier").value(hasItem(DEFAULT_ACCRUE_ACCOUNT_IDENTIFIER)))
//            .andExpect(jsonPath("$.[*].interest").value(hasItem(DEFAULT_INTEREST.toDouble())))
//            .andExpect(jsonPath("$.[*].flexible").value(hasItem(DEFAULT_FLEXIBLE)))
//            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE))) }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun getProductDefinition() {
//        // Initialize the database
//        productDefinitionRepository.saveAndFlush(productDefinitionView)
//
//        val id = productDefinitionView.id
//        assertNotNull(id)
//
//        // Get the productDefinition
//        restProductDefinitionMockMvc.perform(get("/api/product-definitions/{id}", id))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.id").value(productDefinitionView.id?.toInt()))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
//            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER))
//            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
//            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
//            .andExpect(jsonPath("$.minimumBalance").value(DEFAULT_MINIMUM_BALANCE.toDouble()))
//            .andExpect(jsonPath("$.equityLedgerIdentifier").value(DEFAULT_EQUITY_LEDGER_IDENTIFIER))
//            .andExpect(jsonPath("$.cashAccountIdentifier").value(DEFAULT_CASH_ACCOUNT_IDENTIFIER))
//            .andExpect(jsonPath("$.expenseAccountIdentifier").value(DEFAULT_EXPENSE_ACCOUNT_IDENTIFIER))
//            .andExpect(jsonPath("$.accrueAccountIdentifier").value(DEFAULT_ACCRUE_ACCOUNT_IDENTIFIER))
//            .andExpect(jsonPath("$.interest").value(DEFAULT_INTEREST.toDouble()))
//            .andExpect(jsonPath("$.flexible").value(DEFAULT_FLEXIBLE))
//            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE)) }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun getNonExistingProductDefinition() {
//        // Get the productDefinition
//        restProductDefinitionMockMvc.perform(get("/api/product-definitions/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound)
//    }
//    @Test
//    @Transactional
//    fun updateProductDefinition() {
//        // Initialize the database
//        productDefinitionRepository.saveAndFlush(productDefinitionView)
//
//        val databaseSizeBeforeUpdate = productDefinitionRepository.findAll().size
//
//        // Update the productDefinition
//        val id = productDefinitionView.id
//        assertNotNull(id)
//        val updatedProductDefinition = productDefinitionRepository.findById(id).get()
//        // Disconnect from session so that the updates on updatedProductDefinition are not directly saved in db
//        em.detach(updatedProductDefinition)
//        updatedProductDefinition.type = UPDATED_TYPE
//        updatedProductDefinition.identifier = UPDATED_IDENTIFIER
//        updatedProductDefinition.name = UPDATED_NAME
//        updatedProductDefinition.description = UPDATED_DESCRIPTION
//        updatedProductDefinition.minimumBalance = UPDATED_MINIMUM_BALANCE
//        updatedProductDefinition.equityLedgerIdentifier = UPDATED_EQUITY_LEDGER_IDENTIFIER
//        updatedProductDefinition.cashAccountIdentifier = UPDATED_CASH_ACCOUNT_IDENTIFIER
//        updatedProductDefinition.expenseAccountIdentifier = UPDATED_EXPENSE_ACCOUNT_IDENTIFIER
//        updatedProductDefinition.accrueAccountIdentifier = UPDATED_ACCRUE_ACCOUNT_IDENTIFIER
//        updatedProductDefinition.interest = UPDATED_INTEREST
//        updatedProductDefinition.flexible = UPDATED_FLEXIBLE
//        updatedProductDefinition.active = UPDATED_ACTIVE
//
//        restProductDefinitionMockMvc.perform(
//            put("/api/product-definitions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(updatedProductDefinition))
//        ).andExpect(status().isOk)
//
//        // Validate the ProductDefinition in the database
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeUpdate)
//        val testProductDefinition = productDefinitionList[productDefinitionList.size - 1]
//        assertThat(testProductDefinition.type).isEqualTo(UPDATED_TYPE)
//        assertThat(testProductDefinition.identifier).isEqualTo(UPDATED_IDENTIFIER)
//        assertThat(testProductDefinition.name).isEqualTo(UPDATED_NAME)
//        assertThat(testProductDefinition.description).isEqualTo(UPDATED_DESCRIPTION)
//        assertThat(testProductDefinition.minimumBalance).isEqualTo(UPDATED_MINIMUM_BALANCE)
//        assertThat(testProductDefinition.equityLedgerIdentifier).isEqualTo(UPDATED_EQUITY_LEDGER_IDENTIFIER)
//        assertThat(testProductDefinition.cashAccountIdentifier).isEqualTo(UPDATED_CASH_ACCOUNT_IDENTIFIER)
//        assertThat(testProductDefinition.expenseAccountIdentifier).isEqualTo(UPDATED_EXPENSE_ACCOUNT_IDENTIFIER)
//        assertThat(testProductDefinition.accrueAccountIdentifier).isEqualTo(UPDATED_ACCRUE_ACCOUNT_IDENTIFIER)
//        assertThat(testProductDefinition.interest).isEqualTo(UPDATED_INTEREST)
//        assertThat(testProductDefinition.flexible).isEqualTo(UPDATED_FLEXIBLE)
//        assertThat(testProductDefinition.active).isEqualTo(UPDATED_ACTIVE)
//    }
//
//    @Test
//    @Transactional
//    fun updateNonExistingProductDefinition() {
//        val databaseSizeBeforeUpdate = productDefinitionRepository.findAll().size
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        restProductDefinitionMockMvc.perform(
//            put("/api/product-definitions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(productDefinitionView))
//        ).andExpect(status().isBadRequest)
//
//        // Validate the ProductDefinition in the database
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeUpdate)
//    }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun deleteProductDefinition() {
//        // Initialize the database
//        productDefinitionRepository.saveAndFlush(productDefinitionView)
//
//        val databaseSizeBeforeDelete = productDefinitionRepository.findAll().size
//
//        // Delete the productDefinition
//        restProductDefinitionMockMvc.perform(
//            delete("/api/product-definitions/{id}", productDefinitionView.id)
//                .accept(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isNoContent)
//
//        // Validate the database contains one less item
//        val productDefinitionList = productDefinitionRepository.findAll()
//        assertThat(productDefinitionList).hasSize(databaseSizeBeforeDelete - 1)
//    }
//
//    companion object {
//
//        private val DEFAULT_TYPE: Type = Type.CHECKING
//        private val UPDATED_TYPE: Type = Type.SAVINGS
//
//        private const val DEFAULT_IDENTIFIER = "AAAAAAAAAA"
//        private const val UPDATED_IDENTIFIER = "BBBBBBBBBB"
//
//        private const val DEFAULT_NAME = "AAAAAAAAAA"
//        private const val UPDATED_NAME = "BBBBBBBBBB"
//
//        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
//        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"
//
//        private const val DEFAULT_MINIMUM_BALANCE: Double = 1.0
//        private const val UPDATED_MINIMUM_BALANCE: Double = 2.0
//
//        private const val DEFAULT_EQUITY_LEDGER_IDENTIFIER = "AAAAAAAAAA"
//        private const val UPDATED_EQUITY_LEDGER_IDENTIFIER = "BBBBBBBBBB"
//
//        private const val DEFAULT_CASH_ACCOUNT_IDENTIFIER = "AAAAAAAAAA"
//        private const val UPDATED_CASH_ACCOUNT_IDENTIFIER = "BBBBBBBBBB"
//
//        private const val DEFAULT_EXPENSE_ACCOUNT_IDENTIFIER = "AAAAAAAAAA"
//        private const val UPDATED_EXPENSE_ACCOUNT_IDENTIFIER = "BBBBBBBBBB"
//
//        private const val DEFAULT_ACCRUE_ACCOUNT_IDENTIFIER = "AAAAAAAAAA"
//        private const val UPDATED_ACCRUE_ACCOUNT_IDENTIFIER = "BBBBBBBBBB"
//
//        private const val DEFAULT_INTEREST: Double = 1.0
//        private const val UPDATED_INTEREST: Double = 2.0
//
//        private const val DEFAULT_FLEXIBLE: Boolean = false
//        private const val UPDATED_FLEXIBLE: Boolean = true
//
//        private const val DEFAULT_ACTIVE: Boolean = false
//        private const val UPDATED_ACTIVE: Boolean = true
//
//        /**
//         * Create an entity for this test.
//         *
//         * This is a static method, as tests for other entities might also need it,
//         * if they test an entity which requires the current entity.
//         */
//        @JvmStatic
//        fun createEntity(em: EntityManager): ProductDefinitionView {
//            val productDefinition = ProductDefinitionView(
//                    type = DEFAULT_TYPE,
//                    identifier = DEFAULT_IDENTIFIER,
//                    name = DEFAULT_NAME,
//                    description = DEFAULT_DESCRIPTION,
//                    minimumBalance = DEFAULT_MINIMUM_BALANCE,
//                    equityLedgerIdentifier = DEFAULT_EQUITY_LEDGER_IDENTIFIER,
//                    cashAccountIdentifier = DEFAULT_CASH_ACCOUNT_IDENTIFIER,
//                    expenseAccountIdentifier = DEFAULT_EXPENSE_ACCOUNT_IDENTIFIER,
//                    accrueAccountIdentifier = DEFAULT_ACCRUE_ACCOUNT_IDENTIFIER,
//                    interest = DEFAULT_INTEREST,
//                    flexible = DEFAULT_FLEXIBLE,
//                    active = DEFAULT_ACTIVE
//            )
//
//            // Add required entity
//            val termView: TermView
//            if (em.findAll(TermView::class).isEmpty()) {
//                termView = TermResourceIT.createEntity(em)
//                em.persist(termView)
//                em.flush()
//            } else {
//                termView = em.findAll(TermView::class).get(0)
//            }
//            productDefinition.term = termView
//            // Add required entity
//            val currency: Currency
//            if (em.findAll(Currency::class).isEmpty()) {
//                currency = CurrencyResourceIT.createEntity(em)
//                em.persist(currency)
//                em.flush()
//            } else {
//                currency = em.findAll(Currency::class).get(0)
//            }
//            productDefinition.currency = currency
//            return productDefinition
//        }
//
//        /**
//         * Create an updated entity for this test.
//         *
//         * This is a static method, as tests for other entities might also need it,
//         * if they test an entity which requires the current entity.
//         */
//        @JvmStatic
//        fun createUpdatedEntity(em: EntityManager): ProductDefinitionView {
//            val productDefinition = ProductDefinitionView(
//                    type = UPDATED_TYPE,
//                    identifier = UPDATED_IDENTIFIER,
//                    name = UPDATED_NAME,
//                    description = UPDATED_DESCRIPTION,
//                    minimumBalance = UPDATED_MINIMUM_BALANCE,
//                    equityLedgerIdentifier = UPDATED_EQUITY_LEDGER_IDENTIFIER,
//                    cashAccountIdentifier = UPDATED_CASH_ACCOUNT_IDENTIFIER,
//                    expenseAccountIdentifier = UPDATED_EXPENSE_ACCOUNT_IDENTIFIER,
//                    accrueAccountIdentifier = UPDATED_ACCRUE_ACCOUNT_IDENTIFIER,
//                    interest = UPDATED_INTEREST,
//                    flexible = UPDATED_FLEXIBLE,
//                    active = UPDATED_ACTIVE
//            )
//
//            // Add required entity
//            val termView: TermView
//            if (em.findAll(TermView::class).isEmpty()) {
//                termView = TermResourceIT.createUpdatedEntity(em)
//                em.persist(termView)
//                em.flush()
//            } else {
//                termView = em.findAll(TermView::class).get(0)
//            }
//            productDefinition.term = termView
//            // Add required entity
//            val currency: Currency
//            if (em.findAll(Currency::class).isEmpty()) {
//                currency = CurrencyResourceIT.createUpdatedEntity(em)
//                em.persist(currency)
//                em.flush()
//            } else {
//                currency = em.findAll(Currency::class).get(0)
//            }
//            productDefinition.currency = currency
//            return productDefinition
//        }
//    }
// }
