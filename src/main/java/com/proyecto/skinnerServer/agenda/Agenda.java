package com.proyecto.skinnerServer.agenda;

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
public class Agenda {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 
	 @GetMapping("/agenda/{id_usuario}")
		public List<Map<String,Object>> getCitas(@PathVariable("id_usuario") int id_usuario){
			Map<String,Object> userData = jdbcTemplate.queryForMap(String.format("SELECT id_rol FROM usuarios WHERE id = %d",id_usuario));
			String sql = "SELECT id::VARCHAR, titulo as title, fecha_inicio as start, fecha_fin as end FROM agenda WHERE %s = %d";
			if(userData.get("id_rol").equals(1)) {
				sql = String.format(sql, "id_doctor", id_usuario);
			}else {
				sql = String.format(sql, "id_paciente", id_usuario);
			}
			return jdbcTemplate.queryForList(sql);
		}
		
		@PostMapping("/agenda")
		public Map<String,Object> insertCita(@RequestBody Map<String,Object> citaData){
			String sql = "INSERT INTO agenda (id_paciente, id_doctor, titulo, fecha_inicio, fecha_fin) VALUES(%d, %d, '%s', '%s', '%s') RETURNING id;";
			sql = String.format(sql, citaData.get("id_paciente"), citaData.get("id_doctor"), citaData.get("titulo"),  citaData.get("fecha_inicio"), citaData.get("fecha_fin"));
			return jdbcTemplate.queryForMap(sql);
		}
		
		@DeleteMapping("/agenda/{id}")
		public Map<String,Object> deleteCita(@PathVariable("id") long id){
			String sql = "DELETE FROM agenda WHERE id = %d";
			sql = String.format(sql, id);
			System.out.println(sql);
			jdbcTemplate.update(sql);
			Map<String, Object> map = new HashMap<String, Object>();
	        map.put("status", 200);
	        return map;
		}
		
}
