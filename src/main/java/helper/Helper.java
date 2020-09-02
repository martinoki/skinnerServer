package helper;

import java.util.List;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class Helper {

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
			String baseDir = System.getProperty("user.dir") + "/src/main/resources/network";
			String scriptDir = baseDir + "/label_image.py ";
			String scriptDir2 = baseDir + "/DetectarContornoYExtraerCaracteristicas.py ";
			String modelDir = "--graph=" + baseDir + "/retrained_graph.pb ";
			String labelDir = "--label=" + baseDir + "/retrained_labels.txt ";
			String file = "--image=" + baseDir + "/decoderimage.jpg ";
			String filename = "--image=" + baseDir + "/decoderimage";
			// ENVIAR COMO PARAMETRO AL PYTHON CON EL MISMO NOMBRE QUE SE CREO CON EL
			// DECODER
			Process p = Runtime.getRuntime().exec("python3 " + scriptDir + modelDir + labelDir + file);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			ObjectMapper mapper = new ObjectMapper();
			while ((s = in.readLine()) != null) {

				ExpressionParser parser = new SpelExpressionParser();
				Map<String, String> results = (Map) parser.parseExpression(s).getValue();

				map.put("analisis", results.toString().replace("=",":"));
				String key = maxUsingIteration(results);
				System.out.println(results.get(key));
				if(Double.parseDouble(results.get(key)) < 0.66) {
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
