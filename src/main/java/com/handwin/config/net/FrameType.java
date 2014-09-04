package com.handwin.config.net;

import java.util.HashMap;

/**
 * 
 * @author fangliang
 *
 */
public enum FrameType {
	
	 Ping( (byte)0x01 ) ,
     Pong( (byte)0x02 ) ,
     ConfigQuery((byte)0x03) ,
     ConfigSet((byte)0x04) ; 
     
     private byte type ;
     private FrameType(byte type) {
             this.type = type ;
     }
     public byte getType() {
             return type;
     }

     public final static int FRAME_TYPE_BYTES_LENGTH = 1 ;
     
     private static HashMap< Byte, FrameType > FRAME_HASH_TYPES = new  HashMap< Byte, FrameType >() ;
     static {
             for( FrameType ft : FrameType.values() ) {
                     FRAME_HASH_TYPES.put(ft.getType(), ft ) ;
             }
     }
     public static FrameType getFrameType( byte type ) {
             return FRAME_HASH_TYPES.get( type ) ;
     }

}
