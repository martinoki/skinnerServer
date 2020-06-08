package com.proyecto.skinnerServer.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping(path="/")
public class RestData {
	
	
	@GetMapping("/")
	public Map<String, Object> greeting() {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        map.put("message", "RECIBI DEL BACK DE SPRING; STA BIEN?");
        return map;
//		return "Son 5, chicos... sta bien? y los MP? ehh? sta bien? y si yo me saco la foto adentro del baño? sin luz? sta bien? anda?";
	}
	
	@PostMapping("/AnalizarImagen")
	public Map<String, Object> analize(@RequestBody String image) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        map.put("message", "RECIBI DEL BACK DE SPRING; STA BIEN?");
        return map;
//		return "Son 5, chicos... sta bien? y los MP? ehh? sta bien? y si yo me saco la foto adentro del baño? sin luz? sta bien? anda?";
	}
}
