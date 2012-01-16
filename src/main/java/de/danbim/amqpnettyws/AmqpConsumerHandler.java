package de.danbim.amqpnettyws;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmqpConsumerHandler extends SimpleChannelUpstreamHandler {

	private static final Logger log = LoggerFactory.getLogger(AmqpConsumerHandler.class);

	private final EventBus eventBus;

	private ChannelHandlerContext ctx;

	public AmqpConsumerHandler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {

		log.debug("{}", e);
		this.ctx = ctx;
		this.eventBus.register(this);

		ctx.sendUpstream(e);
	}

	@Override
	public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {

		log.debug("{}", e);
		this.eventBus.unregister(this);
		this.ctx = null;

		ctx.sendUpstream(e);
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {

		log.info("Received {}", e);

		ctx.sendUpstream(e);
	}

	@Subscribe
	public void onAmqpMessageReceived(byte[] messageBytes) {

		if (ctx == null) {
			log.warn("ctx is null");
			return;
		}

		if (ctx.getChannel() == null) {
			log.warn("channel is null");
			return;
		}

		if (log.isInfoEnabled()) {
			log.debug("{} => {}", ctx.getChannel(), new String(messageBytes));
		}

		ctx.sendDownstream(
				new DownstreamMessageEvent(ctx.getChannel(), new DefaultChannelFuture(ctx.getChannel(), false),
						new TextWebSocketFrame(new String(messageBytes)), ctx.getChannel().getRemoteAddress()
				)
		);
	}
}
