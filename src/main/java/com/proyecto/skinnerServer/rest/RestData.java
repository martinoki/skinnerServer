package com.proyecto.skinnerServer.rest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
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
	/*
	@PostMapping("/AnalizarImagen")
	public Map<String, Object> analize(@RequestBody String image) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        map.put("message", "Respuesta de SpringBoot, Imagen Recibida");
        return map;
//		return "Son 5, chicos... sta bien? y los MP? ehh? sta bien? y si yo me saco la foto adentro del ba�o? sin luz? sta bien? anda?";
	}*/
	
	@PostMapping("/AnalizarImagen")
	public Map<String, Object> analize(@RequestBody Map<String, String> image) {
	Map<String, Object> map = new HashMap<String, Object>();
	System.out.println("Entre a Analizar");
	try {
			System.out.println("Entre al try");
			String imagenBase64 =image.get("image");
			System.out.println("Hice el get de image");
			//decoder(obj.getString("image") ,"D:\\Users\\gomezcri\\Documents\\RepoSkinner\\ProyectoSkinner\\RedCNN\\Red2\\decoderimage.jpg");
			decoder( imagenBase64, System.getProperty("user.dir") + "/src/main/resources/network/" + "decoderimage.jpg");
			//MODIFICAR, PODRÍAMOS ENVIAR POR PARÁMETRO EL NOMBRE DEL ARCHIVO QUE SE CREA EN LA APP
			//Y CREARLO CON EL MISMO NOMBRE
			System.out.println("Pase el decoder");

	        String s = null;
	        String baseDir = System.getProperty("user.dir") + "/src/main/resources/network";
	        String scriptDir = baseDir + "/label_image.py ";
	        String modelDir = "--graph=" + baseDir + "/retrained_graph.pb ";
	        String labelDir = "--label=" + baseDir + "/retrained_labels.txt ";
	        String file = "--image=" + baseDir + "/decoderimage.jpg ";
	        //ENVIAR COMO PARAMETRO AL PYTHON CON EL MISMO NOMBRE QUE SE CREO CON EL DECODER
	        //Process p = Runtime.getRuntime().exec("python3 " + System.getProperty("user.dir") + "/src/main/resources/network/CNN.py decoderimage");
	        System.out.println("Pase todos los string");
	        Process p = Runtime.getRuntime().exec("python3 " + scriptDir + modelDir + labelDir + file);
	        System.out.println("Pase el process");
	        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        System.out.println("Pase el bufferreader");
	        while((s = in.readLine())!=null){
        	System.out.println(s);
	        map.put("message",s);
	        }
	       }
	       catch(IOException e)
	       {
	    	System.out.println("Error");
	    	System.out.println(e);
	        e.printStackTrace();
	       }
	       map.put("status", 200);
	       
	       return map;
	}

	public static void decoder(String base64Image, String pathFile) {
	   try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
	     // Converting a Base64 String into Image byte array
	     byte[] imageByteArray = Base64.getMimeDecoder().decode(base64Image);
	     imageOutFile.write(imageByteArray);
	   } catch (FileNotFoundException e) {
	     System.out.println("Image not found" + e);
	   } catch (IOException ioe) {
	     System.out.println("Exception while reading the Image " + ioe);
	   }
	 }
}
