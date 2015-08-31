package org.pipoware.pst.exp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Franck
 */
public class Header {

    private IPSTFile pst = null;
    private final int dwMagic;
    private final int wMagicClient;

    private final byte[] DW_MAGIC_BYTE = {0x21, 0x42, 0x44, 0x4E};
    private final int DW_MAGIC = ByteBuffer.wrap(DW_MAGIC_BYTE).order(ByteOrder.LITTLE_ENDIAN).getInt();

    private final byte[] DW_MAGIC_CLIENT_BYTE = {0x53, 0x4D};
    private final short DW_MAGIC_CLIENT = ByteBuffer.wrap(DW_MAGIC_CLIENT_BYTE).order(ByteOrder.LITTLE_ENDIAN).getShort();

    private int dwCRCPartial;
    private static final int CRC_PARTIAL_BEGIN_OFFSET = 8;
    private static final int CRC_PARTIAL_DATA_SIZE = 471;
    
    private static final int ULONG_SIZE = 8;

    private short wVer;
    private short wVerClient;
    private byte bPlatformCreate;
    private byte bPlatformAccess;
    private int dwReserved1;
    private int dwReserved2;
    long bidUnused;
    long bidNextP;
    long bidNextB;
    
    int rgnid[] = new int[128];
    private long qwUnused;
    private Root root;

    public int getwMagicClient() {
        return wMagicClient;
    }

    public int getDwReserved1() {
        return dwReserved1;
    }

    public int getDwReserved2() {
        return dwReserved2;
    }

    public long getBidUnused() {
        return bidUnused;
    }

    public long getBidNextP() {
        return bidNextP;
    }

    public long getBidNextB() {
        return bidNextB;
    }

    public int getDwUnique() {
        return dwUnique;
    }

    public PST_TYPE getType() {
        return type;
    }
    private int dwUnique;

    public enum PST_TYPE {
        ANSI, UNICODE
    };
    private PST_TYPE type;

    public Header(IPSTFile pst) throws IOException {
        this.pst = pst;

        pst.position(0);

        dwMagic = pst.readDWORD();

        if (dwMagic != DW_MAGIC) {
            throw new IllegalArgumentException("Illegal Magic value : " + dwMagic + ", expected : " + DW_MAGIC);
        }

        dwCRCPartial = pst.readDWORD();
        int dwComputedCRCPartial = computeCRCPartial();

        if (dwCRCPartial != dwComputedCRCPartial) {
            throw new IllegalArgumentException("Computed CRCPartial value : " + dwComputedCRCPartial + " not equals to dwCRCPartial : " + dwCRCPartial);
        }

        wMagicClient = pst.readWORD();
        if (wMagicClient != DW_MAGIC_CLIENT) {
            throw new IllegalArgumentException("Illegal wMagicClient value : " + wMagicClient + ", expected : " + DW_MAGIC_CLIENT);
        }

        wVer = pst.readWORD();
        if ((wVer == 14) || (wVer == 15)) {
            type = PST_TYPE.ANSI;
        } else if (wVer == 23) {
            type = PST_TYPE.UNICODE;
        } else {
            throw new IllegalArgumentException("Illegal wVer value: " + wVer + ", expected : 14,15 or 23");
        }

        wVerClient = pst.readWORD();
        bPlatformCreate = pst.readBYTE();
        bPlatformAccess = pst.readBYTE();
        dwReserved1 = pst.readDWORD();
        dwReserved2 = pst.readDWORD();

        if (type == PST_TYPE.UNICODE) {
            bidUnused = pst.readULONG();
            bidNextP = pst.readULONG();
        } else if (type == PST_TYPE.ANSI) {
            bidNextP = pst.readDWORD();
            bidNextB = pst.readDWORD();
        }
        
        dwUnique = pst.readDWORD();
        
        for(int i = 0; i < rgnid.length; i ++) {
            rgnid[i] = pst.readDWORD();
        }
        
        qwUnused = pst.readULONG();
        root = new Root(pst, type);
        
    }



    private int computeCRCPartial() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(CRC_PARTIAL_DATA_SIZE);
        pst.read(buffer, CRC_PARTIAL_BEGIN_OFFSET);
        return CRC.computeCRC(0, buffer.array());
    }

    public int getDwMagic() {
        return dwMagic;
    }

    public int getDwCRCPartial() {
        return dwCRCPartial;
    }

    public short getwVer() {
        return wVer;
    }

    public short getwVerClient() {
        return wVerClient;
    }

    public byte getbPlatformCreate() {
        return bPlatformCreate;
    }

    public byte getbPlatformAccess() {
        return bPlatformAccess;
    }

}
