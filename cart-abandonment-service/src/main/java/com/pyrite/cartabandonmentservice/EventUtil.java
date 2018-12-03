package com.pyrite.cartabandonmentservice;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class EventUtil
{

	private static final JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields();

	/**
	 * Handler is a convenience interface to get typed event callbacks during a JSON.parse cycle.
	 */
	public interface Handler
	{
		void handleProductCreated(CommerceProtos.ProductCreated e);

		void handleSiteCreated(CommerceProtos.SiteCreated e);

		void handleCustomerCreated(CommerceProtos.CustomerCreated e);

		void handleProductAddToCart(CommerceProtos.ProductAddToCart e);
	}

	/**
	 * Parse an eventlist json string and callback the events onto the given Handler interface.
	 *
	 * @param json The raw json string
	 * @param handler The Handler callback interface
	 * @throws InvalidProtocolBufferException
	 */
	public static void parseEventList(String json, Handler handler) throws InvalidProtocolBufferException
	{
		CommerceProtos.EventList.Builder builder = CommerceProtos.EventList.newBuilder();
		JsonFormat.parser().merge(json, builder);

		for (CommerceProtos.Event e : builder.build().getEventList())
		{
			handleEvent(e, handler);
		}
	}

	/**
	 * Parse an event json string and callback the event onto the given Handler interface.
	 *
	 * @param json The raw json string
	 * @param handler The Handler callback interface
	 * @throws InvalidProtocolBufferException
	 */
	public static void parseEvent(String json, Handler handler) throws InvalidProtocolBufferException
	{
		CommerceProtos.Event.Builder builder = CommerceProtos.Event.newBuilder();
		JsonFormat.parser().merge(json, builder);
		handleEvent(builder.build(), handler);
	}

	/**
	 * handleEvent switches on the event type and performs a callback on the given handler.
	 *
	 * @param e
	 * @param handler
	 * @throws InvalidProtocolBufferException
	 */
	public static void handleEvent(CommerceProtos.Event e, Handler handler) throws InvalidProtocolBufferException
	{
		switch (e.getType())
		{
			case UNKNOWN:
				break;
			case CUSTOMER_CREATED:
				handler.handleCustomerCreated(e.getCustomerCreated());
				break;
			case SITE_CREATED:
				handler.handleSiteCreated(e.getSiteCreated());
				break;
			case PRODUCT_CREATED:
				handler.handleProductCreated(e.getProductCreated());
				break;
			case PRODUCT_ADD_TO_CART:
				handler.handleProductAddToCart(e.getProductAddToCart());
				break;
			default:
				throw new RuntimeException(String.format("Unknown event: %s", printer.print(e)));
		}
	}

	/**
	 * Main entrypoint can be used to play around with json string parsing/formatting.
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.err.format("Error: raw json string representing an event list should be first argument\n");
			System.err.format("Usage: $0 '{ \"event\": [...] }'\n");
			System.exit(1);
		}

		String json_string = args[0];
		CommerceProtos.EventList.Builder builder = CommerceProtos.EventList.newBuilder();

		try
		{
			JsonFormat.parser().merge(json_string, builder);
			System.out.format("%s\n", printer.print(builder));
		}
		catch (InvalidProtocolBufferException ex)
		{
			ex.printStackTrace();
		}
	}
}