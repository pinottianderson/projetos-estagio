package test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import codigo.XmlOut;
import codigo.SaxWFReader;

public class SaxWFReaderTest extends DefaultHandler{

	SaxWFReader sax;
	XmlOut xmlOut;
	XmlOut xmlOutTest;
	private BufferedReader fileArq;
	private BufferedReader fileTestArq;
	String testIn  = "test/in";
	String testDir = "test/test.xml";
	static String testOut = "test/out/document.xml";
	
	
	@Before
	/*public void setUp() throws Exception {
		sax = new SaxWFReader();
		aktion = new Aktion();
		aktionTest = new Aktion();
		try {
			sax.parse(testIn, testOut);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}*/
	
	@Test
	public void testParse() throws IOException{
		FileReader fileTest = new FileReader(testDir);
		FileReader file = new FileReader(testOut);
		fileTestArq = new BufferedReader(fileTest);
		fileArq = new BufferedReader(file);
		String lineTest = fileTestArq.readLine();
		String lineFile = fileArq.readLine();
		assertEquals(lineTest, lineFile); 
	}	
	
	@AfterClass
	public static void deleteArq(){
		String file = testOut; 
		File f = new File(file); 
		f.delete();
	}
}