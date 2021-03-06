package com.pyrite.cartabandonmentservice;

import java.util.Collection;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class EventUtil
{
    private static final JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields();
    
    /**
     * Handler is a convenience interface to get typed event callbacks during a JSON.parse cycle.
     */
    public interface Handler {
        void handleProductCreated(CommerceProtos.Message message, CommerceProtos.ProductCreated e);
        void handleSiteCreated(CommerceProtos.Message message, CommerceProtos.SiteCreated e);
        void handleCartCreated(CommerceProtos.Message message, CommerceProtos.CartCreated e);
        void handleCustomerCreated(CommerceProtos.Message message, CommerceProtos.CustomerCreated e);
		void handleProductAddToCart(CommerceProtos.Message message, CommerceProtos.ProductAddToCart e);
		void handleCartSuccessfulCheckout(CommerceProtos.Message message, CommerceProtos.CartSuccessfulCheckout e);
    }

    /**
     * Parse an eventlist json string and callback the events onto the given Handler interface.
     * 
     * @param json The raw json string
     * @param handler The Handler callback interface
     * @throws InvalidProtocolBufferException
     */
    public static void parseMessages(String json, Handler handler) throws InvalidProtocolBufferException {
        CommerceProtos.MessageList.Builder builder = CommerceProtos.MessageList.newBuilder();
        JsonFormat.parser().merge(json, builder);

        for (CommerceProtos.Message e : builder.build().getMessageList()) {
            handleMessage(e, handler);
        }
    }

    /**
     * Parse an Message json string and callback the Message onto the given Handler interface.
     * 
     * @param json The raw json string
     * @param handler The Handler callback interface
     * @throws InvalidProtocolBufferException
     */
    public static void parseMessage(String json, Handler handler) throws InvalidProtocolBufferException {
        CommerceProtos.Message.Builder builder = CommerceProtos.Message.newBuilder();
        JsonFormat.parser().merge(json, builder);
        handleMessage(builder.build(), handler);
    }

    /**
     * handleEvent switches on the event type and performs a callback on the given handler.
     * 
     * @param e
     * @param handler
     * @throws InvalidProtocolBufferException
     */
    public static void handleMessage(CommerceProtos.Message m, Handler handler) throws InvalidProtocolBufferException {
        CommerceProtos.Event e = m.getData();

        switch (e.getType()) {
        case UNKNOWN:
            break;
        case CUSTOMER_CREATED:
            handler.handleCustomerCreated(m, e.getCustomerCreated());
            break;
        case SITE_CREATED:
            handler.handleSiteCreated(m, e.getSiteCreated());
            break;
        case CART_CREATED:
            handler.handleCartCreated(m, e.getCartCreated());
            break;
        case PRODUCT_CREATED:
           handler.handleProductCreated(m, e.getProductCreated());
           break;
        case PRODUCT_ADD_TO_CART:
            handler.handleProductAddToCart(m, e.getProductAddToCart());
            break;
        case CART_SUCCESSFUL_CHECKOUT:
            handler.handleCartSuccessfulCheckout(m, e.getCartSuccessfulCheckout());
            break;
         default:
            throw new RuntimeException(String.format("Unknown event: %s", printer.print(e)));
        }
    }

    public static void appendAll(CommerceProtos.MessageList.Builder msgList, Collection<Object> events) {
        for (final Object e : events) {
            append(msgList, e);
        }
    }

    public static void append(CommerceProtos.MessageList.Builder msgList, Object event) {
        if (event instanceof CommerceProtos.ProductAddToCart) {
            msgList.addMessage(CommerceProtos.Message.newBuilder().build());
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
		CommerceProtos.MessageList.Builder builder = CommerceProtos.MessageList.newBuilder();

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