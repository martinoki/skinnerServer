package com.proyecto.skinnerServer.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping(path="/")
public class RestData {
	
	
	@GetMapping("/")
	public Map<String, Object> greeting() {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        //map.put("message", "Respuesta de SpringBoot");
        try {
            String s = "ANÁLISIS DE IMAGEN - RESULTADO: ";
            System.out.println(System.getProperty("user.dir"));
            //Process p = Runtime.getRuntime().exec("python3 " + System.getProperty("user.dir") + "/ProyectoSkinner/RedCNN/Red2/CNN.py");
            //Process p = Runtime.getRuntime().exec("python3 " + System.getProperty("user.dir") + "/src/main/resources/hello.py")	;
            Process p = Runtime.getRuntime().exec("python3 " + System.getProperty("user.dir") + "/src/main/resources/network/CNN.py")	;
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println("Waiting for batch file ...");
            try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("TODO MAL ESPERANDO");
			}
            System.out.println("Batch file done.");
            
            while((s = in.readLine()) !=null){
            	map.put("message", s);
            	s = in.readLine();
        	}
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return map;
//		return "Son 5, chicos... sta bien? y los MP? ehh? sta bien? y si yo me saco la foto adentro del ba�o? sin luz? sta bien? anda?";
	}
	
	@PostMapping("/AnalizarImagen")
	public Map<String, Object> analize(@RequestBody String image) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        map.put("message", "Respuesta de SpringBoot, Imagen Recibida");
        return map;
//		return "Son 5, chicos... sta bien? y los MP? ehh? sta bien? y si yo me saco la foto adentro del ba�o? sin luz? sta bien? anda?";
	}
}
