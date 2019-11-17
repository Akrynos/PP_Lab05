package ekstra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import java.util.Iterator;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ItemProvider implements Iterator<Item>, AutoCloseable {

    private final Reader reader;
    private final XMLStreamReader xml;
    private boolean isItem = false;

    public ItemProvider(String filename) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        File f = new File(filename);
        try {
            reader = new InputStreamReader(new FileInputStream(f), "UTF-8");
            xml = factory.createXMLStreamReader(reader);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean hasNext() {
        if (isItem) {
            return true;
        }
        try {
            while (xml.hasNext()) {
                xml.next();
                if (xml.getEventType() == XMLStreamReader.START_ELEMENT
                        && "item".equals(xml.getLocalName())) {
                    isItem = true;
                    return true;
                }
            }
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }

    @Override
    public Item next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            Item item = new Item();
            while (xml.hasNext()) {
                xml.next();
                if (xml.getEventType() == XMLStreamReader.END_ELEMENT
                        && "item".equals(xml.getLocalName())) {
                    isItem = false;
                    break;
                }
                if (xml.getEventType() == XMLStreamReader.START_ELEMENT
                        && "id".equals(xml.getLocalName())) {
                    item.setId(Integer.parseInt(xml.getElementText()));
                }
                if (xml.getEventType() == XMLStreamReader.START_ELEMENT
                        && "price".equals(xml.getLocalName())) {
                    item.setPrice(Float.parseFloat(xml.getElementText()));
                }
                if (xml.getEventType() == XMLStreamReader.START_ELEMENT
                        && "name".equals(xml.getLocalName())) {
                    item.setName(xml.getElementText());
                }
                if (xml.getEventType() == XMLStreamReader.START_ELEMENT
                        && "category".equals(xml.getLocalName())) {
                    item.setCategory(xml.getElementText());
                }
                if (xml.getEventType() == XMLStreamReader.START_ELEMENT
                        && "description".equals(xml.getLocalName())) {
                    item.setDescription(xml.getElementText());
                }
            }
            return item;
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void remove() {

    }

    @Override
    public void close() throws XMLStreamException, IOException {
        try {
            xml.close();
        } finally {
            reader.close();
        }
    }

}
