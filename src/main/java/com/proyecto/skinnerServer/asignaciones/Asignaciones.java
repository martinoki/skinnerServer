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

import com.proyecto.skinnerServer.api.email.EmailBody;
import com.proyecto.skinnerServer.api.email.EmailPort;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import helper.EmailHtmlCreator;
import helper.Helper;

@RestController
//@RequestMapping(path="/")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})

public class Asignaciones {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 
	 @Autowired
		private EmailPort emailPort;
	 
	/*
	@GetMapping("historial/{id}")
	public List<Map<String,Object>> getHistoriales(){
		String sql = "SELECT * FROM historial_lesion";
		return jdbcTemplate.queryForList(sql);
	}*/
	
	@GetMapping("/asignaciones/{id_doctor}")
	public List<Map<String,Object>> getAsignacionesPorIdDoctor(@PathVariable("id_doctor") long id_doctor){
		String sql = "SELECT a.*, l.*, u.nombre, u.apellido FROM asignaciones a JOIN usuarios u ON a.id_paciente = u.id JOIN lesiones l ON a.id_lesion= l.id WHERE a.id_doctor = %d AND a.aprobado is null";
		sql = String.format(sql, id_doctor);
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/asignaciones/count/{id_doctor}")
	public int getCantidadAsignacionesPorIdDoctor(@PathVariable("id_doctor") long id_doctor){
		String sql = "SELECT count(*) FROM asignaciones WHERE id_doctor = %d AND aprobado is null";
		sql = String.format(sql, id_doctor);
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
	
	@PutMapping("/asignaciones/{id}")
		public Map<String,Object> editAginacion(@RequestBody Map<String,Object> asignacionData, @PathVariable("id") long id){
			String sql = "UPDATE asignaciones SET aprobado = %b, fecha_modificacion= NOW()::timestamp WHERE id = %d RETURNING  *";
			sql = String.format(sql, asignacionData.get("aprobado"), id);
			List<Map<String,Object>> lista = jdbcTemplate.queryForList(sql);
			if(!lista.isEmpty()) {
				sql = "SELECT email, token FROM usuarios WHERE id = %d";
				sql = String.format(sql, Integer.parseInt(lista.get(0).get("id_paciente").toString()));
				Map<String, Object> userData = jdbcTemplate.queryForMap(sql);
				String resultadoSolicitud = "rechazada";
				Map<String, Object> datosMedico = jdbcTemplate.queryForMap("SELECT * FROM lugares WHERE id = "+lista.get(0).get("id_lugar"));
				if((boolean)asignacionData.get("aprobado") == true) {
					resultadoSolicitud = "aprobada";
					String updateQuery = "UPDATE lesiones SET id_doctor = %d, id_lugar = %d WHERE id = %d";
					updateQuery = String.format(updateQuery, lista.get(0).get("id_doctor"), lista.get(0).get("id_lugar"), lista.get(0).get("id_lesion"));
					jdbcTemplate.update(updateQuery);
				}else {
					
				}
				Helper.enviarNotificacion(userData.get("token").toString(),
						"Solicitud de atenci&oacute;n", "Su solicitud fue ".concat(resultadoSolicitud));
				EmailBody emailBody = new EmailBody(userData.get("email").toString(),
						EmailHtmlCreator.createBody("Solicitud de atención",
								"Su solicitud fue " + resultadoSolicitud + "<br/>Lugar: "+datosMedico.get("nombre") + "<br/>"+
										"Direccion: "+ datosMedico.get("direccion").toString()+"<br/>"+
										"Telefono: " +datosMedico.get("telefono")
								)
						, "SkinnerApp - Solicitud de atención");
				emailPort.sendEmail(emailBody);
			}
			Map<String, Object> map = new HashMap<String, Object>();
	        map.put("status", 200);
	        return map;
	}
	
	@PostMapping("/asignaciones")
	public Map<String,Object> insertHistorial(@RequestBody Map<String,Object> asignacionData){
		Map<String, Object> result = new HashMap<String, Object>();
		System.out.println(result.toString());
		String sql = "INSERT INTO asignaciones (id_doctor, id_paciente, id_lesion, id_lugar, fecha_creacion) VALUES(%d, %d, %d, %d, NOW()::timestamp) RETURNING id"; 
		sql = String.format(sql, asignacionData.get("id_doctor"), asignacionData.get("id_paciente"),  asignacionData.get("id_lesion"),  asignacionData.get("id_lugar"));
		return jdbcTemplate.queryForMap(sql);
	}
	
}
