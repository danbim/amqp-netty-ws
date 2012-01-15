package de.danbim.amqpnettyws;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

public class AmqpConsumerHandler implements ChannelUpstreamHandler {

	private static final Logger log = LoggerFactory.getLogger(AmqpConsumerHandler.class);

	private final EventBus eventBus;

	private ChannelHandlerContext ctx;

	public AmqpConsumerHandler(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
		if (e instanceof ChannelStateEvent) {
			ChannelStateEvent event = (ChannelStateEvent) e;
			switch (event.getState()) {
				case CONNECTED:
					if (event.getValue() == null) {
						this.eventBus.unregister(this);
						this.ctx = null;
					} else {
						this.ctx = ctx;
						this.eventBus.register(this);
					}
			}
		}
		ctx.sendUpstream(e);
	}

	@Subscribe
	public void messageEvent(byte[] messageBytes) {

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

		Channels.write(ctx.getChannel(), wrappedBuffer(messageBytes));
	}
}
