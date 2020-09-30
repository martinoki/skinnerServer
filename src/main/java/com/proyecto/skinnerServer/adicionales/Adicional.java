package com.proyecto.skinnerServer.adicionales;

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

public class Adicional {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;

	@GetMapping("/adicionales/{id}")
	public List<Map<String,Object>> getAsignacionesPorId(@PathVariable("id") long id){
		String sql = "SELECT * FROM adicionales WHERE id_historial= %d order by id_tipo asc";
		sql = String.format(sql, id);
		List<Map<String,Object>>listaResultado= jdbcTemplate.queryForList(sql);
		List<Map<String,Object>>listaResultadoAgrupado = new ArrayList<Map<String,Object>>();
       	Map<String,Object> grupo=new HashMap<String, Object>();
		int idx = 1;
		for (Map<String,Object> item : listaResultado) {
				if(idx==(int)item.get("id_tipo")) {
            	grupo.put("id_tipo", (int)item.get("id_tipo"));
            	if(item.get("tipo").toString().equals("Borde")) {
                	grupo.put("borde",item.get("imagen"));
            	}
            	if(item.get("tipo").toString().equals("Color")) {
                	grupo.put("color",item.get("imagen"));
            	}
            	if(item.get("tipo").toString().equals("Recortada")) {
                	grupo.put("recortada",item.get("imagen"));
            	}
            }
            else {
            	idx++;
            	listaResultadoAgrupado.add(grupo);
                    grupo=new HashMap<String, Object>();

	            	grupo.put("id_tipo", (int)item.get("id_tipo"));
	            	if(item.get("tipo").toString().equals("Borde")) {
	                	grupo.put("borde",item.get("imagen"));
	            	}
	            	if(item.get("tipo").toString().equals("Color")) {
	                	grupo.put("color",item.get("imagen"));
	            	}
	            	if(item.get("tipo").toString().equals("Recortada")) {
	                	grupo.put("recortada",item.get("imagen"));
	            	}
	            
            }

		}
    	listaResultadoAgrupado.add(grupo);
return listaResultadoAgrupado;
//TODO Hacer un mejor codigo By:luqui
//BLAME LUQUI
	}

	@DeleteMapping("/adicionales/{id}/{id_tipo}")
	public Map<String,Object> deleteAdicional(@PathVariable("id") long id,@PathVariable("id_tipo") long id_tipo){
		String sql = "delete from adicionales WHERE id_historial = %d and id_tipo= %d;";
		sql = String.format(sql, id,id_tipo);
		Map<String, Object> map = new HashMap<String, Object>();
		jdbcTemplate.update(sql);
		map.put("status", 200);
		return map;
	}
}
