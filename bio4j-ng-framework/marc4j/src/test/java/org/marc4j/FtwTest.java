package org.marc4j;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.marc4j.utils.StaticTestRecords;
import org.marc4j.utils.TestUtils;

import java.io.InputStream;

import static org.junit.Assert.*;

public class FtwTest {

    @Test
    public void testMarcStreamReader() throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(StaticTestRecords.RESOURCES_CHABON_MRC);
        assertNotNull(input);

        MarcStreamReader reader = new MarcStreamReader(input);
        assertTrue("Should have at least one record", reader.hasNext());

        Record record1 = reader.next();
        TestUtils.validateKavalieAndClayRecord(record1);

        assertTrue("Should have at least two records", reader.hasNext());
        Record record2 = reader.next();
        TestUtils.validateSummerlandRecord(record2);

        assertFalse(" have more than two records", reader.hasNext());
        input.close();
    }


}
