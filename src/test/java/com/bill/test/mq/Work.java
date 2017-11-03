package com.bill.test.mq;

import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;

public class Work {
	private static final Logger logger=LoggerFactory.getLogger(Work.class);
	// 队列名称
	private final static String QUEUE_NAME = "workqueue";

	public static void main(String[] argv) throws java.io.IOException, java.lang.InterruptedException, TimeoutException {
		// 区分不同工作进程的输出
		int hashCode = Work.class.hashCode();
		// 创建连接和频道
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.1.129");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// 声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(hashCode + " [*] Waiting for messages. To exit press CTRL+C");
		Consumer consumer = new DefaultConsumer(channel);
		// 指定消费队列
		boolean ack = false; // 打开应答机制
		channel.basicConsume(QUEUE_NAME, ack, consumer);
		while (true) {
//			Delivery delivery = consumer.;
//			String message = new String(delivery.getBody());
//
//			System.out.println(hashCode + " [x] Received '" + message + "'");
//			doWork(message);
//			System.out.println(hashCode + " [x] Done");
//			// 发送应答
//			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

		}

	}

	/**
	 * 每个点耗时1s
	 * 
	 * @param task
	 * @throws InterruptedException
	 */
	private static void doWork(String task) throws InterruptedException {
		logger.info("***************接收到的消息："+task);
		for (char ch : task.toCharArray()) {
			if (ch == '.')
				Thread.sleep(1000);
		}
	}
}
