package com.trace.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.trace.domain.Driver;
import com.trace.repository.DriverRepository;
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
 * REST controller for managing Driver.
 */
@RestController
@RequestMapping("/api")
public class DriverResource {

    private final Logger log = LoggerFactory.getLogger(DriverResource.class);

    @Inject
    private DriverRepository driverRepository;

    /**
     * POST  /drivers -> Create a new driver.
     */
    @RequestMapping(value = "/drivers",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Driver> create(@RequestBody Driver driver) throws URISyntaxException {
        log.debug("REST request to save Driver : {}", driver);
        if (driver.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new driver cannot already have an ID").body(null);
        }
        Driver result = driverRepository.save(driver);
        return ResponseEntity.created(new URI("/api/drivers/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("driver", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /drivers -> Updates an existing driver.
     */
    @RequestMapping(value = "/drivers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Driver> update(@RequestBody Driver driver) throws URISyntaxException {
        log.debug("REST request to update Driver : {}", driver);
        if (driver.getId() == null) {
            return create(driver);
        }
        Driver result = driverRepository.save(driver);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("driver", driver.getId().toString()))
                .body(result);
    }

    /**
     * GET  /drivers -> get all the drivers.
     */
    @RequestMapping(value = "/drivers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Driver>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Driver> page = driverRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/drivers", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /drivers/:id -> get the "id" driver.
     */
    @RequestMapping(value = "/drivers/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Driver> get(@PathVariable Long id) {
        log.debug("REST request to get Driver : {}", id);
        return Optional.ofNullable(driverRepository.findOne(id))
            .map(driver -> new ResponseEntity<>(
                driver,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /drivers/:id -> delete the "id" driver.
     */
    @RequestMapping(value = "/drivers/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Driver : {}", id);
        driverRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("driver", id.toString())).build();
    }
}
