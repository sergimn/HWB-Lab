/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classicapplet1;

import javacard.framework.*;

/**
 *
 * @author Administrator
 */
public class Uni_Lab extends Applet {
    byte [] array;
    short data_siz;
    byte [] my_name;

    /**
     * Installs this applet.
     * 
     * @param bArray
     *            the array containing installation parameters
     * @param bOffset
     *            the starting offset in bArray
     * @param bLength
     *            the length in bytes of the parameter data in bArray
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new Uni_Lab();
    }

    /**
     * Only this class's install method should create the applet object.
     */
    protected Uni_Lab() {
        array = new byte[128];
	data_siz = 0;
        register();
    }

    /**
     * Processes an incoming APDU.
     * 
     * @see APDU
     * @param apdu
     *            the incoming APDU
     */
    public void process(APDU apdu) {
        if (selectingApplet()) {
            ISOException.throwIt(ISO7816.SW_NO_ERROR);
            return;
        }
        if((byte)apdu.getBuffer()[ISO7816.OFFSET_CLA] == (byte)0x80){
            switch(apdu.getBuffer()[ISO7816.OFFSET_INS]){
                case 0x01:
                    get_and_store_data(apdu);
                    break;
                case 0x02:
                    dump_stored_data(apdu);
                    break;
                default:
                    ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }
        }
        else ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
    }

    private void get_and_store_data(APDU apdu){
        short len = 0;
        len = apdu.setIncomingAndReceive();
        if (len >= array.length) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        Util.arrayCopy(apdu.getBuffer(), ISO7816.OFFSET_CDATA, array, (short)0, len);
	data_siz = len;
    }

    private void dump_stored_data(APDU apdu){
        short len = 0;
        len = apdu.setOutgoing();
        if (len != data_siz) ISOException.throwIt((short) (ISO7816.SW_CORRECT_LENGTH_00 + data_siz));
        apdu.setOutgoingLength(len);
        apdu.sendBytesLong(array, (short)0, len);
    }
}
