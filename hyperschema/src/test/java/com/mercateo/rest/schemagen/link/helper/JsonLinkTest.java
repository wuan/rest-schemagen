package com.mercateo.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.ws.rs.core.Link;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.TextNode;
import com.mercateo.rest.schemagen.link.LinkCreator;

public class JsonLinkTest {

	public static final String SCHEMA_VALUE = "{\"type\":\"schema\"}";

	public static final String TARGET_SCHEMA_VALUE = "{\"type\":\"targetSchema\"}";

	private JsonLink jsonLink;

	private String uriString;

	private JsonLink jsonLinkTemplate;

	private String uriStringTemplate;

	@Before
	public void setUp() throws IOException {
		uriString = "https://localhost:1234/base?parm1=val1&parm2=val2#test";
		jsonLink = new JsonLink(Link //
				.fromUri(uriString) //
				.param(LinkCreator.SCHEMA_PARAM_KEY, SCHEMA_VALUE) //
				.param(LinkCreator.TARGET_SCHEMA_PARAM_KEY, TARGET_SCHEMA_VALUE) //
				.param("foo", "bar") //
				.build());

		uriStringTemplate = "https://localhost:1234/{id}/base?parm1=val1&parm2=val2#test";
				jsonLinkTemplate = new JsonLink(Link //
								.fromUri(uriStringTemplate) //
								.param(LinkCreator.SCHEMA_PARAM_KEY, SCHEMA_VALUE) //
								.param(LinkCreator.TARGET_SCHEMA_PARAM_KEY, TARGET_SCHEMA_VALUE) //
								.param("foo", "bar") //
								.build("{id}"));
	}

	@Test
	public void testGetHref() {
		Assertions.assertThat(jsonLink.getHref()).isEqualTo(uriString);
	}

	@Test
	public void testTemplate() {
			Assertions.assertThat(jsonLinkTemplate.getHref()).isEqualTo(uriStringTemplate);
	}

	@Test
	public void testSetHref() {
		jsonLink.setHref("foo");
		Assertions.assertThat(jsonLink.getHref()).isEqualTo("foo");
	}

	@Test
	public void testGetSchema() {
		Assertions.assertThat(jsonLink.getSchema().toString()).isEqualTo(SCHEMA_VALUE);
	}

	@Test
	public void testSetSchema() {
		jsonLink.setSchema(new TextNode("foo"));
		Assertions.assertThat(jsonLink.getSchema().toString()).isEqualTo("\"foo\"");
	}

	@Test
	public void testGetTargetSchema() {
		Assertions.assertThat(jsonLink.getTargetSchema().toString()).isEqualTo(TARGET_SCHEMA_VALUE);
	}

	@Test
	public void testSetTargetSchema() {
		jsonLink.setTargetSchema(new TextNode("bar"));
		Assertions.assertThat(jsonLink.getTargetSchema().toString()).isEqualTo("\"bar\"");
	}

	@Test
	public void testGetMap() {
		Assertions.assertThat(jsonLink.getMap()).hasSize(1).containsEntry("foo", "bar");
	}

	@Test
	public void testSetMap() {
		jsonLink.setMap("eins", "zwei");
		Assertions.assertThat(jsonLink.getMap()).containsKey("eins");
	}

	@Test
	public void testWithoutSchemaValues() throws IOException {
		jsonLink = new JsonLink(Link.fromUri(uriString).build());

		Assertions.assertThat(jsonLink.getSchema()).isNull();
		Assertions.assertThat(jsonLink.getTargetSchema()).isNull();
	}

}