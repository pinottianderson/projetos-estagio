package test;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import codigo.SaxWFReader;

public class SaxWFReaderTest{

	SaxWFReader sax;

		
	@Before
	public void setUp() throws Exception {
		sax = new SaxWFReader();
	}
	
//	@Test
//	public void setValues(){
//		assertEquals(, );
//	}
	
	@Test
	public void remUnder(){
		assertEquals("PenumWf", sax.setParameter("pPENUM_WF"));
	}	
}