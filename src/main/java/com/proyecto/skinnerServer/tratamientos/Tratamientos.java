package com.proyecto.skinnerServer.tratamientos;

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
@RestController
//@RequestMapping(path="/")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE})
public class Tratamientos {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/tratamientos")
	public List<Map<String, Object>> getTratamientos() {
		String sql = "SELECT a.id,b.descripcion as tipolesion,a.titulo,a.descripcion FROM public.tratamientos as a inner join public.tipo_lesion as b on a.id_tipo=b.id order by a.id_tipo";
		return jdbcTemplate.queryForList(sql);
	}

	@PutMapping("/tratamientos/{id}")
	public Map<String, Object> editTratamiento(@RequestBody Map<String, Object> tratamientoData,
			@PathVariable("id") long id) {
		String sql = "UPDATE public.tratamientos SET id_tipo = %d, titulo ='%s', descripcion = '%s' WHERE id = %d RETURNING *";
		sql = String.format(sql, tratamientoData.get("tipoLesion"), tratamientoData.get("titulo"),
				tratamientoData.get("descripcion"), id);

		jdbcTemplate.queryForList(sql);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		System.out.println();
		return map;
	}

	@PostMapping("/tratamientos")
	public Map<String, Object> insertTratamiento(@RequestBody Map<String, Object> tratamientoData) {
		String sql = "INSERT INTO public.tratamientos (id_tipo, titulo, descripcion) VALUES(%d, '%s', '%s') RETURNING id;";
		sql = String.format(sql, tratamientoData.get("tipoLesion"), tratamientoData.get("titulo"),
				tratamientoData.get("descripcion"));
		return jdbcTemplate.queryForMap(sql);
	}

	@DeleteMapping("/tratamientos/{id}")
	public Map<String, Object> deleteTratamiento(@PathVariable("id") long id) {
		String sql = "delete from public.tratamientos where id = %d;";
		sql = String.format(sql, id);
		System.out.println(sql);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 200);
		return map;
	}
}
