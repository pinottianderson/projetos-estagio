package codigo;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class function {
	
	static String uriIn  = "/home/pal/Área de Trabalho/xml/in";
	static String uriOut = "/home/pal/Área de Trabalho/xml/out/document.xml";
		
	public static void main(String[] args) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		SaxWFReader reader = new SaxWFReader();
		reader.parse(uriIn);	
	}
}
