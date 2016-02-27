package codigo;
import java.io.FileOutputStream;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlOut {

	private final String NODE_PROZESSDEF = "prozessdef";
	private final String NODE_MODUL      = "modul";
	private final String NODE_BO         = "bo";
	private final String NODE_VIEW       = "view";
	private final String NODE_PARAMETER  = "parameter";
	private final String ATT_NAME        = "name";

	private final String BOS   = "bos";
	private final String VIEWS = "views";

	private TreeMap<String, TreeMap<String, TreeMap<String, TreeSet<String>>>> mapModul = new TreeMap<String, TreeMap<String, TreeMap<String, TreeSet<String>>>>();

	public void setValues(String key, TreeSet<String> value, boolean boOpen) {
		String modulName = key.trim().substring(0, key.trim().indexOf("."));
		String boName    = key.trim().substring((key.trim().indexOf(".") + 1), key.length());
		String temp      = null;
		TreeMap<String, TreeMap<String, TreeSet<String>>> mapBOs;
		TreeMap<String, TreeSet<String>> mapBo;
		TreeSet<String> setParam;
		
		if (boOpen) {
			temp = BOS;
		} else {
			temp = VIEWS;
		}

		if (mapModul.containsKey(modulName)) {
			mapBOs = mapModul.get(modulName);
		} else {
			mapBOs = new TreeMap<String, TreeMap<String, TreeSet<String>>>();
			mapModul.put(modulName, mapBOs);
		}
		if (mapBOs.containsKey(temp)) {
			mapBo = mapBOs.get(temp);
		} else {
			mapBo = new TreeMap<String, TreeSet<String>>();
			mapBOs.put(temp, mapBo);
		}
		if (mapBo.containsKey(boName)) {
			setParam = mapBo.get(boName);
		} else {
			setParam = new TreeSet<String>();
			mapBo.put(boName, setParam);
		}
		setParam.addAll(value);
	}
		
	public TreeMap<String, TreeMap<String, TreeMap<String, TreeSet<String>>>> getMapModul() {
		return mapModul;
	}
	
	/**
	 * Create output document
	 */
	public void createXml(FileOutputStream fileOut) throws ParserConfigurationException, TransformerException {
		if(!mapModul.isEmpty()){
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
	
			Element root = doc.createElement(NODE_PROZESSDEF);
			doc.appendChild(root);
			for (Entry<String, TreeMap<String, TreeMap<String, TreeSet<String>>>> tempMapModul : this.mapModul.entrySet()) {
				Element modul = doc.createElement(NODE_MODUL);
				modul.setAttribute(ATT_NAME, tempMapModul.getKey());
				root.appendChild(modul);
				for (Entry<String, TreeMap<String, TreeSet<String>>> tempMapBoS : tempMapModul.getValue().entrySet()) {
					Element bosOrViews = doc.createElement(tempMapBoS.getKey());
					modul.appendChild(bosOrViews);
					if (tempMapBoS.getKey().equals(BOS)) {
						for (Entry<String, TreeSet<String>> tempMapBo : tempMapBoS.getValue().entrySet()) {
							Element boOrView = doc.createElement(NODE_BO);
							boOrView.setAttribute(ATT_NAME, tempMapBo.getKey());
							bosOrViews.appendChild(boOrView);
							for (String set : tempMapBo.getValue()) {
								if (set.equals("INS") || set.equals("DEL") || set.equals("UPD")) {
									Element sql = doc.createElement(set);
									boOrView.appendChild(sql);
								} else {
									Element parameter = doc.createElement(NODE_PARAMETER);
									parameter.setAttribute(ATT_NAME, set);
									boOrView.appendChild(parameter);
								}
							}
						}
					} else {
						for (Entry<String, TreeSet<String>> tempMapBo : tempMapBoS.getValue().entrySet()) {
							Element boOrView = doc.createElement(NODE_VIEW);
							boOrView.setAttribute(ATT_NAME, tempMapBo.getKey());
							bosOrViews.appendChild(boOrView);
							for (String set : tempMapBo.getValue()) {
								Element parameter = doc.createElement(NODE_PARAMETER);
								parameter.setAttribute(ATT_NAME, set);
								boOrView.appendChild(parameter);
							}
						}
					}
				}
			}
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			DOMSource fontDocument = new DOMSource(doc);
			StreamResult finalDocument = new StreamResult(fileOut);
			trans.transform(fontDocument, finalDocument); 
		}
	}
}