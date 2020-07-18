package com.proyecto.skinnerServer.rest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
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
            String s = "ANÃ�LISIS DE IMAGEN - RESULTADO: ";
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
        }
	/*
	@PostMapping("/AnalizarImagen")
	public Map<String, Object> analize(@RequestBody String image) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        map.put("message", "Respuesta de SpringBoot, Imagen Recibida");
        return map;
	}*/
	
	@PostMapping("/AnalizarImagen")
	public Map<String, Object> analize(@RequestBody Map<String, String> image) {
	Map<String, Object> map = new HashMap<String, Object>();
	
	
	try {

			String imagenBase64 =image.get("image");
			//decoder(obj.getString("image") ,"D:\\Users\\gomezcri\\Documents\\RepoSkinner\\ProyectoSkinner\\RedCNN\\Red2\\decoderimage.jpg");
			decoder( imagenBase64, System.getProperty("user.dir") + "/src/main/resources/network/" + "decoderimage.jpg");
			//MODIFICAR, PODRÃ�AMOS ENVIAR POR PARÃ�METRO EL NOMBRE DEL ARCHIVO QUE SE CREA EN LA APP
			//Y CREARLO CON EL MISMO NOMBRE

	        String s = null;
	        String s2 = null;
	        String path;
			Contenido contenido;
	        String baseDir = System.getProperty("user.dir") + "/src/main/resources/network";
	        String scriptDir = baseDir + "/label_image.py ";
	        String scriptDir2 = baseDir + "/DetectarContornoYExtraerCaracteristicas.py ";
	        String modelDir = "--graph=" + baseDir + "/retrained_graph.pb ";
	        String labelDir = "--label=" + baseDir + "/retrained_labels.txt ";
	        String file = "--image=" + baseDir + "/decoderimage1.jpg ";
	        String filename ="--image=" + baseDir +"/decoderimage1";
	        //ENVIAR COMO PARAMETRO AL PYTHON CON EL MISMO NOMBRE QUE SE CREO CON EL DECODER
	        //Process p = Runtime.getRuntime().exec("python3 " + System.getProperty("user.dir") + "/src/main/resources/network/CNN.py decoderimage");
	        Process p = Runtime.getRuntime().exec("python3 " + scriptDir + modelDir + labelDir + file);
	        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        ObjectMapper mapper = new ObjectMapper();
	        while((s = in.readLine())!=null){
	        	
	        	//Map<String, Double> results = mapper.readValue("{\"lunar\": 0.5513151, \"melanoma\": 0.4486848}", Map.class);
	        	ExpressionParser parser = new SpelExpressionParser();
	            Map<String,String> results = (Map) parser.parseExpression(s).getValue();
	        	
	        	String key = maxUsingIteration(results);
	        	
	        	if(key.equals("lunar")||key.equals("melanoma"))
	        	{
	        		Process p2 = Runtime.getRuntime().exec("python3 " + scriptDir2 + filename);
	        		BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
	        		while((s2 = in2.readLine())!=null){
	        			//String aux = s2.replaceAll("'", "\"");
	        			//Caracteristicas[] data = new Gson().fromJson("[{'pathImagen': 'decoderimage1 ', 'contenido': {'asimetria': 'Asimetrico', 'diametro': 120}}]", Caracteristicas[].class);
	        			Caracteristicas[] data = new Gson().fromJson(s2, Caracteristicas[].class);
	        			
	        			
	        			path = data[0].getPathImagen();
	        			contenido = data[0].getContenido();
	        		}
	        		
	        	}
	        map.put("message",s);
	        }
	       }
	       catch(IOException e)
	       {
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
	
	public <K, V extends Comparable<V>> K maxUsingIteration(Map<K, V> map) {
	    Map.Entry<K, V> maxEntry = null;
	    for (Map.Entry<K, V> entry : map.entrySet()) {
	        if (maxEntry == null || entry.getValue()
	            .compareTo(maxEntry.getValue()) > 0) {
	            maxEntry = entry;
	        }
	    }
	    return maxEntry.getKey();
	}
	
	
class Caracteristicas {
		  private String pathImagen;
		  private Contenido contenido;

		 // Getter Methods 

		  public String getPathImagen() {
		    return pathImagen;
		  }
		  
		  public Contenido getContenido() {
			    return contenido;
		  }

		 // Setter Methods 

		  public void setPathImagen( String pathImagen ) {
		    this.pathImagen = pathImagen;
		  }
		  
		  public void setContenido(Contenido contenido) {
			  this.contenido = contenido; 
		  }

		}

class Contenido {
	  private String asimetria;
	  private float diametro;
	  
	 // Getter Methods 
	
	  public String getAsimetria() {
	    return asimetria;
	  }
	
	  public float getDiametro() {
	    return diametro;
	  }
	
	 // Setter Methods 
	
	  public void setAsimetria( String asimetria ) {
	    this.asimetria = asimetria;
	  }
	
	  public void setDiametro( float diametro ) {
	    this.diametro = diametro;
	  }
}
}
