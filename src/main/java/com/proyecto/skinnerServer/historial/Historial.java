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

@RestController
//@RequestMapping(path="/")
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
		String sql = "SELECT * FROM historial_lesion WHERE id= %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/historial/lesion/{id}")
	public List<Map<String,Object>> getHistorialPorIdLesion(@PathVariable("id") long id){
		String sql = "SELECT * FROM historial_lesion WHERE id_lesion = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	
	@PutMapping("/historial/{id}")
		public Map<String,Object> editHistorial(@RequestBody Map<String,Object> historialData, @PathVariable("id") long id){
			String sql = "UPDATE historial_lesion SET id_doctor = %d, descripcion = '%s', imagen = '%s', fecha = '%s' WHERE id = %d RETURNING *";
			sql = String.format(sql, historialData.get("id_doctor"),  historialData.get("descripcion"),  historialData.get("imagen"),  historialData.get("fecha"), id);
			jdbcTemplate.queryForList(sql);
			Map<String, Object> map = new HashMap<String, Object>();
	        map.put("status", 200);
	        return map;
	}
	
	@PostMapping("/historial")
	public List<Map<String,Object>> insertHistorial(@RequestBody Map<String,Object> historialData){
		String sql = "INSERT INTO historial_lesion (id_lesion, id_doctor, descripcion, imagen, fecha) VALUES(%d, %d, '%s', '%s', '%s') RETURNING id"; 
		sql = String.format(sql, historialData.get("id_lesion"),  historialData.get("id_doctor"),  historialData.get("descripcion"),  historialData.get("imagen"),  historialData.get("fecha"));
		return jdbcTemplate.queryForList(sql);
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
