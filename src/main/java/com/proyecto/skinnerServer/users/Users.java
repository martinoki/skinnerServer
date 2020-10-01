package com.proyecto.skinnerServer.users;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.skinnerServer.api.email.EmailBody;
import com.proyecto.skinnerServer.api.email.EmailPort;

import helper.Helper;
import helper.UpdatableBCrypt;
import helper.passwordGenerator;

@RestController
//@RequestMapping(path="/")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
		RequestMethod.DELETE })
public class Users {

	private final RestTemplate restTemplate = new RestTemplate();
	@Autowired
	JdbcTemplate jdbcTemplate;

	@PostMapping("/data")
	public Post mandarNotif() {
		HttpHeaders headers = new HttpHeaders();

		headers.set("Authorization",
				"key=AAAARYSOvWI:APA91bG8B0d18HRFyXyTKT9K3CsZ7eCZ9lVJ9FJONeJiW0gVWqhYbn4uL60NSFWQUJb6vbZapuEtmzUBpo5hWHOKlujPnv6FP92TuPKgGW3FGftZjUyq0C2JW6IZJs9Bw9By9owxCqy_");
		headers.set("Content-Type", "application/json");

		String url = "https://fcm.googleapis.com/fcm/send";
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> notification = new HashMap<>();
		notification.put("body", "A VER SI ANDA ESTE MENSAJE DE MIERDA");
		notification.put("title", "TITULO DEL MENSAJITO");
		map.put("to",
				"eI4tA3XlTLuu_MX0_b7S4a:APA91bFzDLTU3elSNg6UejjEkgJ6FS0FPRTQB1WWF_tuXsgZ_RBNrcedQi4nZBRV-ulYuOeIOJyhibzNEOis8pqTj_tNGDOdtcZEtn0CH3_AeIyhdPxH8tGOWbnE4H0l1S7T_H_SeIhx");
		map.put("collapse_key", "type_a");
		map.put("notification", notification);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<Post> response = restTemplate.exchange(url, HttpMethod.POST, entity, Post.class);
		return response.getBody();
	}

	@GetMapping("/usuarios")
	public List<Map<String, Object>> getUsuarios() {
		String sql = "SELECT * FROM usuarios";
		return jdbcTemplate.queryForList(sql);
	}

	@GetMapping("/usuarios/rol/{id}")
	public List<Map<String, Object>> getUsuariosByRol(@PathVariable("id") int id_rol) {
		String sql = "SELECT * FROM usuarios WHERE id_rol=?";
		return jdbcTemplate.queryForList(sql, id_rol);
	}

