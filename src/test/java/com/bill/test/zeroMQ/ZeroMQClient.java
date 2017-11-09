package com.bill.test.zeroMQ;

import java.io.UnsupportedEncodingException;

import org.zeromq.ZMQ;
/**
 * Request/Subscribe
 * @author sara
 *
 */
public class ZeroMQClient {

	public static void main(String[] args) {
		for(int j=0;j<5;j++) {
			new Thread(
					new Runnable() {

						@Override
						public void run() {
							ZMQ.Context context=ZMQ.context(1);
							ZMQ.Socket socket=context.socket(ZMQ.REQ);
							/**与response建立连接**/
							socket.connect("tcp://127.0.0.1:5555");
							
							long start=System.currentTimeMillis();
							for(int i=0;i<10000;i++) {
								String request="hello+"+i;
								/**向response端发送数据**/
								socket.send(request.getBytes());
								/**接收response端返回的数据**/
								byte[] response=socket.recv();
								try {
									System.out.println("response端响应的数据："+new String(response,"utf-8"));
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}
							long end=System.currentTimeMillis();
							System.out.println("*********执行耗时："+(end-start)/1000);
						}
						
					}
					).start();;
		}

	}

}
