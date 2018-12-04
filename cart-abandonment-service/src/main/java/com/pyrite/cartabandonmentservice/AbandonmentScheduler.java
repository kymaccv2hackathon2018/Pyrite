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

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AbandonmentScheduler
{
	public static final int ABANDONED_CART_TIME = 120000;
	@Autowired
	private EventStorage eventStorage;


	private static final Logger log = LoggerFactory.getLogger(AbandonmentScheduler.class);

	@Scheduled(fixedRate = 30000)
	public void doThis()
	{
		log.warn("ABANDONMENT SCHEDULER TRIGGERED");
		final Map<String, List<CommerceProtos.ProductAddToCart>> userEvents = eventStorage.getCarts();

		final Set<String> users = userEvents.keySet();

		users.forEach(user -> evaluateUserCart(user, userEvents));
	}

	private void evaluateUserCart(final String userId, final Map<String, List<CommerceProtos.ProductAddToCart>> userEvents)
	{
		final List<CommerceProtos.ProductAddToCart> products = userEvents.get(userId);
		final Optional<Date> activeProductsInCart = products.stream().map(p -> {
			try
			{
				return parseDate(p.getEventTime());
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			return null;
		}).filter(date -> productDateActive(date)).findAny();


		if (!activeProductsInCart.isPresent())
		{
			log.warn("Abandoning Cart for user: " + userId);
			// remove userId from event map
			eventStorage.removeByUser(userId);
			// add userId to abandoned map
			eventStorage.addToAbandonedCarts(userId, products);
			// generate event
		}
	}

	public Date parseDate(final String date) throws ParseException
	{
		final ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
		return Date.from(zonedDateTime.toInstant());
	}

	private boolean productDateActive(final Date date)
	{
		final long timeDifferenceInMs = System.currentTimeMillis() - date.getTime();
		if (timeDifferenceInMs < ABANDONED_CART_TIME)
		{
			return true;
		}
		return false;
	}
}
