package codigo;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class function {
	
	public static void main(String[] args) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		String uriIn  = "/home/pal/�rea de Trabalho/xml/in";
		String uriOut = "/home/pal/�rea de Trabalho/xml/out/document.xml";
		File dir = new File("/home/pal/�rea de Trabalho/xml/in");
		
		SaxWFReader reader = new SaxWFReader();
		reader.parse(uriIn);	
		reader.parse(dir);
	}
}
