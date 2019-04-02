package com.luyouchina.comm.model;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author lifajun
 *
 */
public class StringSerializer extends StdSerializer<String> {

	private static final long serialVersionUID = 5155238973074256671L;

	public StringSerializer() {
		super(String.class);
	}

	/**
	 * @param value
	 * @param gen
	 * @param serializers
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeString(StringEscapeUtils.escapeHtml4(value));
	}

}
