package openfasttrack.importer.specobject;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import openfasttrack.core.SpecificationItem;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

public class SpecobjectImporter implements Importer
{
    private final static Logger LOG = Logger.getLogger(SpecobjectImporter.class.getName());
    private final XMLInputFactory xmlInputFactory;
    private final Reader reader;

    public SpecobjectImporter(final Reader reader, final XMLInputFactory xmlInputFactory)
    {
        this.xmlInputFactory = xmlInputFactory;
        this.reader = new BufferedReader(reader);
    }

    @Override
    public List<SpecificationItem> runImport()
    {
        try
        {
            final XMLEventReader xmlEventReader = this.xmlInputFactory
                    .createXMLEventReader(this.reader);
            final ImportHelper importHelper = new ImportHelper(xmlEventReader);
            importHelper.runImport();
            return importHelper.getItems();
        } catch (final XMLStreamException e)
        {
            throw new ImporterException("Error importing specobjects document", e);
        }
    }
}
