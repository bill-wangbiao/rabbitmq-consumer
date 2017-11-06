package com.bill.test.mq;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 消息路由consumer： 1、消息路由
 * 
 * @author sara
 *
 */
public class ReceiveLogsDirect2 {
	// 交换器名称
	private static final String EXCHANGE_NAME = "direct_logs";
	// 路由关键字
	private static final String[] routingKeys = new String[] { "error" };

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.1.129");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// 声明交换器
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		// 获取匿名队列名称
		String queueName = channel.queueDeclare().getQueue();
		// 根据路由关键字进行多重绑定
		for (String severity : routingKeys) {
			channel.queueBind(queueName, EXCHANGE_NAME, severity);
			System.out.println("ReceiveLogsDirect1 exchange:" + EXCHANGE_NAME + ", queue:" + queueName
					+ ", BindRoutingKey:" + severity);
		}
		System.out.println("ReceiveLogsDirect1 [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
}
