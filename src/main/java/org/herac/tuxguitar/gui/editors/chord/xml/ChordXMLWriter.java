package org.herac.tuxguitar.gui.editors.chord.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.herac.tuxguitar.song.models.TGChord;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ChordXMLWriter {

  public static Document createDocument() {
    Document document = null;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.newDocument();
    } catch (ParserConfigurationException pce) {
      LOG.error(pce);
    }
    return document;
  }
  /** The Logger for this class. */
  public static final transient Logger LOG = Logger.getLogger(ChordXMLWriter.class);


  public static void saveDocument(Document document, File file) {
    try {
      FileOutputStream fs = new FileOutputStream(file);

      // Write it out again
      TransformerFactory xformFactory = TransformerFactory.newInstance();
      Transformer idTransform = xformFactory.newTransformer();
      Source input = new DOMSource(document);
      Result output = new StreamResult(fs);
      idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
      idTransform.transform(input, output);
    } catch (FactoryConfigurationError e) {
      LOG.error(e);
    } catch (TransformerConfigurationException e) {
      LOG.error(e);
    } catch (TransformerException e) {
      LOG.error(e);
    } catch (FileNotFoundException e) {
      LOG.error(e);
    }
  }

  /**
   * Write chords to xml file
   */
  private static void setChords(List<TGChord> chords, Document document) {
    // chords tag
    Node chordsNode = document.createElement(ChordXML.CHORD_LIST_TAG);

    for (final TGChord chord : chords) {

      // chord tag
      Node chordNode = document.createElement(ChordXML.CHORD_TAG);
      chordsNode.appendChild(chordNode);

      // name attribute
      Attr nameAttr = document.createAttribute(ChordXML.CHORD_NAME_ATTRIBUTE);
      nameAttr.setNodeValue(chord.getName());
      chordNode.getAttributes().setNamedItem(nameAttr);

      // strings attribute
      Attr stringsAttr = document
          .createAttribute(ChordXML.CHORD_STRINGS_ATTRIBUTE);
      stringsAttr.setNodeValue(Integer.toString(chord.getStrings().length));
      chordNode.getAttributes().setNamedItem(stringsAttr);

      // first fret attribute
      Attr firstFretAttr = document
          .createAttribute(ChordXML.CHORD_FIRST_FRET_ATTRIBUTE);
      firstFretAttr.setNodeValue(Integer.toString(chord.getFirstFret()));
      chordNode.getAttributes().setNamedItem(firstFretAttr);

      for (int i = 0; i < chord.getStrings().length; i++) {
        // string tag
        Node stringNode = document.createElement(ChordXML.STRING_TAG);
        chordNode.appendChild(stringNode);

        // number attribute
        Attr numberAttr = document
            .createAttribute(ChordXML.STRING_NUMBER_ATTRIBUTE);
        numberAttr.setNodeValue(Integer.toString(i));
        stringNode.getAttributes().setNamedItem(numberAttr);

        // fret attribute
        Attr fretAttr = document
            .createAttribute(ChordXML.STRING_FRET_ATTRIBUTE);
        fretAttr.setNodeValue(Integer.toString(chord.getFretValue(i)));
        stringNode.getAttributes().setNamedItem(fretAttr);
      }
    }

    document.appendChild(chordsNode);
  }

  public static void setChords(List<TGChord> chords, String fileName) {
    File file = new File(fileName);

    Document doc = createDocument();
    setChords(chords, doc);
    saveDocument(doc, file);
  }
}
