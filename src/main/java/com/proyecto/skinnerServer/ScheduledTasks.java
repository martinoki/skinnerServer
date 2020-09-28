package com.proyecto.skinnerServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import helper.Helper;

@Component
public class ScheduledTasks {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Scheduled(cron = "0 0 7 * * ?")
	//s m h...
	public void reportAgenda() {
		String data = "The time is now " + (dateFormat.format(new Date())).toString();
		String sql = "SELECT u1.token, TO_CHAR(a.fecha_inicio::DATE, 'dd/mm/yyyy a las HH:mm') as fecha_inicio, u2.nombre as nombre_doctor, u2.apellido as apellido_doctor "
				+ "FROM agenda a join usuarios u1 on a.id_paciente = u1.id join usuarios u2 on a.id_doctor = u2.id "
				+ "WHERE fecha_inicio >= NOW()::timestamp "
				+ "and fecha_inicio < NOW()::timestamp + interval '1 DAY' and u1.token is not null";

		String title = "Recordatorio de Turno";
		String body = "Turno con el doctor %s \n el dia %s";
		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : result) {
			String token = map.get("token").toString();
			String fecha_inicio = map.get("fecha_inicio").toString();
			String doctor = map.get("nombre_doctor").toString().concat(" " + map.get("apellido_doctor").toString());
			Helper.enviarNotificacion(token, title, String.format(body, doctor, fecha_inicio));
		}
	}
	
	@Scheduled(cron = "0 0 12 * * ?")
	public void reportRecomendaciones() {
		String sql = "SELECT titulo, descripcion FROM recomendaciones r " + 
				"WHERE id_tipo = ? " + 
				"ORDER BY random() " + 
				"LIMIT 1;";
		
		Map<String, Object> recomendacion_tipo1 = jdbcTemplate.queryForMap(sql, 1);
		Map<String, Object> recomendacion_tipo2 = jdbcTemplate.queryForMap(sql, 2);
		Map<String, Object> recomendacion_tipo3 = jdbcTemplate.queryForMap(sql, 3);
		Map<String, Object> recomendacion_tipo4 = jdbcTemplate.queryForMap(sql, 4);
		
		sql = "SELECT id_paciente, id_tipo, token FROM ( " + 
				"	SELECT *, row_number() OVER (PARTITION BY id_paciente ORDER BY random()) as rn " +
				"FROM lesiones l join usuarios u on l.id_paciente = u.id where id_tipo <> 5 "
				+ "and u.token is not null) sub " + 
				"WHERE rn = 1;";
		List<Map<String, Object>> usuarios = jdbcTemplate.queryForList(sql);
		List<String> tokenList_tipo1 = new ArrayList<String>();
		List<String> tokenList_tipo2 = new ArrayList<String>();
		List<String> tokenList_tipo3 = new ArrayList<String>();
		List<String> tokenList_tipo4 = new ArrayList<String>();
		
		for (Map<String, Object> user : usuarios) {
			String token = user.get("token").toString();
			int id_tipo = (int)user.get("id_tipo");
			switch (id_tipo) {
			case 1:
				tokenList_tipo1.add(token);
				break;
			case 2:
				tokenList_tipo2.add(token);
				break;
			case 3:
				tokenList_tipo3.add(token);
				break;
			case 4:
				tokenList_tipo4.add(token);
				break;

			default:
				break;
			}
			
			if(!tokenList_tipo1.isEmpty()) {
				Helper.enviarMultiplesNotificaciones(tokenList_tipo1, recomendacion_tipo1.get("titulo").toString(), recomendacion_tipo1.get("descripcion").toString());
			};

			if(!tokenList_tipo2.isEmpty()) {
				Helper.enviarMultiplesNotificaciones(tokenList_tipo2, recomendacion_tipo2.get("titulo").toString(), recomendacion_tipo2.get("descripcion").toString());
			};
			
			if(!tokenList_tipo3.isEmpty()) {
				Helper.enviarMultiplesNotificaciones(tokenList_tipo3, recomendacion_tipo3.get("titulo").toString(), recomendacion_tipo3.get("descripcion").toString());
			};
			
			if(!tokenList_tipo4.isEmpty()) {
				Helper.enviarMultiplesNotificaciones(tokenList_tipo4, recomendacion_tipo4.get("titulo").toString(), recomendacion_tipo4.get("descripcion").toString());
			};
			
		}
		
		List<Map<String,Object>> lesiones_usuarios = jdbcTemplate.queryForList(sql);
		Map<Object, List<Map<String, Object>>> map = lesiones_usuarios.stream().collect(Collectors.groupingBy(m->m.remove("id_paciente")));
		System.out.println(map);
		
//		String title = "Recordatorio de Turno";
//		String body = "Turno con el doctor %s \n el dia %s";
//		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
//		for (Map<String, Object> map : result) {
//			String token = map.get("token").toString();
//			String fecha_inicio = map.get("fecha_inicio").toString();
//			String doctor = map.get("nombre_doctor").toString().concat(" " + map.get("apellido_doctor").toString());
//			Helper.enviarNotificacion(token, title, String.format(body, doctor, fecha_inicio));
//		}
	}
}
