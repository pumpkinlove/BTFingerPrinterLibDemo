package com.miaxis.btfingerprinterlib.utils;

/**
 * Created by xu.nan on 2017/8/17.
 */

public class ProtocolUtil {

    public static String hex2String(String hex) throws Exception{
        String r = bytes2String(hexString2Bytes(hex));
        return r;
    }

    public static String bytes2String(byte[] b) throws Exception {
        String r = new String (b,"UTF-8");
        return r;
    }

    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }

    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] getReqOrder(byte protocolOrderCode, byte[] data) {
        int dataLen;
        if (data == null) {
            dataLen = 0;
        } else {
            dataLen = data.length;
        }
        int orderLen = 4 + dataLen;
        int totalLen = 1 + 2 + orderLen + 1 + 1;

        byte[] reqBytes = new byte[totalLen];
        reqBytes[0] = ProtocolOrderCode.PROTOCOL_HEAD;

        if (dataLen < 256) {
            reqBytes[1] = (byte) 0x00;               //长度从P1到数据结束的长度,用2字节表示，小于256字节用字节3表示，字节2补零,大于256字节用字节3和字节2表示
            reqBytes[2] = (byte) orderLen;           //长度
        } else {
            reqBytes[1] = (byte) (dataLen - 255);
            reqBytes[2] = (byte) 0xff;
        }

        reqBytes[3] = protocolOrderCode;        //P1  约定的指纹设备命令
        reqBytes[4] = (byte) 0x00;              //P2  保留
        reqBytes[5] = (byte) 0x00;              //P3  保留
        reqBytes[6] = (byte) 0x00;              //P4  P4 =0x5A且开启扩展串口时 转发到扩展口，其他无意义

        for (int i = 0; i < dataLen; i ++) {
            reqBytes[7 + i] = data[i];
        }

        reqBytes[totalLen - 2] = CodeUtil.getXorCheckCode(reqBytes);            //校验和
        reqBytes[totalLen - 1] = ProtocolOrderCode.PROTOCOL_END;
        return reqBytes;
    }

}
