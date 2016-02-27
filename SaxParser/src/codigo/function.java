package codigo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class function {
	
	public static void main(String[] args) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		FileInputStream uriIn  = new FileInputStream("/home/pal/Área de Trabalho/xml/test/in/WFTest.xml");
		FileOutputStream uriOut = new FileOutputStream("/home/pal/Área de Trabalho/xml/out/document.xml");
		File dir = new File("/home/pal/Área de Trabalho/xml/in");
		
		SaxWFReader reader = new SaxWFReader();
		reader.parse(uriIn, uriOut);	
		//reader.parse(dir, uriOut);
	}
}
