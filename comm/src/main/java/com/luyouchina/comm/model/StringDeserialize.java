package com.luyouchina.comm.model;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author lifajun
 *
 */
public class StringDeserialize extends StdDeserializer<String> {

	private static final long serialVersionUID = 3876087254798049117L;

	public StringDeserialize() {
		super(String.class);
	}

	/**
	 * @param p
	 * @param ctxt
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return StringEscapeUtils.unescapeHtml4(p.getText());
	}

}
