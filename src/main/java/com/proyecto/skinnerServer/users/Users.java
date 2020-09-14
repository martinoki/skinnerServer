package com.proyecto.skinnerServer.users;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

import helper.UpdatableBCrypt;

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

	@PostMapping("/usuarios")
	public Map<String, Object> insertUsuario(@RequestBody Map<String, Object> usuarioData) {
		UpdatableBCrypt hasheador = new UpdatableBCrypt(5);
		String sql = "INSERT INTO public.usuarios (nombre, apellido, email, password, telefono, direccion, id_rol) VALUES('%s', '%s', '%s', '%s', '%s', '%s', %d) RETURNING id;";
		sql = String.format(sql, usuarioData.get("nombre"), usuarioData.get("apellido"), usuarioData.get("email"),
				hasheador.hash(usuarioData.get("password").toString()), usuarioData.get("telefono"),
				usuarioData.get("direccion"), usuarioData.get("id_rol"));
		return jdbcTemplate.queryForMap(sql);
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
				return user;
			} else {
				response.put("message", "Contraseña incorrecta");
			}

		}
		return response;
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
	public List<Map<String, Object>> paciente(@PathVariable("id") long id) {
		String sql = "SELECT * FROM usuarios WHERE id = " + id;
		return jdbcTemplate.queryForList(sql);
	}

}
