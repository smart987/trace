package com.trace.web.rest;

import com.trace.Application;
import com.trace.domain.Driver;
import com.trace.repository.DriverRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the DriverResource REST controller.
 *
 * @see DriverResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DriverResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Boolean DEFAULT_GENDER = false;
    private static final Boolean UPDATED_GENDER = true;

    @Inject
    private DriverRepository driverRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restDriverMockMvc;

    private Driver driver;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DriverResource driverResource = new DriverResource();
        ReflectionTestUtils.setField(driverResource, "driverRepository", driverRepository);
        this.restDriverMockMvc = MockMvcBuilders.standaloneSetup(driverResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        driver = new Driver();
        driver.setName(DEFAULT_NAME);
        driver.setGender(DEFAULT_GENDER);
    }

    @Test
    @Transactional
    public void createDriver() throws Exception {
        int databaseSizeBeforeCreate = driverRepository.findAll().size();

        // Create the Driver

        restDriverMockMvc.perform(post("/api/drivers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(driver)))
                .andExpect(status().isCreated());

        // Validate the Driver in the database
        List<Driver> drivers = driverRepository.findAll();
        assertThat(drivers).hasSize(databaseSizeBeforeCreate + 1);
        Driver testDriver = drivers.get(drivers.size() - 1);
        assertThat(testDriver.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDriver.getGender()).isEqualTo(DEFAULT_GENDER);
    }

    @Test
    @Transactional
    public void getAllDrivers() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        // Get all the drivers
        restDriverMockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(driver.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.booleanValue())));
    }

    @Test
    @Transactional
    public void getDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

        // Get the driver
        restDriverMockMvc.perform(get("/api/drivers/{id}", driver.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(driver.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDriver() throws Exception {
        // Get the driver
        restDriverMockMvc.perform(get("/api/drivers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

		int databaseSizeBeforeUpdate = driverRepository.findAll().size();

        // Update the driver
        driver.setName(UPDATED_NAME);
        driver.setGender(UPDATED_GENDER);
        

        restDriverMockMvc.perform(put("/api/drivers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(driver)))
                .andExpect(status().isOk());

        // Validate the Driver in the database
        List<Driver> drivers = driverRepository.findAll();
        assertThat(drivers).hasSize(databaseSizeBeforeUpdate);
        Driver testDriver = drivers.get(drivers.size() - 1);
        assertThat(testDriver.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDriver.getGender()).isEqualTo(UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void deleteDriver() throws Exception {
        // Initialize the database
        driverRepository.saveAndFlush(driver);

		int databaseSizeBeforeDelete = driverRepository.findAll().size();

        // Get the driver
        restDriverMockMvc.perform(delete("/api/drivers/{id}", driver.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Driver> drivers = driverRepository.findAll();
        assertThat(drivers).hasSize(databaseSizeBeforeDelete - 1);
    }
}
