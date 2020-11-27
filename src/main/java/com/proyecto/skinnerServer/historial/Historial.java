package com.proyecto.skinnerServer.historial;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import helper.Helper;

@RestController
//@RequestMapping(path="/")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})

public class Historial {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 
	/*
	@GetMapping("historial/{id}")
	public List<Map<String,Object>> getHistoriales(){
		String sql = "SELECT * FROM historial_lesion";
		return jdbcTemplate.queryForList(sql);
	}*/
	
	@GetMapping("/historial/{id}")
	public List<Map<String,Object>> getHistorialPorId(@PathVariable("id") long id){
		String sql = "SELECT * FROM historial_lesion WHERE id= %d order by fecha desc";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/historial/lesion/{id}")
	public List<Map<String,Object>> getHistorialPorIdLesion(@PathVariable("id") long id){
		String sql = "SELECT * FROM historial_lesion WHERE id_lesion = %d order by fecha desc";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	
	@PutMapping("/historial/{id}")
		public Map<String,Object> editHistorial(@RequestBody Map<String,Object> historialData, @PathVariable("id") long id){
			Map<String, Object> historial = new HashMap<String, Object>();
			String sql = "SELECT * FROM historial_lesion WHERE id= %d";
			sql = String.format(sql, id);
			historial = jdbcTemplate.queryForMap(sql);
			sql = "UPDATE historial_lesion SET id_doctor = %d, analisis = '%s' WHERE id = %d RETURNING *";
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, Object> oldAnalisis = objectMapper.readValue(historial.get("analisis").toString(), Map.class);
				Map<String, Object> newAnalisis = new HashMap<String, Object>();
				if(historialData.get("analisis") != null) {
					newAnalisis = objectMapper.readValue(historialData.get("analisis").toString(), Map.class);
				}
				if(newAnalisis.containsKey("diametro")) {
					oldAnalisis.put("diametro", newAnalisis.get("diametro"));
					
				}else if(newAnalisis.containsKey("palmas")){
					oldAnalisis.put("palmas", newAnalisis.get("palmas"));
				}
				String analisisFinal = objectMapper.writeValueAsString(oldAnalisis);
				sql = String.format(sql, historialData.get("id_doctor"), analisisFinal, id);
				jdbcTemplate.queryForList(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
	        map.put("status", 200);
	        return map;
	}
	
	@PostMapping("/historial")
	public Map<String,Object> insertHistorial(@RequestBody Map<String,Object> historialData){
		String result = "{}";
		if(historialData.get("id_tipo").equals(1) || historialData.get("id_tipo").equals(4)) {
		 result = Helper.analizarCaracteristicas(historialData.get("imagen").toString());
		}
		System.out.println(result.toString());
		String sql = "INSERT INTO historial_lesion (id_lesion, id_doctor, descripcion, imagen, fecha, analisis) VALUES(%d, %d, '%s', '%s', '%s', '%s') RETURNING id"; 
		sql = String.format(sql, historialData.get("id_lesion"),  historialData.get("id_doctor"),  historialData.get("descripcion"),  historialData.get("imagen"),  historialData.get("fecha"), result.toString());
		Map<String, Object> historial = jdbcTemplate.queryForMap(sql);
		if(historialData.get("id_tipo").equals(1) || historialData.get("id_tipo").equals(4)) {
		 	String queryAdicionales = Helper.agregarAdicionales((int)historial.get("id"), historialData.get("imagen").toString());
			if(!queryAdicionales.equals("")) {
				jdbcTemplate.update(queryAdicionales);			
			}
		}
		
		return historial;
	}
	
	@DeleteMapping("/historial/{id}")
	public Map<String,Object> deleteHistorial(@PathVariable("id") long id){
		String sql = "UPDATE historial_lesion SET activo = false WHERE id = %d;";
		sql = String.format(sql, id);
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        return map;
	}
}
