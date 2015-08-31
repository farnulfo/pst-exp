/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck
 */
public class HeaderTest {

    public HeaderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getwMagicClient method, of class Header.
     */
    @Test
    public void testHeader() throws IOException, URISyntaxException {
        PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/header.bin").toURI()));

        Header header = pstFile.getHeader();
        int dwMagic = 0x4e444221;
        assertEquals(dwMagic, header.getDwMagic());

        int dwCRCPartial = 0x379aa90e;
        assertEquals(dwCRCPartial, pstFile.getHeader().getDwCRCPartial());

        short wMagicClient = 0x4d53;
        assertEquals(wMagicClient, pstFile.getHeader().getwMagicClient());

        short wVer = 0x0017;
        assertEquals(wVer, pstFile.getHeader().getwVer());

        short wVerClient = 0x0013;
        assertEquals(wVerClient, pstFile.getHeader().getwVerClient());

        byte bPlatformCreate = 0x01;
        assertEquals(bPlatformCreate, pstFile.getHeader().getbPlatformCreate());

        byte bPlatformAccess = 0x01;
        assertEquals(bPlatformAccess, pstFile.getHeader().getbPlatformAccess());

        int dwReserved1 = 0x0000075c;
        assertEquals(dwReserved1, pstFile.getHeader().getDwReserved1());

        int dwReserved2 = 0x0b997bd0;
        assertEquals(dwReserved2, pstFile.getHeader().getDwReserved2());

        long bidUnused = 0x0000000100000004L;
        assertEquals(bidUnused, pstFile.getHeader().getBidUnused());

        long bidNextP = 0x0000000000000254L;
        assertEquals(bidNextP, pstFile.getHeader().getBidNextP());

        int dwUnique = 0x00000045;
        assertEquals(dwUnique, pstFile.getHeader().getDwUnique());

        int rgnid[] = new int[]{
            0x00000400,
            0x00000400,
            0x00000404,
            0x00004000,
            0x00010002,
            0x00000404,
            0x00000400,
            0x00000400,
            0x00008000,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000404,
            0x00000404,
            0x00000404,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x00000400,
            0x0000040f
        };
        
        assertArrayEquals(rgnid, pstFile.getHeader().getRgnid());
        
        byte bytes[] = pstFile.getHeader().getRgbFM();
        for (byte b : bytes) {
            assertEquals(0xFF, b & 0xFF);
        }

        bytes = pstFile.getHeader().getRgbFP();
        for (byte b : bytes) {
            assertEquals(0xFF, b & 0xFF);
        }

    }

}
