package com.proyecto.skinnerServer.mensajes;

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
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})

public class Mensaje {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 
	/*
	@GetMapping("historial/{id}")
	public List<Map<String,Object>> getHistoriales(){
		String sql = "SELECT * FROM historial_lesion";
		return jdbcTemplate.queryForList(sql);
	}*/
	
	@GetMapping("/mensajes/{idLesion}")
	public List<Map<String,Object>> getMensajesById(@PathVariable("idLesion") long id){
		String sql = "SELECT a.id_origen_usuario, a.id_destino_usuario,a.mensaje,a.fecha,b.nombre as nombre_origen,b.apellido as apellido_origen,c.nombre as nombre_destino,c.apellido as apellido_destino FROM mensajes a join usuarios b on a.id_origen_usuario=b.id join usuarios c on a.id_destino_usuario=c.id WHERE id_lesion= %d order by fecha asc";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	
	@PostMapping("/mensajes")
	public Map<String,Object> insertMensajes(@RequestBody Map<String,Object> mensajesData){
		String sql = "INSERT INTO mensajes (id_origen_usuario, id_destino_usuario, mensaje, fecha, id_lesion) VALUES(%d, %d, '%s', now()::timestamp, %d) RETURNING id"; 
		sql = String.format(sql, mensajesData.get("id_origen_usuario"),  mensajesData.get("id_destino_usuario"),  mensajesData.get("mensaje"),  mensajesData.get("id_lesion"));
		return jdbcTemplate.queryForMap(sql);
	}
	
}