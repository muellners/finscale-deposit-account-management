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
// import org.muellners.finscale.deposit.repository.ActionViewRepository
// import org.muellners.finscale.deposit.views.Action
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
// * Integration tests for the [ActionResource] REST controller.
// *
// * @see ActionResource
// */
// @SpringBootTest(classes = [SecurityBeanOverrideConfiguration::class, DepositAccountManagementApp::class])
// @AutoConfigureMockMvc
// @WithMockUser
// class ActionResourceIT {
//
//    @Autowired
//    private lateinit var actionViewRepository: ActionViewRepository
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
//    private lateinit var restActionMockMvc: MockMvc
//
//    private lateinit var action: Action
//
//    @BeforeEach
//    fun setup() {
//        MockitoAnnotations.initMocks(this)
//        val actionResource = ActionResource(actionViewRepository)
//         this.restActionMockMvc = MockMvcBuilders.standaloneSetup(actionResource)
//             .setCustomArgumentResolvers(pageableArgumentResolver)
//             .setControllerAdvice(exceptionTranslator)
//             .setConversionService(createFormattingConversionService())
//             .setMessageConverters(jacksonMessageConverter)
//             .setValidator(validator).build()
//    }
//
//    @BeforeEach
//    fun initTest() {
//        action = createEntity(em)
//    }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun createAction() {
//        val databaseSizeBeforeCreate = actionViewRepository.findAll().size
//
//        // Create the Action
//        restActionMockMvc.perform(
//            post("/api/actions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(action))
//        ).andExpect(status().isCreated)
//
//        // Validate the Action in the database
//        val actionList = actionViewRepository.findAll()
//        assertThat(actionList).hasSize(databaseSizeBeforeCreate + 1)
//        val testAction = actionList[actionList.size - 1]
//        assertThat(testAction.identifier).isEqualTo(DEFAULT_IDENTIFIER)
//        assertThat(testAction.name).isEqualTo(DEFAULT_NAME)
//        assertThat(testAction.description).isEqualTo(DEFAULT_DESCRIPTION)
//        assertThat(testAction.transactionType).isEqualTo(DEFAULT_TRANSACTION_TYPE)
//    }
//
//    @Test
//    @Transactional
//    fun createActionWithExistingId() {
//        val databaseSizeBeforeCreate = actionViewRepository.findAll().size
//
//        // Create the Action with an existing ID
//        action.id = 1L
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restActionMockMvc.perform(
//            post("/api/actions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(action))
//        ).andExpect(status().isBadRequest)
//
//        // Validate the Action in the database
//        val actionList = actionViewRepository.findAll()
//        assertThat(actionList).hasSize(databaseSizeBeforeCreate)
//    }
//
//    @Test
//    @Transactional
//    fun checkNameIsRequired() {
//        val databaseSizeBeforeTest = actionViewRepository.findAll().size
//        // set the field null
//        action.name = null
//
//        // Create the Action, which fails.
//
//        restActionMockMvc.perform(
//            post("/api/actions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(action))
//        ).andExpect(status().isBadRequest)
//
//        val actionList = actionViewRepository.findAll()
//        assertThat(actionList).hasSize(databaseSizeBeforeTest)
//    }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun getAllActions() {
//        // Initialize the database
//        actionViewRepository.saveAndFlush(action)
//
//        // Get all the actionList
//        restActionMockMvc.perform(get("/api/actions?sort=id,desc"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(action.id?.toInt())))
//            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER)))
//            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
//            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
//            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE))) }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun getAction() {
//        // Initialize the database
//        actionViewRepository.saveAndFlush(action)
//
//        val id = action.id
//        assertNotNull(id)
//
//        // Get the action
//        restActionMockMvc.perform(get("/api/actions/{id}", id))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.id").value(action.id?.toInt()))
//            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER))
//            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
//            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
//            .andExpect(jsonPath("$.transactionType").value(DEFAULT_TRANSACTION_TYPE)) }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun getNonExistingAction() {
//        // Get the action
//        restActionMockMvc.perform(get("/api/actions/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound)
//    }
//    @Test
//    @Transactional
//    fun updateAction() {
//        // Initialize the database
//        actionViewRepository.saveAndFlush(action)
//
//        val databaseSizeBeforeUpdate = actionViewRepository.findAll().size
//
//        // Update the action
//        val id = action.id
//        assertNotNull(id)
//        val updatedAction = actionViewRepository.findById(id).get()
//        // Disconnect from session so that the updates on updatedAction are not directly saved in db
//        em.detach(updatedAction)
//        updatedAction.identifier = UPDATED_IDENTIFIER
//        updatedAction.name = UPDATED_NAME
//        updatedAction.description = UPDATED_DESCRIPTION
//        updatedAction.transactionType = UPDATED_TRANSACTION_TYPE
//
//        restActionMockMvc.perform(
//            put("/api/actions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(updatedAction))
//        ).andExpect(status().isOk)
//
//        // Validate the Action in the database
//        val actionList = actionViewRepository.findAll()
//        assertThat(actionList).hasSize(databaseSizeBeforeUpdate)
//        val testAction = actionList[actionList.size - 1]
//        assertThat(testAction.identifier).isEqualTo(UPDATED_IDENTIFIER)
//        assertThat(testAction.name).isEqualTo(UPDATED_NAME)
//        assertThat(testAction.description).isEqualTo(UPDATED_DESCRIPTION)
//        assertThat(testAction.transactionType).isEqualTo(UPDATED_TRANSACTION_TYPE)
//    }
//
//    @Test
//    @Transactional
//    fun updateNonExistingAction() {
//        val databaseSizeBeforeUpdate = actionViewRepository.findAll().size
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        restActionMockMvc.perform(
//            put("/api/actions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(action))
//        ).andExpect(status().isBadRequest)
//
//        // Validate the Action in the database
//        val actionList = actionViewRepository.findAll()
//        assertThat(actionList).hasSize(databaseSizeBeforeUpdate)
//    }
//
//    @Test
//    @Transactional
//    @Throws(Exception::class)
//    fun deleteAction() {
//        // Initialize the database
//        actionViewRepository.saveAndFlush(action)
//
//        val databaseSizeBeforeDelete = actionViewRepository.findAll().size
//
//        // Delete the action
//        restActionMockMvc.perform(
//            delete("/api/actions/{id}", action.id)
//                .accept(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isNoContent)
//
//        // Validate the database contains one less item
//        val actionList = actionViewRepository.findAll()
//        assertThat(actionList).hasSize(databaseSizeBeforeDelete - 1)
//    }
//
//    companion object {
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
//        private const val DEFAULT_TRANSACTION_TYPE = "AAAAAAAAAA"
//        private const val UPDATED_TRANSACTION_TYPE = "BBBBBBBBBB"
//
//        /**
//         * Create an entity for this test.
//         *
//         * This is a static method, as tests for other entities might also need it,
//         * if they test an entity which requires the current entity.
//         */
//        @JvmStatic
//        fun createEntity(em: EntityManager): Action {
//            val action = Action(
//                identifier = DEFAULT_IDENTIFIER,
//                name = DEFAULT_NAME,
//                description = DEFAULT_DESCRIPTION,
//                transactionType = DEFAULT_TRANSACTION_TYPE
//            )
//
//            return action
//        }
//
//        /**
//         * Create an updated entity for this test.
//         *
//         * This is a static method, as tests for other entities might also need it,
//         * if they test an entity which requires the current entity.
//         */
//        @JvmStatic
//        fun createUpdatedEntity(em: EntityManager): Action {
//            val action = Action(
//                identifier = UPDATED_IDENTIFIER,
//                name = UPDATED_NAME,
//                description = UPDATED_DESCRIPTION,
//                transactionType = UPDATED_TRANSACTION_TYPE
//            )
//
//            return action
//        }
//    }
// }
