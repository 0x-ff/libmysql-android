package android.util.xmlutils;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

import java.io.Writer;
import java.io.IOException;
import java.io.FileNotFoundException;
 
// велосипед для записи XML документа в поток вывода
public class XmlDocumentWriter {
	 
	Document      doc;
	XmlSerializer xml;

	public XmlDocumentWriter(Document d) {
		doc = d;
	}
	
	public boolean write(Writer writer) throws IOException {
		
		xml = Xml.newSerializer();
		xml.setOutput(writer);
		xml.startDocument("UTF-8", true);
		Element element = doc.getDocumentElement();
		if ( !writeRecursive(element) ) {
			return false;
		}
		xml.endDocument();
		return true;
	}
	
	protected boolean writeRecursive(Element element) throws IOException {
		
		xml.startTag("", element.getTagName());
		NamedNodeMap attrs = element.getAttributes();
		
		for (int j = 0; j < attrs.getLength(); j++ ) {

			Attr attr = (Attr) attrs.item(j);
			String val = attr.getValue();
			xml.attribute("", attr.getName(), (val == null ? "" : val));
		}

		NodeList childs = element.getChildNodes();
		String txt = "";
		for (int i = 0; i < childs.getLength(); i++) {

			Node child = childs.item(i);
			short nodeType = child.getNodeType();
			if (nodeType == Node.ELEMENT_NODE) {
				
				if (!writeRecursive((Element)child)) {
					return false;
				}
			} else if (nodeType == Node.CDATA_SECTION_NODE) {
				txt = child.getNodeValue();
				if (txt != null) {
					xml.cdsect(txt);
				}
			} else {
				txt = child.getNodeValue();
				if (txt != null) {
					xml.text(txt);
				}
			}
		}
		
		xml.endTag("", element.getTagName());
		return true;
	}
};

