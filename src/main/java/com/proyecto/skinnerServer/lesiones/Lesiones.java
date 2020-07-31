package com.proyecto.skinnerServer.lesiones;

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
public class Lesiones {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 
	
	@GetMapping("/lesiones")
	public List<Map<String,Object>> getLesiones(){
		String sql = "SELECT * FROM lesiones";
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/lesiones/paciente/{id}")
	public List<Map<String,Object>> getLesionesPorPaciente(@PathVariable("id") long id){
		String sql = "SELECT * FROM lesiones WHERE id_paciente = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/lesiones/{id}")
	public List<Map<String,Object>> getLesionPorId(@PathVariable("id") long id){
		String sql = "SELECT * FROM lesiones WHERE id = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	@PutMapping("/lesiones/{id}")
		public Map<String,Object> editLesion(@RequestBody Map<String,Object> lesionData, @PathVariable("id") long id){
		String sql = "UPDATE public.lesiones SET id_paciente = %d, id_doctor = %d, descripcion = '%s', id_tipo = %d, ubicacion = '%s', fecha_creacion = '%s' WHERE id = %d RETURNING *";
		sql = String.format(sql, lesionData.get("id_paciente"),  lesionData.get("id_doctor"),  lesionData.get("descripcion"),  lesionData.get("id_tipo"),  lesionData.get("ubicacion"),  lesionData.get("fecha_creacion"), id);
		jdbcTemplate.queryForList(sql);
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        return map;
	}
	
	@PostMapping("/lesiones")
	public List<Map<String,Object>> insertLesion(@RequestBody Map<String,Object> lesionData){
	String sql = "INSERT INTO public.lesiones (id_paciente, id_doctor, descripcion, id_tipo, ubicacion, fecha_creacion) "+
			"VALUES(%d, %d, '%s', %d, '%s', '%s') RETURNING id;"; 
	sql = String.format(sql, lesionData.get("id_paciente"),  lesionData.get("id_doctor"),  lesionData.get("descripcion"),  lesionData.get("id_tipo"),  lesionData.get("ubicacion"),  lesionData.get("fecha_creacion"));
	return jdbcTemplate.queryForList(sql);
	}
	
	@DeleteMapping("/lesiones/{id}")
	public Map<String,Object> deleteLesion(@PathVariable("id") long id){
		String sql = "UPDATE public.lesiones SET activo = false WHERE id = %d;";
		sql = String.format(sql, id);
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        return map;
	}
}
