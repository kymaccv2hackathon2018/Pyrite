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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventStorage
{
	private Map<String, List<ProductAddToCart>> carts = new LinkedHashMap<>();
	private Map<String, List<ProductAddToCart>> abandonedCarts = new LinkedHashMap<>();

	private static final Logger log = LoggerFactory.getLogger(AbandonmentScheduler.class);


	public void addToCartEvent(final String userId, final ProductAddToCart event)
	{
		log.debug("Product added to cart: " + event.toString());

		if (carts.containsKey(userId))
		{
			carts.get(userId).add(event);
		}
		else
		{
			final ArrayList<ProductAddToCart> events = new ArrayList<>();
			events.add(event);
			carts.put(userId, events);
		}
	}

	public void removeByUser(final String userId)
	{
		if (carts.containsKey(userId))
		{
			carts.remove(userId);
		}
	}

	public Map<String, List<ProductAddToCart>> getCarts()
	{
		return carts;
	}

	public void addToAbandonedCarts(final String userId, final List<ProductAddToCart> event)
	{
		log.debug("Product added to abandoned cart: " + event.toString());

		if (carts.containsKey(userId))
		{
			carts.get(userId).addAll(event);
		}
		else
		{
			final ArrayList<ProductAddToCart> events = new ArrayList<>();
			events.addAll(event);
			carts.put(userId, events);
		}
	}
}
