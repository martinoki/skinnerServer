package com.proyecto.skinnerServer.lesiones;

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
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
public class Lesiones {
	
	 @Autowired
	 JdbcTemplate jdbcTemplate;
	 
	
	@GetMapping("/lesiones")
	public List<Map<String,Object>> getLesiones(){
		String sql = "SELECT * FROM lesiones";
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/tipo_lesiones")
	public List<Map<String,Object>> getTipoLesiones(){
		String sql = "SELECT * FROM tipo_lesion";
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/lesiones/paciente/{id}")
	public List<Map<String,Object>> getLesionesPorPaciente(@PathVariable("id") long id){
		String sql = "SELECT * FROM lesiones WHERE id_paciente = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/lesiones/usuarios/{id}")
	public List<Map<String,Object>> getUsuarioPorIdLesion(@PathVariable("id") long id){
		String sql = "SELECT id_paciente,id_doctor FROM lesiones WHERE id = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	@GetMapping("/lesiones/{id}")
	public List<Map<String,Object>> getLesionPorId(@PathVariable("id") long id){
		String sql = "SELECT a.id_paciente,a.id_doctor,a.descripcion,a.id_tipo,a.ubicacion,a.fecha_creacion,a.activo,a.imagen,a.seccion,a.analisis,b.descripcion as nombreLesion FROM lesiones a join tipo_lesion b on a.id_tipo=b.id WHERE a.id = %d";
		sql = String.format(sql, id);
		return jdbcTemplate.queryForList(sql);
	}
	
	@PutMapping("/lesiones/{id}")
		public Map<String,Object> editLesion(@RequestBody Map<String,Object> lesionData, @PathVariable("id") long id){
		String sql = "UPDATE public.lesiones SET id_paciente = %d, id_doctor = %d, descripcion = '%s', id_tipo = %d, ubicacion = '%s', fecha_creacion = '%s' WHERE id = %d RETURNING *";
		sql = String.format(sql, lesionData.get("id_paciente"),  lesionData.get("id_doctor"),  lesionData.get("descripcion"),  lesionData.get("id_tipo"),  lesionData.get("ubicacion"),  lesionData.get("fecha_creacion"), id);
		jdbcTemplate.queryForList(sql);
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        return map;
	}
	
	@PostMapping("/lesiones")
	public Map<String,Object> insertLesion(@RequestBody Map<String,Object> lesionData){
	Map<String, Object> tipo = Helper.analizarImagen(lesionData.get("imagen").toString());
	String sql = "INSERT INTO public.lesiones (id_paciente, id_doctor, descripcion, id_tipo, ubicacion, fecha_creacion, imagen, seccion, analisis) "+
			"VALUES(%d, %d, '%s', %d, '%s', '%s', '%s', '%s', '%s') RETURNING *;";
	String select = "SELECT * from public.tipo_lesion WHERE descripcion ILIKE '" + tipo.get("result") + "'";
	Map<String, Object> id_tipo = jdbcTemplate.queryForMap(select);
	sql = String.format(sql, lesionData.get("id_paciente"),  lesionData.get("id_doctor"),  lesionData.get("descripcion"),  id_tipo.get("id"),  lesionData.get("ubicacion"),  lesionData.get("fecha_creacion"),  lesionData.get("imagen"), lesionData.get("seccion"), tipo.get("analisis").toString());
	Map<String, Object> result = jdbcTemplate.queryForMap(sql);
	int id_lesion = (int)result.get("id");
	
	String resultadoAnalisisLunar = "";
	if(tipo.get("result").equals("melanoma") || tipo.get("result").equals("lunar")) {
		resultadoAnalisisLunar = Helper.analizarCaracteristicas(lesionData.get("imagen").toString());
	}
	System.out.println(resultadoAnalisisLunar);
	String sqlHistorial = "INSERT INTO historial_lesion (id_lesion, id_doctor, descripcion, imagen, fecha, analisis) VALUES(%d, %d, '%s', '%s', '%s', '%s') RETURNING id"; 
	sqlHistorial = String.format(sqlHistorial, id_lesion,  lesionData.get("id_doctor"),  lesionData.get("descripcion"),  lesionData.get("imagen"),  lesionData.get("fecha_creacion"), resultadoAnalisisLunar);
	Map<String, Object> historial = jdbcTemplate.queryForMap(sqlHistorial);
	result.put("id_historial", historial.get("id"));
	String queryAdicionales = Helper.agregarAdicionales((int)historial.get("id"), lesionData.get("imagen").toString());
	if(!queryAdicionales.equals("")) {
		jdbcTemplate.update(queryAdicionales);			
	}
	return result;
	}
	
	@DeleteMapping("/lesiones/{id}")
	public Map<String,Object> deleteLesion(@PathVariable("id") long id){
		String sql = "UPDATE public.lesiones SET activo = false WHERE id = %d;";
		sql = String.format(sql, id);
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        return map;
	}
}
