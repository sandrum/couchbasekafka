package com.paypal.cookie.utils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.codec.binary.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.paypal.cookie.utils.ServerCookieData;
import com.paypal.utils.cb.kafka.CBMessage;
import com.paypal.utils.cb.kafka.CBMessageConverter;
import com.paypal.utils.cb.kafka.ConfigLoader;
import com.paypal.utils.cb.kafka.Constants;

/**
 * Converts Couchbase message into flume friendly Event format. * 
 * @author ssudhakaran
 *
 */
public class CBCookieMessageConverterImpl extends CBMessageConverter {
	private static ObjectMapper objectMapper=null;
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetDecoder decoder = charset.newDecoder();
	
	@Override
	public  CBMessage convert(String key,String message) {
		try{
			CBMessage cbMessage=new CBMessage();
			
		if(objectMapper==null) objectMapper=new org.codehaus.jackson.map.ObjectMapper();
		
		//Specific case to extract header from Cookie
		if (key.startsWith(ConfigLoader.getProp(Constants.KEYPREFIXFILTER))){
			//System.out.println("config :"+ConfigLoader.getProp(Constants.KEYPREFIXFILTER));
			ServerCookieData cookiedata=(ServerCookieData) objectMapper.readValue(message, ServerCookieData.class);
			byte[] header=objectMapper.writeValueAsBytes(cookiedata.getHeaders());
			//String cookieMsg= StringUtils.newStringUtf8(header);
			cbMessage.setMessage(StringUtils.newStringUtf8(header));
			cbMessage.setTopic(ConfigLoader.getProp(ConfigLoader.getProp(Constants.KEYPREFIXFILTER)+Constants.TOPIC_NAME_STRING));
			return cbMessage;
		}else if (key.startsWith(ConfigLoader.getProp(Constants.ANALYTICS_KEYPREFIXFILTER))){
			cbMessage.setMessage(message);
			cbMessage.setTopic(ConfigLoader.getProp(ConfigLoader.getProp(Constants.ANALYTICS_KEYPREFIXFILTER)+Constants.TOPIC_NAME_STRING));
			return cbMessage;
			
		}else {
			//return the String we received. This is applicable for cases like
			//cs_map, analytics document.
			cbMessage.setMessage(message);
			cbMessage.setTopic(ConfigLoader.getProp(ConfigLoader.getProp(Constants.ANALYTICS_KEYPREFIXFILTER)+Constants.TOPIC_NAME_STRING));
			return cbMessage;
			//return null;
		}
		}catch(org.codehaus.jackson.JsonParseException e){
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
			
		
	}



}
