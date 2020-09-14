package com.proyecto.skinnerServer.recomendaciones;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import helper.UpdatableBCrypt;

@RestController
//@RequestMapping(path="/")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
		RequestMethod.DELETE })
public class Recomendaciones {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/recomendaciones")
	public List<Map<String, Object>> getRecomendaciones() {

		String sql = "SELECT a.id,a.id_tipo, b.descripcion as tipolesion, a.titulo FROM recomendaciones as a inner join public.tipo_lesion as b on a.id_tipo=b.id";
		return jdbcTemplate.queryForList(sql);
	}

	@GetMapping("/recomendaciones/{id}")
	public List<Map<String, Object>> getRecomendacionesById(@PathVariable("id") long id) {
		String sql = "SELECT * FROM recomendaciones WHERE id = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}

	@GetMapping("/recomendaciones/tipo/{id_tipo}")
	public List<Map<String, Object>> getRecomendacionesByTipo(@PathVariable("id_tipo") long id) {
		String sql = "SELECT * FROM recomendaciones WHERE id_tipo = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}

	@PutMapping("/recomendaciones/{id}")
	public Map<String, Object> editRecomendaciones(@RequestBody Map<String, Object> recomendacionData,
			@PathVariable("id") long id) {
		String sql = "UPDATE public.recomendaciones SET titulo = '%s', descripcion= '%s' WHERE id = %d RETURNING *";
		sql = String.format(sql, recomendacionData.get("titulo"), recomendacionData.get("descripcion"), id);
		jdbcTemplate.queryForList(sql);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		return map;
	}

	@PostMapping("/recomendaciones")
	public Map<String, Object> insertRecomendaciones(@RequestBody Map<String, Object> recomendacionData) {
		String sql = "INSERT INTO public.recomendaciones (id_tipo, titulo, descripcion) VALUES(%d, '%s', '%s') RETURNING id;";
		sql = String.format(sql, recomendacionData.get("id_tipo"), recomendacionData.get("titulo"),
				recomendacionData.get("descripcion"));
		return jdbcTemplate.queryForMap(sql);
	}

	@DeleteMapping("/recomendaciones/{id}")
	public Map<String, Object> deleteRecomendacion(@PathVariable("id") long id) {
		String sql = "DELETE FROM public.recomendaciones false WHERE id = %d;";
		sql = String.format(sql, id);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		return map;
	}

}
