package com.bill.test.mq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

@Service
public class Receive {
	private static final Logger logger=LoggerFactory.getLogger(Receive.class);
	private final static String QUEUE_NAME = "hello";
	
	
	public String receive() throws IOException, TimeoutException {
		String consumMessage="";
		
		ConnectionFactory conn=new ConnectionFactory();
		conn.setHost("localhost");
		
		Connection connection=conn.newConnection();
		Channel channel=connection.createChannel();
		
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		
		Consumer consumer=new DefaultConsumer(channel) {
			/**监听mqserver localhost的queue hello
			 * 可以延伸相关业务：报警等
			 * producer端：处理完成某种业务后发出消息
			 * consumer端：接收到后处理预先准备好的业务逻辑**/
			 @Override
		      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
		          throws IOException {
		        String message = new String(body, "UTF-8");
		        logger.info("接收到的消息："+message);
		      }
		};
		channel.basicConsume(QUEUE_NAME, true,consumer);
		
		return "消费成功";
	}
}
