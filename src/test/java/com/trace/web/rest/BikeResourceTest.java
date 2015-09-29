package com.trace.web.rest;

import com.trace.Application;
import com.trace.domain.Bike;
import com.trace.repository.BikeRepository;

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
 * Test class for the BikeResource REST controller.
 *
 * @see BikeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class BikeResourceTest {

    private static final String DEFAULT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_MODELNO = "SAMPLE_TEXT";
    private static final String UPDATED_MODELNO = "UPDATED_TEXT";

    @Inject
    private BikeRepository bikeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restBikeMockMvc;

    private Bike bike;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BikeResource bikeResource = new BikeResource();
        ReflectionTestUtils.setField(bikeResource, "bikeRepository", bikeRepository);
        this.restBikeMockMvc = MockMvcBuilders.standaloneSetup(bikeResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        bike = new Bike();
        bike.setType(DEFAULT_TYPE);
        bike.setModelno(DEFAULT_MODELNO);
    }

    @Test
    @Transactional
    public void createBike() throws Exception {
        int databaseSizeBeforeCreate = bikeRepository.findAll().size();

        // Create the Bike

        restBikeMockMvc.perform(post("/api/bikes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bike)))
                .andExpect(status().isCreated());

        // Validate the Bike in the database
        List<Bike> bikes = bikeRepository.findAll();
        assertThat(bikes).hasSize(databaseSizeBeforeCreate + 1);
        Bike testBike = bikes.get(bikes.size() - 1);
        assertThat(testBike.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBike.getModelno()).isEqualTo(DEFAULT_MODELNO);
    }

    @Test
    @Transactional
    public void getAllBikes() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

        // Get all the bikes
        restBikeMockMvc.perform(get("/api/bikes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bike.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].modelno").value(hasItem(DEFAULT_MODELNO.toString())));
    }

    @Test
    @Transactional
    public void getBike() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

        // Get the bike
        restBikeMockMvc.perform(get("/api/bikes/{id}", bike.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(bike.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.modelno").value(DEFAULT_MODELNO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBike() throws Exception {
        // Get the bike
        restBikeMockMvc.perform(get("/api/bikes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBike() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

		int databaseSizeBeforeUpdate = bikeRepository.findAll().size();

        // Update the bike
        bike.setType(UPDATED_TYPE);
        bike.setModelno(UPDATED_MODELNO);
        

        restBikeMockMvc.perform(put("/api/bikes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bike)))
                .andExpect(status().isOk());

        // Validate the Bike in the database
        List<Bike> bikes = bikeRepository.findAll();
        assertThat(bikes).hasSize(databaseSizeBeforeUpdate);
        Bike testBike = bikes.get(bikes.size() - 1);
        assertThat(testBike.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBike.getModelno()).isEqualTo(UPDATED_MODELNO);
    }

    @Test
    @Transactional
    public void deleteBike() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

		int databaseSizeBeforeDelete = bikeRepository.findAll().size();

        // Get the bike
        restBikeMockMvc.perform(delete("/api/bikes/{id}", bike.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Bike> bikes = bikeRepository.findAll();
        assertThat(bikes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
