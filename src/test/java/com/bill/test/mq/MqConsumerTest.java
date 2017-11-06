package com.bill.test.mq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
/**
 * 工作队列consumer：
 * 1、持久化
 * 2、匿名交换器
 * 3、消息确认
 * 4、负载均衡
 * @author sara
 *
 */
public class MqConsumerTest {
	private static final Logger logger = LoggerFactory.getLogger(MqConsumerTest.class);
	private final static String QUEUE_NAME = "hello01";

	public static void main(String[] argv) throws Exception {
		int hashCode = MqConsumerTest.class.hashCode();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.1.129");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		boolean durable=true;
		channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		/** 每次从队列中获取数量 **/
		int prefetchCount = 1;
		channel.basicQos(prefetchCount);
		

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				try {
					String message = new String(body, "UTF-8");
					System.out.println(" *****Received '" + message + "'");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					logger.error("编码异常！");
				} finally {
					System.out.println(" *****Done");
					logger.info("发送应答！");
					/**发送应答**/
					channel.basicAck(envelope.getDeliveryTag(), false);
				}

			}
		};
		boolean ack = false; // 打开应答机制
		channel.basicConsume(QUEUE_NAME, ack, consumer);
	}

	/**
	 * 每个点耗时1s
	 * 
	 * @param task
	 * @throws InterruptedException
	 */
	private static void doWork(String task) throws InterruptedException {
		logger.info("*****************接收到的消息：" + task);
		for (char ch : task.toCharArray()) {
			if (ch == '.')
				Thread.sleep(1000);
		}
	}
}
