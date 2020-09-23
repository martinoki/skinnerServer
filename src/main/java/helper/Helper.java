package helper;


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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.proyecto.skinnerServer.users.Post;

public class Helper {

	public static void enviarNotificacion(String token, String title, String body) {
		HttpHeaders headers = new HttpHeaders();

		headers.set("Authorization",
				"key=AAAARYSOvWI:APA91bG8B0d18HRFyXyTKT9K3CsZ7eCZ9lVJ9FJONeJiW0gVWqhYbn4uL60NSFWQUJb6vbZapuEtmzUBpo5hWHOKlujPnv6FP92TuPKgGW3FGftZjUyq0C2JW6IZJs9Bw9By9owxCqy_");
		headers.set("Content-Type", "application/json");

		String url = "https://fcm.googleapis.com/fcm/send";
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("title", title);
		data.put("body", body);
		map.put("to", token);
		map.put("collapse_key", "type_a");
		map.put("data", data);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<Post> response = restTemplate.exchange(url, HttpMethod.POST, entity, Post.class);
	}
	
	public static void enviarMultiplesNotificaciones(List<String> token, String title, String body) {
		HttpHeaders headers = new HttpHeaders();

		headers.set("Authorization",
				"key=AAAARYSOvWI:APA91bG8B0d18HRFyXyTKT9K3CsZ7eCZ9lVJ9FJONeJiW0gVWqhYbn4uL60NSFWQUJb6vbZapuEtmzUBpo5hWHOKlujPnv6FP92TuPKgGW3FGftZjUyq0C2JW6IZJs9Bw9By9owxCqy_");
		headers.set("Content-Type", "application/json");

		String url = "https://fcm.googleapis.com/fcm/send";
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("title", title);
		data.put("body", body);
		map.put("registration_ids", token);
		map.put("collapse_key", "type_a");
		map.put("data", data);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<Post> response = restTemplate.exchange(url, HttpMethod.POST, entity, Post.class);
	}

	public static Map<String, Object> analizarImagen(String imagenBase64) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {

			decoder(imagenBase64, System.getProperty("user.dir") + "/src/main/resources/network/" + "decoderimage.jpg");
			// MODIFICAR, PODRÃ�AMOS ENVIAR POR PARÃ�METRO EL NOMBRE DEL ARCHIVO QUE SE CREA
			// EN LA APP
			// Y CREARLO CON EL MISMO NOMBRE

			String s = null;
			String s2 = null;
			String path;
			Contenido contenido;
			String baseDir = "\"" + System.getProperty("user.dir") + "/src/main/resources/network";
			String scriptDir = baseDir + "/label_image.py\" ";
			String scriptDir2 = baseDir + "/DetectarContornoYExtraerCaracteristicas.py\" ";
			String modelDir = "--graph=" + baseDir + "/retrained_graph.pb\" ";
			String labelDir = "--label=" + baseDir + "/retrained_labels.txt\" ";
			String file = "--image=" + baseDir + "/decoderimage.jpg\" ";
			String filename = "--image=" + baseDir + "/decoderimage\" ";
			// ENVIAR COMO PARAMETRO AL PYTHON CON EL MISMO NOMBRE QUE SE CREO CON EL
			// DECODER
			Process p = Runtime.getRuntime().exec("python3 " + scriptDir + modelDir + labelDir + file);
			// Process p = Runtime.getRuntime().exec("python3 " + scriptDir);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			ObjectMapper mapper = new ObjectMapper();
			while ((s = in.readLine()) != null) {

				ExpressionParser parser = new SpelExpressionParser();
				Map<String, String> results = (Map) parser.parseExpression(s).getValue();

				map.put("analisis", results.toString().replace("=", ":"));
				String key = maxUsingIteration(results);
				System.out.println(results.get(key));
				if (Double.parseDouble(results.get(key)) < 0.66) {
					key = "ninguna";
				}

				if (key.equals("lunar") || key.equals("melanoma")) {
					Process p2 = Runtime.getRuntime().exec("python3 " + scriptDir2 + filename);
					BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
					while ((s2 = in2.readLine()) != null) {
						Caracteristicas[] data = new Gson().fromJson(s2, Caracteristicas[].class);

						path = data[0].getPathImagen();
						contenido = data[0].getContenido();
					}

				}
				map.put("result", key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static Map<String, Object> analizarCaracteristicas(String imagenBase64) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {

			decoder(imagenBase64, System.getProperty("user.dir") + "/src/main/resources/network/" + "decoderimage.jpg");
			// MODIFICAR, PODRÃ�AMOS ENVIAR POR PARÃ�METRO EL NOMBRE DEL ARCHIVO QUE SE CREA
			// EN LA APP
			// Y CREARLO CON EL MISMO NOMBRE

			String s = null;
			String s2 = null;
			String path;
			Contenido contenido;
			String baseDir = System.getProperty("user.dir") + "/src/main/resources/network";
			String scriptDir2 = baseDir + "/DetectarContornoYExtraerCaracteristicas.py ";
			String filename = "--image=" + baseDir + "/decoderimage";
			// ENVIAR COMO PARAMETRO AL PYTHON CON EL MISMO NOMBRE QUE SE CREO CON EL
			// DECODER

			Process p2 = Runtime.getRuntime().exec("python3 " + scriptDir2 + filename);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
			while ((s2 = in2.readLine()) != null) {
				Caracteristicas[] data = new Gson().fromJson(s2, Caracteristicas[].class);

				path = data[0].getPathImagen();
				contenido = data[0].getContenido();

				map.put("asimetria", contenido.asimetria);
				map.put("diametro", contenido.diametro);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	public static <K, V extends Comparable<V>> K maxUsingIteration(Map<K, V> map) {
		Map.Entry<K, V> maxEntry = null;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
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

		public void setPathImagen(String pathImagen) {
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

		public void setAsimetria(String asimetria) {
			this.asimetria = asimetria;
		}

		public void setDiametro(float diametro) {
			this.diametro = diametro;
		}
	}
}
