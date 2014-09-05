option java_package = "com.handwin.config.proto";
option java_outer_classname = "MessageProto";


message ConfigMessage {
	optional string region = 1;
	optional string business = 2;
	optional string content = 3;
	optional int32  sequence = 4 ;
}


