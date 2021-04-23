package com.dhirendra.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The Abstract base class to be sub-classed in all the Controllers. It defines
 * common functionalities needed in all the Controllers
 *
 */

public class AbstractBaseRestController {

	protected ResponseEntity<?> createResponse(Object obj, HttpStatus status) {
		return new ResponseEntity<Object>(obj, status);
	}

	protected ResponseEntity<?> createResponse(HttpStatus status) {
		return new ResponseEntity<Object>(status);
	}

	protected ResponseEntity<?> createResponse(HttpHeaders httpheader, HttpStatus status) {
		return new ResponseEntity<Object>(httpheader, status);
	}

}
