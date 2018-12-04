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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventStorage
{
	private Map<String, Set<ProductAddToCart>> carts = new LinkedHashMap<>();
	private Map<String, Set<ProductAddToCart>> abandonedCarts = new LinkedHashMap<>();

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
			final HashSet<ProductAddToCart> events = new HashSet<>();
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

	public Map<String, Set<ProductAddToCart>> getCarts()
	{
		return carts;
	}



	public Map<String, Set<ProductAddToCart>> getAbandonedCarts()
	{
		return abandonedCarts;
	}

	public void addToAbandonedCarts(final String userId, final Set<ProductAddToCart> event)
	{
		log.debug("Product added to abandoned cart: " + event.toString());

		if (abandonedCarts.containsKey(userId))
		{
			abandonedCarts.get(userId).addAll(event);
		}
		else
		{
			final Set<ProductAddToCart> events = new HashSet<>();
			events.addAll(event);
			abandonedCarts.put(userId, events);
		}
	}
}
