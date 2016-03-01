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
	
	@Test
	public void setValues(){
		assertEquals("pvclan.MassenStellenwechsel", sax.setValues("ch.claninfo.pvclan.gui.generated.view.MassenStellenwechselView"));
		assertEquals("EinkaufPotenz", sax.setValues("EinkaufPotenz"));
		assertEquals("pvclan.FibuzaeinWf", sax.setValues("pvclan.FibuzaeinWf"));
	}
	
	@Test
	public void setParameter(){
		assertEquals("PenumWf", sax.setParameter("pPENUM_WF"));
		assertEquals("Penum", sax.setParameter("pPENUM"));
	}
}