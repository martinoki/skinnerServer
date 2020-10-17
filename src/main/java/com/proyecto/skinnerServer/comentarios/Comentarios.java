package com.proyecto.skinnerServer.comentarios;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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

public class Comentarios {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;

	@GetMapping("/comentarios/{id}/{tipo}")
	public List<Map<String,Object>> getComentariosPorIdLesionYTipo(@PathVariable("id") long id,@PathVariable("tipo") long tipo){
		String sql = "SELECT * FROM comentarios WHERE lesion= %d and tipo_comentario=%d order by fechacreacion";

	sql = String.format(sql,id,tipo);
	jdbcTemplate.queryForList(sql);
	Map<String, Object> map = new HashMap<String, Object>();
    map.put("status", 200);
	return jdbcTemplate.queryForList(sql);
}
	
	@PostMapping("/comentarios")
	public Map<String, Object> insertTratamiento(@RequestBody Map<String, Object> comentarioData) {
		String sql = "INSERT INTO public.comentarios (tipo_comentario, lesion,comentario,fechacreacion) VALUES(%d, '%d', '%s',now()::timestamp) RETURNING id;";
		sql = String.format(sql, comentarioData.get("tipo_comentario"), comentarioData.get("lesion"),
				comentarioData.get("comentario"));
		return jdbcTemplate.queryForMap(sql);
	}

}
