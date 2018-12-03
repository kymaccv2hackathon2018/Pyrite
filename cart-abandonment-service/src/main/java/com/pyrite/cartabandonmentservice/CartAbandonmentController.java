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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartAbandonment")
public class CartAbandonmentController
{

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
	
	@RequestMapping("/events")
	@ResponseBody
	public String events()
	{
		String json = "{ \"event\": [] }";

		EventUtil.parseEventList(json, this);

		return "Processed";
	}

	@Override 
	public void handleProductCreated(CommerceProtos.ProductCreated event) {
	}

}

