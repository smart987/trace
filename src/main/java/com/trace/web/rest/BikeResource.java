package com.trace.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.trace.domain.Bike;
import com.trace.repository.BikeRepository;
import com.trace.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Bike.
 */
@RestController
@RequestMapping("/api")
public class BikeResource {

    private final Logger log = LoggerFactory.getLogger(BikeResource.class);

    @Inject
    private BikeRepository bikeRepository;

    /**
     * POST  /bikes -> Create a new bike.
     */
    @RequestMapping(value = "/bikes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Bike> create(@RequestBody Bike bike) throws URISyntaxException {
        log.debug("REST request to save Bike : {}", bike);
        if (bike.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new bike cannot already have an ID").body(null);
        }
        Bike result = bikeRepository.save(bike);
        return ResponseEntity.created(new URI("/api/bikes/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("bike", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /bikes -> Updates an existing bike.
     */
    @RequestMapping(value = "/bikes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Bike> update(@RequestBody Bike bike) throws URISyntaxException {
        log.debug("REST request to update Bike : {}", bike);
        if (bike.getId() == null) {
            return create(bike);
        }
        Bike result = bikeRepository.save(bike);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("bike", bike.getId().toString()))
                .body(result);
    }

    /**
     * GET  /bikes -> get all the bikes.
     */
    @RequestMapping(value = "/bikes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Bike> getAll() {
        log.debug("REST request to get all Bikes");
        return bikeRepository.findAllWithEagerRelationships();
    }

    /**
     * GET  /bikes/:id -> get the "id" bike.
     */
    @RequestMapping(value = "/bikes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Bike> get(@PathVariable Long id) {
        log.debug("REST request to get Bike : {}", id);
        return Optional.ofNullable(bikeRepository.findOneWithEagerRelationships(id))
            .map(bike -> new ResponseEntity<>(
                bike,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bikes/:id -> delete the "id" bike.
     */
    @RequestMapping(value = "/bikes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Bike : {}", id);
        bikeRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bike", id.toString())).build();
    }
}
