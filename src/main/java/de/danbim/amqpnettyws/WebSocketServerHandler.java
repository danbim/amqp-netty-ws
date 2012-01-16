/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package de.danbim.amqpnettyws;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.*;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.CharsetUtil;

import static com.google.common.base.Throwables.propagate;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handles handshakes and messages
 */
public class WebSocketServerHandler extends SimpleChannelUpstreamHandler {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandler.class);

	private static final String WEBSOCKET_PATH = "/websocket";

	private WebSocketServerHandshaker handshaker;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, e);
		}
	}

	@Override
	public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {

		if (e instanceof MessageEvent) {
			messageReceived(ctx, (MessageEvent) e);
		}

		// swallow all other events to fire them according to the websocket channel state
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
		// Allow only GET methods.
		if (req.getMethod() != GET) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
			return;
		}

		// Send the demo page and favicon.ico
		if (req.getUri().equals("/")) {
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);

			ChannelBuffer content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));

			res.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
			setContentLength(res, content.readableBytes());

			res.setContent(content);
			sendHttpResponse(ctx, req, res);
			return;
		} else if (req.getUri().equals("/favicon.ico")) {
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);
			sendHttpResponse(ctx, req, res);
			return;
		}

		// Handshake
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				this.getWebSocketLocation(req), null, false
		);
		this.handshaker = wsFactory.newHandshaker(req);
		if (this.handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {
			try {
				this.handshaker.performOpeningHandshake(ctx.getChannel(), req);
				ctx.sendUpstream(new UpstreamChannelStateEvent(
						ctx.getChannel(),
						ChannelState.CONNECTED,
						ctx.getChannel().getRemoteAddress()
				)
				);
			} catch (Exception e) {
				throw propagate(e);
			}
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, MessageEvent e) {

		WebSocketFrame frame = (WebSocketFrame) e.getMessage();

		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {

			this.handshaker.performClosingHandshake(ctx.getChannel(), (CloseWebSocketFrame) frame);
			ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.CONNECTED, null));
			return;

		} else if (frame instanceof PingWebSocketFrame) {

			ctx.sendDownstream(
					new DownstreamMessageEvent(
							ctx.getChannel(),
							new DefaultChannelFuture(ctx.getChannel(), true),
							new PongWebSocketFrame(frame.getBinaryData()),
							ctx.getChannel().getRemoteAddress()
					)
			);
			return;

		} else if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
					.getName()
			)
			);
		}

		ctx.sendUpstream(e);
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	private String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
	}
}