	@GetMapping("/usuarios/notificaciones/{id}")
	public int getLesionesPorPacienteCount(@PathVariable("id") long id) {
		String sql = "SELECT COUNT(*) FROM lesiones WHERE id_paciente = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	@PutMapping("/usuarios/{id}")
	public Map<String, Object> editUsuario(@RequestBody Map<String, Object> usuarioData, @PathVariable("id") long id) {
		String sql = "UPDATE public.usuarios SET nombre = '%s', apellido = '%s', telefono = '%s', direccion = '%s', id_rol = %d,activo=%s WHERE id = %d RETURNING *";
		sql = String.format(sql, usuarioData.get("nombre"), usuarioData.get("apellido"), usuarioData.get("telefono"),
				usuarioData.get("direccion"), usuarioData.get("id_rol"), usuarioData.get("activo"), id);

		jdbcTemplate.queryForList(sql);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		return map;
	}
	
	@PutMapping("/cambiar_password/{id}")
	public Map<String, Object> usuarioData(@RequestBody Map<String, Object> request, @PathVariable("id") long id) {
		
		String sqlSelect = "SELECT * FROM public.usuarios WHERE id = %d;";
		sqlSelect = String.format(sqlSelect, id);
		Map<String, Object> user = jdbcTemplate.queryForMap(sqlSelect);
		UpdatableBCrypt hasheador = new UpdatableBCrypt(5);
		boolean passwordCorrect = hasheador.verifyHash(request.get("password").toString(), user.get("password").toString());
		if(passwordCorrect == true) {
			String hashedPassword = hasheador.hash(request.get("new_password").toString());
			String sql = "UPDATE public.usuarios SET password = '%s' WHERE id = %d RETURNING *";
			sql = String.format(sql, hashedPassword, id);
			return jdbcTemplate.queryForMap(sql);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mensaje", "Contraseña incorrecta");
		map.put("status", 400);
		return map;
	}

	@PostMapping("/usuarios")
	public Map<String, Object> insertUsuario(@RequestBody Map<String, Object> usuarioData) {
		UpdatableBCrypt hasheador = new UpdatableBCrypt(5);
		String sqlSelect = "SELECT * FROM public.usuarios WHERE email = '%s';";
		sqlSelect = String.format(sqlSelect, usuarioData.get("email"));
		List<Map<String, Object>> userData = jdbcTemplate.queryForList(sqlSelect);
		if (userData.isEmpty()) {
			String sql = "INSERT INTO public.usuarios (nombre, apellido, email, password, telefono, direccion, id_rol, id_ciudad) VALUES('%s', '%s', '%s', '%s', '%s', '%s', %d, %d) RETURNING id;";
			sql = String.format(sql, usuarioData.get("nombre"), usuarioData.get("apellido"), usuarioData.get("email"),
					hasheador.hash(usuarioData.get("password").toString()), usuarioData.get("telefono"),
					usuarioData.get("direccion"), usuarioData.get("id_rol"), usuarioData.get("id_ciudad"));
			return jdbcTemplate.queryForMap(sql);
		}else {
			throw new ResponseStatusException(
			          HttpStatus.BAD_REQUEST, "El usuario ya existe");			
		}
	}

	@PostMapping("/login")
	public Map<String, Object> login(@RequestBody Map<String, Object> loginData) {
		Map<String, Object> response = new HashMap<String, Object>();
		UpdatableBCrypt hasheador = new UpdatableBCrypt(5);
		String sql = "SELECT * FROM public.usuarios WHERE email = '%s';";
		sql = String.format(sql, loginData.get("email"));
		List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);
		if (userData.isEmpty()) {
			response.put("message", "Usuario no encontrado");
		} else {

			Boolean passwordCorrect = hasheador.verifyHash(loginData.get("password").toString(),
					((userData.get(0)).get("password")).toString());
			if (passwordCorrect) {
				Map<String, Object> user = new HashMap<String, Object>(userData.get(0));
				user.remove("password");
				sql = "UPDATE public.usuarios SET token = '%s' WHERE id = %d;";
				sql = String.format(sql, loginData.get("token"), user.get("id"));
				jdbcTemplate.update(sql);
				return user;
			} else {
				response.put("message", "Contraseña incorrecta");
			}

		}
		return response;
	}
	
	@Autowired
	private EmailPort emailPort;
	
	@PostMapping("/recuperar_password")
	public Map<String, Object> recuperarPassword(@RequestBody Map<String, Object> recoveryData) {
		Map<String, Object> response = new HashMap<String, Object>();
		String email = recoveryData.get("email").toString();
		String sql = "SELECT * FROM public.usuarios WHERE email = '%s';";
		sql = String.format(sql, email);
		List<Map<String, Object>> user = jdbcTemplate.queryForList(sql);
		if (user.isEmpty()) {
			response.put("message", "Usuario no encontrado");
		}else {
		String newPassword = passwordGenerator.generatePassword();
		UpdatableBCrypt hasheador = new UpdatableBCrypt(5);
		String hashedPass = hasheador.hash(newPassword);
		sql = "UPDATE public.usuarios SET password = '%s' WHERE email = '%s';";
		sql = String.format(sql, hashedPass, email);
		jdbcTemplate.update(sql);
		EmailBody emailBody = new EmailBody(email, "Su nueva contraseña es: ".concat(newPassword), "SkinnerApp - Recuperar contraseña");
		emailPort.sendEmail(emailBody);
		response.put("status", 200);
		response.put("message", "Usuario actualizado correctamente");
		}
		/*
		String sql = "SELECT * FROM public.usuarios WHERE email = '%s';";
		sql = String.format(sql, loginData.get("email"));
		List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);
		if (userData.isEmpty()) {
			response.put("message", "Usuario no encontrado");
		} else {

			Boolean passwordCorrect = hasheador.verifyHash(loginData.get("password").toString(),
					((userData.get(0)).get("password")).toString());
			if (passwordCorrect) {
				Map<String, Object> user = new HashMap<String, Object>(userData.get(0));
				user.remove("password");
				sql = "UPDATE public.usuarios SET token = '%s' WHERE id = %d;";
				sql = String.format(sql, loginData.get("token"), user.get("id"));
				jdbcTemplate.update(sql);
				return user;
			} else {
				response.put("message", "Contraseña incorrecta");
			}

		}*/
		return response;
	}
	
	@PutMapping("/actualizar_password/{id}")
	public Map<String, Object> actualizarPassword(@RequestBody Map<String, Object> passwordData, @PathVariable("id") long id) {
		String newPassword = passwordData.get("password").toString();
		UpdatableBCrypt hasheador = new UpdatableBCrypt(5);
		String hashedPass = hasheador.hash(newPassword);
		String sql = "UPDATE public.usuarios SET password = '%s' WHERE id = %d RETURNING id";
		sql = String.format(sql, hashedPass, id);
		Map<String,Object> newUserData = jdbcTemplate.queryForMap(sql);
		return newUserData;
	}

	@DeleteMapping("/usuarios/{id}")
	public Map<String, Object> deleteTratamiento(@PathVariable("id") long id) {
		String sql = "UPDATE public.usuarios SET activo = false WHERE id = %d;";
		sql = String.format(sql, id);
		System.out.println(sql);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		return map;
	}

	@GetMapping("/usuarios/{id}")
	public Map<String, Object> paciente(@PathVariable("id") long id) {
		String sql = "SELECT * FROM usuarios WHERE id = " + id;
		
		Map<String, Object> user = jdbcTemplate.queryForMap(sql);
		user.remove("password");
		return user;
	}
	
	@PutMapping("/cerrar_sesion/{id}")
	public void cerrarSesion(@PathVariable("id") long id) {
		String sql = "UPDATE usuarios SET token = null WHERE id = " + id;
		jdbcTemplate.update(sql);
	}
	
	@PutMapping("/recibir_notificacion/{id}")
	public void recibirNotificacion(@PathVariable("id") int id, @RequestBody Map<String, Object> data) {
		String sql = "UPDATE usuarios SET recibir_notificaciones = %b WHERE id = %d" ;
		sql = String.format(sql, data.get("estado"), id);
		jdbcTemplate.update(sql);
	}
	
	@GetMapping("/notificacion_habilitada/{id}")
	public Map<String, Object> recibirNotificacion(@PathVariable("id") int id) {
		String sql = "SELECT recibir_notificaciones FROM usuarios WHERE id = %d" ;
		sql = String.format(sql, id);
		return jdbcTemplate.queryForMap(sql);
	}

}
