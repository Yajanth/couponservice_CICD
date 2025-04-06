package com.tus.coupon.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tus.coupon.model.Coupon;
import com.tus.coupon.repo.CouponRepo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/couponapi")
public class CouponRestController {
//comment for version control
	@Autowired
	CouponRepo repo;
    private static final Logger logger = LoggerFactory.getLogger(CouponRestController.class);


	// this is called constructor based injection.
	// you can now use normal mocks try
	// setter injection

	@PostMapping(value = "/coupons")
	public ResponseEntity<Coupon> create(@RequestBody Coupon coupon) {
		return new ResponseEntity<>(repo.save(coupon), HttpStatus.OK);
	}

	@GetMapping("/coupons/{code}")
	Coupon getCouponByCouponCode(@PathVariable String code) {
		 logger.info(code);
		return repo.findByCode(code);
	}

	@GetMapping(value = "/coupons")
	public List<Coupon> getAllCoupons() {
		return repo.findAll();

	}


    }

