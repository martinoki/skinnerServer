package com.proyecto.skinnerServer.asignaciones;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import helper.Helper;

@RestController
//@RequestMapping(path="/")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})

public class Asignaciones {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 
	/*
	@GetMapping("historial/{id}")
	public List<Map<String,Object>> getHistoriales(){
		String sql = "SELECT * FROM historial_lesion";
		return jdbcTemplate.queryForList(sql);
	}*/
	
	@GetMapping("/asignaciones/{id_doctor}")
	public List<Map<String,Object>> getAsignacionesPorIdDoctor(@PathVariable("id_doctor") long id_doctor){
		String sql = "SELECT a.*, u.nombre, u.apellido FROM asignaciones a JOIN usuarios u ON a.id_paciente = u.id WHERE id_doctor = %d";
		sql = String.format(sql, id_doctor);
		return jdbcTemplate.queryForList(sql);
	}
	
	@PutMapping("/asignaciones/{id}")
		public Map<String,Object> editAginacion(@RequestBody Map<String,Object> asignacionData, @PathVariable("id") long id){
			String sql = "UPDATE asignaciones SET aprobado = %d, fecha_modificacion WHERE id = NOW()::timestamp RETURNING *";
			sql = String.format(sql, asignacionData.get("aprobado"), id);
			jdbcTemplate.queryForList(sql);
			Map<String, Object> map = new HashMap<String, Object>();
	        map.put("status", 200);
	        return map;
	}
	
	@PostMapping("/asignaciones")
	public Map<String,Object> insertHistorial(@RequestBody Map<String,Object> asignacionData){
		Map<String, Object> result = new HashMap<String, Object>();
		System.out.println(result.toString());
		String sql = "INSERT INTO asignaciones (id_doctor, id_paciente, id_lesion, fecha_creacion) VALUES(%d, %d, %d, NOW()::timestamp) RETURNING id"; 
		sql = String.format(sql, asignacionData.get("id_doctor"), asignacionData.get("id_paciente"),  asignacionData.get("id_lesion"));
		return jdbcTemplate.queryForMap(sql);
	}
	
}