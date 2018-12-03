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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CartAbandonmentEventHandler implements EventUtil.Handler
{
	@Autowired
	private EventStorage eventStorage;

	@Override
	public void handleProductCreated(final ProductCreated e)
	{

	}

	@Override
	public void handleSiteCreated(final SiteCreated e)
	{

	}

	@Override
	public void handleCustomerCreated(final CustomerCreated e)
	{

	}

	@Override
	public void handleProductAddToCart(final ProductAddToCart e)
	{
		eventStorage.addToCartEvent(e.getUserId(), e);
	}
}
