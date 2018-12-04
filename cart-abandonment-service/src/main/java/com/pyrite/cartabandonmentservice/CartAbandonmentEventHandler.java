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

import static com.pyrite.cartabandonmentservice.CommerceProtos.CustomerCreated;
import static com.pyrite.cartabandonmentservice.CommerceProtos.ProductAddToCart;
import static com.pyrite.cartabandonmentservice.CommerceProtos.ProductCreated;
import static com.pyrite.cartabandonmentservice.CommerceProtos.SiteCreated;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CartAbandonmentEventHandler implements EventUtil.Handler
{
	@Autowired
	private EventStorage eventStorage;

	/**
	 * A mapping where values are the outer wrapper messages keyed by their
	 * inner event object type.
	 */
	private Map<Object, CommerceProtos.Message> messages = new LinkedHashMap();

	@Override
	public void handleProductCreated(final CommerceProtos.Message message, final ProductCreated e)
	{
		messages.put(e, message);
	}

	@Override
	public void handleSiteCreated(final CommerceProtos.Message message, final SiteCreated e)
	{
		messages.put(e, message);
	}

	@Override
	public void handleCartCreated(final CommerceProtos.Message message, final CommerceProtos.CartCreated e)
	{
		messages.put(e, message);
	}

	@Override
	public void handleCustomerCreated(final CommerceProtos.Message message, final CustomerCreated e)
	{
		messages.put(e, message);
	}

	@Override
	public void handleProductAddToCart(final CommerceProtos.Message message, final ProductAddToCart e)
	{
		messages.put(e, message);
		eventStorage.addToCartEvent(e.getUserId(), e);
	}

	@Override
	public void handleCartSuccessfulCheckout(final CommerceProtos.Message message, final CommerceProtos.CartSuccessfulCheckout e)
	{
		messages.put(e, message);
		eventStorage.successfulCheckout(e.getUserId());
	}

	/**
	 * Return all events known to the EventHandler
	 */
	public CommerceProtos.MessageList getAllMessages() {
		CommerceProtos.MessageList.Builder builder = CommerceProtos.MessageList.newBuilder();
		for (CommerceProtos.Message value : this.messages.values()) {
			builder.addMessage(value);
		}
		return builder.build();
	}

}
