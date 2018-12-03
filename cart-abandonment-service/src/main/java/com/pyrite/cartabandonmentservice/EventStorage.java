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

import static com.pyrite.cartabandonmentservice.CommerceProtos.ProductAddToCart;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EventStorage
{
	private Map<String, List<Object>> userEvent = new LinkedHashMap<>();
	

	public void addToCartEvent(final String userId, final ProductAddToCart event)
	{
		if (userEvent.containsKey(userId))
		{
			userEvent.get(userId).add(event);
		}
		else
		{
			userEvent.put(userId, Arrays.asList(event));
		}

	}
}
