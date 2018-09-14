package com.customeranalytics.web.rest;

import com.customeranalytics.CustomeranalyticsApp;

import com.customeranalytics.domain.Stuff;
import com.customeranalytics.repository.StuffRepository;
import com.customeranalytics.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.customeranalytics.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StuffResource REST controller.
 *
 * @see StuffResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomeranalyticsApp.class)
public class StuffResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Long DEFAULT_PHONE = 1L;
    private static final Long UPDATED_PHONE = 2L;

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Autowired
    private StuffRepository stuffRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restStuffMockMvc;

    private Stuff stuff;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StuffResource stuffResource = new StuffResource(stuffRepository);
        this.restStuffMockMvc = MockMvcBuilders.standaloneSetup(stuffResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stuff createEntity(EntityManager em) {
        Stuff stuff = new Stuff()
            .name(DEFAULT_NAME)
            .surname(DEFAULT_SURNAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return stuff;
    }

    @Before
    public void initTest() {
        stuff = createEntity(em);
    }

    @Test
    @Transactional
    public void createStuff() throws Exception {
        int databaseSizeBeforeCreate = stuffRepository.findAll().size();

        // Create the Stuff
        restStuffMockMvc.perform(post("/api/stuffs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stuff)))
            .andExpect(status().isCreated());

        // Validate the Stuff in the database
        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeCreate + 1);
        Stuff testStuff = stuffList.get(stuffList.size() - 1);
        assertThat(testStuff.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStuff.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testStuff.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testStuff.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testStuff.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testStuff.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createStuffWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = stuffRepository.findAll().size();

        // Create the Stuff with an existing ID
        stuff.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStuffMockMvc.perform(post("/api/stuffs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stuff)))
            .andExpect(status().isBadRequest());

        // Validate the Stuff in the database
        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = stuffRepository.findAll().size();
        // set the field null
        stuff.setName(null);

        // Create the Stuff, which fails.

        restStuffMockMvc.perform(post("/api/stuffs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stuff)))
            .andExpect(status().isBadRequest());

        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSurnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = stuffRepository.findAll().size();
        // set the field null
        stuff.setSurname(null);

        // Create the Stuff, which fails.

        restStuffMockMvc.perform(post("/api/stuffs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stuff)))
            .andExpect(status().isBadRequest());

        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkImageIsRequired() throws Exception {
        int databaseSizeBeforeTest = stuffRepository.findAll().size();
        // set the field null
        stuff.setImage(null);

        // Create the Stuff, which fails.

        restStuffMockMvc.perform(post("/api/stuffs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stuff)))
            .andExpect(status().isBadRequest());

        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStuffs() throws Exception {
        // Initialize the database
        stuffRepository.saveAndFlush(stuff);

        // Get all the stuffList
        restStuffMockMvc.perform(get("/api/stuffs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stuff.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getStuff() throws Exception {
        // Initialize the database
        stuffRepository.saveAndFlush(stuff);

        // Get the stuff
        restStuffMockMvc.perform(get("/api/stuffs/{id}", stuff.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(stuff.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingStuff() throws Exception {
        // Get the stuff
        restStuffMockMvc.perform(get("/api/stuffs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStuff() throws Exception {
        // Initialize the database
        stuffRepository.saveAndFlush(stuff);
        int databaseSizeBeforeUpdate = stuffRepository.findAll().size();

        // Update the stuff
        Stuff updatedStuff = stuffRepository.findOne(stuff.getId());
        // Disconnect from session so that the updates on updatedStuff are not directly saved in db
        em.detach(updatedStuff);
        updatedStuff
            .name(UPDATED_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restStuffMockMvc.perform(put("/api/stuffs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedStuff)))
            .andExpect(status().isOk());

        // Validate the Stuff in the database
        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeUpdate);
        Stuff testStuff = stuffList.get(stuffList.size() - 1);
        assertThat(testStuff.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStuff.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testStuff.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testStuff.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testStuff.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testStuff.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingStuff() throws Exception {
        int databaseSizeBeforeUpdate = stuffRepository.findAll().size();

        // Create the Stuff

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restStuffMockMvc.perform(put("/api/stuffs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(stuff)))
            .andExpect(status().isCreated());

        // Validate the Stuff in the database
        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteStuff() throws Exception {
        // Initialize the database
        stuffRepository.saveAndFlush(stuff);
        int databaseSizeBeforeDelete = stuffRepository.findAll().size();

        // Get the stuff
        restStuffMockMvc.perform(delete("/api/stuffs/{id}", stuff.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Stuff> stuffList = stuffRepository.findAll();
        assertThat(stuffList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stuff.class);
        Stuff stuff1 = new Stuff();
        stuff1.setId(1L);
        Stuff stuff2 = new Stuff();
        stuff2.setId(stuff1.getId());
        assertThat(stuff1).isEqualTo(stuff2);
        stuff2.setId(2L);
        assertThat(stuff1).isNotEqualTo(stuff2);
        stuff1.setId(null);
        assertThat(stuff1).isNotEqualTo(stuff2);
    }
}
