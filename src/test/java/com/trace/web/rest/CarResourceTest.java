package com.trace.web.rest;

import com.trace.Application;
import com.trace.domain.Car;
import com.trace.repository.CarRepository;

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
 * Test class for the CarResource REST controller.
 *
 * @see CarResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CarResourceTest {

    private static final String DEFAULT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_TYPE = "UPDATED_TEXT";

    private static final Integer DEFAULT_SEATS = 1;
    private static final Integer UPDATED_SEATS = 2;

    @Inject
    private CarRepository carRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restCarMockMvc;

    private Car car;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CarResource carResource = new CarResource();
        ReflectionTestUtils.setField(carResource, "carRepository", carRepository);
        this.restCarMockMvc = MockMvcBuilders.standaloneSetup(carResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        car = new Car();
        car.setType(DEFAULT_TYPE);
        car.setSeats(DEFAULT_SEATS);
    }

    @Test
    @Transactional
    public void createCar() throws Exception {
        int databaseSizeBeforeCreate = carRepository.findAll().size();

        // Create the Car

        restCarMockMvc.perform(post("/api/cars")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(car)))
                .andExpect(status().isCreated());

        // Validate the Car in the database
        List<Car> cars = carRepository.findAll();
        assertThat(cars).hasSize(databaseSizeBeforeCreate + 1);
        Car testCar = cars.get(cars.size() - 1);
        assertThat(testCar.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCar.getSeats()).isEqualTo(DEFAULT_SEATS);
    }

    @Test
    @Transactional
    public void getAllCars() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get all the cars
        restCarMockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].seats").value(hasItem(DEFAULT_SEATS)));
    }

    @Test
    @Transactional
    public void getCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get the car
        restCarMockMvc.perform(get("/api/cars/{id}", car.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(car.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.seats").value(DEFAULT_SEATS));
    }

    @Test
    @Transactional
    public void getNonExistingCar() throws Exception {
        // Get the car
        restCarMockMvc.perform(get("/api/cars/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

		int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car
        car.setType(UPDATED_TYPE);
        car.setSeats(UPDATED_SEATS);
        

        restCarMockMvc.perform(put("/api/cars")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(car)))
                .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> cars = carRepository.findAll();
        assertThat(cars).hasSize(databaseSizeBeforeUpdate);
        Car testCar = cars.get(cars.size() - 1);
        assertThat(testCar.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCar.getSeats()).isEqualTo(UPDATED_SEATS);
    }

    @Test
    @Transactional
    public void deleteCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

		int databaseSizeBeforeDelete = carRepository.findAll().size();

        // Get the car
        restCarMockMvc.perform(delete("/api/cars/{id}", car.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Car> cars = carRepository.findAll();
        assertThat(cars).hasSize(databaseSizeBeforeDelete - 1);
    }
}
