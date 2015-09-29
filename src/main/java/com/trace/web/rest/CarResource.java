package com.trace.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.trace.domain.Car;
import com.trace.repository.CarRepository;
import com.trace.web.rest.util.HeaderUtil;
import com.trace.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Car.
 */
@RestController
@RequestMapping("/api")
public class CarResource {

    private final Logger log = LoggerFactory.getLogger(CarResource.class);

    @Inject
    private CarRepository carRepository;

    /**
     * POST  /cars -> Create a new car.
     */
    @RequestMapping(value = "/cars",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Car> create(@RequestBody Car car) throws URISyntaxException {
        log.debug("REST request to save Car : {}", car);
        if (car.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new car cannot already have an ID").body(null);
        }
        Car result = carRepository.save(car);
        return ResponseEntity.created(new URI("/api/cars/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("car", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /cars -> Updates an existing car.
     */
    @RequestMapping(value = "/cars",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Car> update(@RequestBody Car car) throws URISyntaxException {
        log.debug("REST request to update Car : {}", car);
        if (car.getId() == null) {
            return create(car);
        }
        Car result = carRepository.save(car);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("car", car.getId().toString()))
                .body(result);
    }

    /**
     * GET  /cars -> get all the cars.
     */
    @RequestMapping(value = "/cars",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Car>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Car> page = carRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cars", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /cars/:id -> get the "id" car.
     */
    @RequestMapping(value = "/cars/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Car> get(@PathVariable Long id) {
        log.debug("REST request to get Car : {}", id);
        return Optional.ofNullable(carRepository.findOne(id))
            .map(car -> new ResponseEntity<>(
                car,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /cars/:id -> delete the "id" car.
     */
    @RequestMapping(value = "/cars/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Car : {}", id);
        carRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("car", id.toString())).build();
    }
}
