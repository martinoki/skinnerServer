package com.proyecto.skinnerServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	public void reportCurrentTime() {
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
}
