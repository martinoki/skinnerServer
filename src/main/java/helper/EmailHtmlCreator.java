package helper;

public class EmailHtmlCreator {
	public static String createBody(String title, String body) {
		return "<div style='height: 500px; background-color: lightblue; text-align: center;'>" + 
				"    <div>" + 
				"        <img style='width:150px' src='cid:logoSkinner' alt='logoSkinner'>" + 
				"    </div>" + 
				"    <div style='color: black;'>" + 
				"        <h1>"+ title +"</h1>" + 
				"    </div>" + 
				"    <div style='color: black;'>" + 
				"        <h3>"+ body +"</h3>" + 
				"    </div>" + 
				"</div>";
	}
}
