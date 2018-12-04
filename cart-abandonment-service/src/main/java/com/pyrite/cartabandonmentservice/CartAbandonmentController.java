/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.pyrite.cartabandonmentservice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

@RestController
@RequestMapping("/cartAbandonment")
public class CartAbandonmentController
{
	@Autowired
	private CartAbandonmentEventHandler eventHandler;
	@Autowired
	private EventStorage eventStorage;

	@RequestMapping("/hello")
	@ResponseBody
	public String hello()
	{
		return "What's up?";
	}

	@RequestMapping("/hey")
	@ResponseBody
	public String hey()
	{
		return "Another test endpoint";
	}

	@PostMapping("/events")
	@ResponseBody
	public String events(final HttpServletRequest request) throws IOException
	{
		final String json = IOUtils.toString(request.getInputStream(), "UTF-8");

		EventUtil.parseMessages(json, eventHandler);

		return "Processed";
	}

	@GetMapping("/events")
	@ResponseBody
	public String getEvents()
	{
		final JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields();

		StringBuilder responseBuilder = new StringBuilder();

		final Map<String, List<Object>> userEvents = eventStorage.getUserEvents();
		userEvents.values().stream().forEach(value -> {

			value.stream().forEach(cart -> {
				try
				{
					responseBuilder.append(printer.print((MessageOrBuilder) cart));
				}
				catch (InvalidProtocolBufferException e)
				{
					e.printStackTrace();
				}
			});
		});

		return responseBuilder.toString();
	}
}

