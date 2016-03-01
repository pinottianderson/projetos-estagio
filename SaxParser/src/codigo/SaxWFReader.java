package codigo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxWFReader extends DefaultHandler {

	private XmlOut xmlOut = new XmlOut();

	private StringBuffer tempValue = new StringBuffer(50);

	private TreeSet<String> setParam;
	private TreeSet<String> setSelect;
	private TreeSet<String> setMethod;

	private String tempParam;
	private String tempClass;

	private final String NO_AKTION       = "aktion";
	private final String NO_CLASSNAME    = "classname";
	private final String NO_PARAMETER    = "parameter";
	private final String NO_MAPPING      = "mapping";
	private final String NO_BEFORE_CLOSE = "check-before-close";
	private final String NO_BEFORE_OPEN  = "check-before-open";
	private final String DO_AFTER_CLOSE  = "do-after-close";
	private final String ATT_PARAM_NAME  = "name";
	private final String ATT_CLASS_TYPE  = "type";
	private final String ENDS_WITH       = ".xml";

	private boolean mappingOpen = false;
	private boolean classOpen   = false;
	private boolean classExist  = false;
	private boolean paramOpen   = false;
	private boolean boOpen      = false;
	private boolean methodOpen  = false;
	
	private List<String> listSelect;
	private List<String> listMethod;
	

	/**
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException 
	 */
	public void parse(File dir, FileOutputStream fileOut) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].toString().endsWith(ENDS_WITH)) {
				FileInputStream fileStream = new FileInputStream(files[i]);
				parse(fileStream, fileOut);
				fileStream.close();
			}
		}
	}
	
	public void parse(FileInputStream file, FileOutputStream fileOut) throws ParserConfigurationException, SAXException, IOException, TransformerException{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser(); 
		parser.parse(file, this );
		xmlOut.createXml(fileOut);
	}
	
	public void parse(FileOutputStream fileOut) throws ParserConfigurationException, TransformerException{
		//xmlOut.createXml(fileOut);
	}

	public void startElement(String uri, String localName, String tag, Attributes atts) {
		if (tag.equals(NO_AKTION)) {
			setParam   = new TreeSet<String>();
		}
		if (tag.equals(NO_CLASSNAME)) {
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getQName(i).equals(ATT_CLASS_TYPE)) {
					if (atts.getValue(i).equals("M")) {
						boOpen = true;
						classOpen = true;
						classExist = true;
					} else if (atts.getValue(i).equals("V")) {
						boOpen = false;
						classOpen = true;
						classExist = true;
					}
				}
			}
		}
		if (tag.equals(NO_PARAMETER)) {
			if (!mappingOpen) {
				for (int i = 0; i < atts.getLength(); i++) {
					if (atts.getQName(i).equals(ATT_PARAM_NAME)) {
						tempParam = setParameter(atts.getValue(i).toLowerCase());
						paramOpen = true;
					}
				}
			}
		}
		if (tag.equals(NO_MAPPING)) {
			mappingOpen = true;
		}
		if(tag.equals(NO_BEFORE_CLOSE) || tag.equals(NO_BEFORE_OPEN) || tag.equals(NO_MAPPING) || tag.equals(DO_AFTER_CLOSE)){
			listSelect = new ArrayList<String>();
			listMethod = new ArrayList<String>();
		}
	}

	public void endElement(String uri, String localName, String tag) {
		if (tag.equals(NO_AKTION)) {
			if (classOpen) {
				xmlOut.setValues(tempClass, setParam, boOpen);
			}
			classOpen = false;
			if (boOpen) {
				boOpen = false;
			}
			methodOpen = false;
		}
		else if (tag.equals(NO_CLASSNAME)) {
			if (classOpen && classExist) {
				tempClass = setValues(tempValue.toString().trim());
			}
			classExist = false;
		}
		else if (tag.equals(NO_PARAMETER)) {
			if (paramOpen && !mappingOpen) {
				setParam.add(tempParam);
			}
			paramOpen   = false;
			mappingOpen = false;
		}
		else if (tag.equals(NO_BEFORE_CLOSE) || tag.equals(NO_BEFORE_OPEN) || tag.equals(NO_MAPPING) || tag.equals(DO_AFTER_CLOSE)) {
			verifySelectOrMethod(tempValue.toString().trim());
			if(classOpen && !listMethod.isEmpty()){
				for(String str : listMethod){
					setMethod(str);
				}
			}
			if(classOpen && !listSelect.isEmpty()){
				for(String str : listSelect){
					setSelect(str);
				}
			}
		}
		tempValue.delete(0, tempValue.length());
	}

	public void characters(char[] ch, int start, int length) {
		tempValue.append(ch, start, length);
	}

	/**
	 * Verify the content of tempValue
	 * @param temp
	 * @return
	 */
	public String setValues(String temp) {
		String method        = "";
		String tempClassname = "";
		String tempModul     = "";
		String tempBo        = "";
		if (temp.trim().startsWith("ch.")) {
			temp = temp.substring(12, temp.length());
			tempModul = temp.trim().substring(0, temp.indexOf(".")+1);
			tempBo = temp.substring((temp.lastIndexOf(".") + 1), temp.length());
			if (tempBo.contains("View")) {
				tempBo = tempBo.substring(0, (tempBo.length() - 4));
			} else {
				method = tempBo.substring((tempBo.length() - 9), tempBo.length());
				method = method.substring(0, 3);
				tempBo = tempBo.substring(0, (tempBo.length() - 9));
				paramOpen = true;
				setParam.add(method);
				paramOpen = false;
			}
			tempClassname = tempModul + tempBo;
			return tempClassname;
		} else if (temp.contains("/")) {
			method = temp.trim().substring((temp.trim().length() - 4), temp.trim().length());
			method = method.substring(1, method.length());
			tempClassname = temp.trim().substring(0, temp.trim().length() - 4);
			paramOpen = true;
			setParam.add(method);
			paramOpen = false;
			return tempClassname;
		} else {
			tempClassname = temp.trim();
			return tempClassname;
		}
	}
	
	public void verifySelectOrMethod(String temp) {
		if (temp.trim().contains("@SELECT")) {
			checkSelect(tempValue.toString().trim());
		}
		if (temp.trim().contains("@METHOD")) {
			checkMethod(tempValue.toString().trim());
		}
	}

	/**
	 * @param temp
	 */
	public void checkSelect(String temp) {
		String temporary = "";
		int cont = 0;
		temp = temp.trim().substring(temp.trim().indexOf("@SELECT"), temp.trim().length());
		for (String st : temp.split("@SELECT")) {
			if (!st.isEmpty()) {
				st = st.trim().substring(2, st.trim().length());
				setSelect = new TreeSet<String>();
				if (st.startsWith("p")) {
					st = st.substring(0, st.indexOf("]"));
					for (String str : st.toString().split("',")) {
						cont++;
						if (cont <= 3) {
							temporary += str;
							temporary = temporary.replaceAll(" ", "");
							temporary = temporary.replaceAll("\\s+$", "");
						}
					}
					cont = 0;
					listSelect.add(temporary);
					temporary = "";
				}				
			}
		}
	}

	/**
	 * Get the content of checkSelect and send to the aktion object
	 * @param temp
	 */
	public void setSelect(String temp) {
		String bo        = "";
		String modul     = "";
		String param     = "";
		String tempmodul = "";
		for (String str : temp.split("'")) {
			if (param.isEmpty()) {
				param = str;
				param = setParameter(param);
				setSelect.add(param);
			} else if (modul.isEmpty()) {
				modul = str;
			} else if (bo.isEmpty()) {
				bo = setParameter(str);
			}
		}
		tempmodul = modul + "." + bo;
		xmlOut.setValues(tempmodul, setSelect, boOpen);
	}

	/**
	 * @param temp
	 */
	public void checkMethod(String temp) {
		methodOpen = true;
		temp = temp.trim().substring(temp.trim().indexOf("@METHOD"), temp.trim().length());
		for (String st : temp.split("@METHOD")) {
			if (!st.isEmpty()) {
				st = st.replaceAll(" ", "");
				st = st.replaceAll("\\s+$", "");
				st = st.trim().substring(1, (st.trim().length()-1));
				listMethod.add(st.trim());
			}
		}
	}

	/**
	 * Get the content of checkMethod and send to the xmlOut object
	 * @param temp
	 */
	public void setMethod(String temp) {
		String bo        = "";
		String modul     = "";
		String param     = "";
		String method    = "";
		String tempModul = "";
		setMethod = new TreeSet<String>();
		for (String str : temp.trim().split(",")) {
			if (str.startsWith("'") && str.endsWith("'")) {
				str = str.trim().substring(1, str.trim().length() - 1);
				if (param.isEmpty()) {
					param = str;
					param = setParameter(param);
					setMethod.add(param);
				} else if (modul.isEmpty()) {
					modul = str;
				} else if (bo.isEmpty()) {
					bo = setParameter(str);
				} else if (method.isEmpty()) {
					method = str;
					setMethod.add(method);
				} else {
					str = setParameter(str);
					setMethod.add(str);
				}
			}
		}
		tempModul = modul + "." + bo;
		xmlOut.setValues(tempModul, setMethod, methodOpen);
	}
	
	/**
	 * Edit String of parameter
	 * @param str
	 * @return
	 */
	public String setParameter(String str){
		String result = "";
		str = str.substring(1, str.length());
		str = str.toLowerCase();
		if(str.contains("_")){
			String[] st = str.split("_");
			for(String string : st){
				result = result + string.replaceFirst(string.substring(0, 1), string.substring(0, 1).toUpperCase());
			}
			result = result.replaceAll("_", "");
			return result.trim();
		}else{
			str = str.replaceFirst(str.substring(0, 1), str.substring(0,1).toUpperCase());
			return str;
		}
	}
}