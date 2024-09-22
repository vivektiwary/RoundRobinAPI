package com.vivektiwary.SimpleAPI.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SimpleController {

	@PostMapping()
	public Map<String, Object> echoItem(@RequestBody Map<String, Object> item) {
    return item;
  }
}
