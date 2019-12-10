package com.nga.xtendhr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nga.xtendhr.model.Country;
import com.nga.xtendhr.service.CountryService;

@RestController
@RequestMapping("/DBDataRetriveSample/api")
public class DBDataRetriveSample {
	@Autowired
	CountryService countryService;

	@GetMapping(value = "/getCountries")
	public List<Country> getCountries() {
		return countryService.findAll();
	}
}
